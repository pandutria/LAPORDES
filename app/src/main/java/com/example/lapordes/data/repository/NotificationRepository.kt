package com.example.lapordes.data.repository

import android.content.Context
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Comment
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.model.Notification
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

class NotificationRepository {
    private val firestore = FirebaseHelper.firestore()

    fun createNotif(
        complaint_uid: String,
        user_uid: String,
        callback: (ResultState<String>) -> Unit
    ) {
        callback(ResultState.Loading)

        try {
            var complaint: Complaint?

            firestore.collection("complaints")
                .document(complaint_uid)
                .get()
                .addOnSuccessListener { snap ->
                    if (!snap.exists()) {
                        callback(ResultState.Error("Data tidak ditemukan"))
                        return@addOnSuccessListener
                    }

                    complaint = snap.toObject(Complaint::class.java)

                    val uid = UUID.randomUUID().toString()
                    val notif = hashMapOf(
                        "uid" to uid,
                        "user_uid" to user_uid,
                        "created_at" to FieldValue.serverTimestamp(),
                        "complaint" to complaint
                    )

                    firestore.collection("notifications")
                        .document(uid)
                        .set(notif)
                        .addOnSuccessListener {
                            callback(ResultState.Success("Berhasil mengirim notif"))
                        }
                        .addOnFailureListener {
                            callback(ResultState.Error(it.message!!))
                        }
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message!!))
        }
    }

    private var listener: ListenerRegistration? = null

    fun getNotif(user_uid: String, callback: (ResultState<List<Notification>>) -> Unit) {
        callback(ResultState.Loading)

        try {
            listener?.remove()
            firestore.collection("notifications")
                .whereEqualTo("user_uid", user_uid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        callback(ResultState.Error(error.message!!))
                        return@addSnapshotListener
                    }

                    val list = value?.documents?.mapNotNull {
                        it.toObject(Notification::class.java)
                    }

                    callback(ResultState.Success(list!!))
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message!!))
        }
    }

    fun removeListener() {
        listener?.remove()
        listener = null
    }
}