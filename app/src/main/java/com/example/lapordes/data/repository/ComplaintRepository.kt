package com.example.lapordes.data.repository

import android.content.Context
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.model.Notification
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

class ComplaintRepository {
    private val firestore = FirebaseHelper.firestore()

    fun create(
        title: String,
        category: String,
        priority: String,
        description: String,
        imageUrl: String,
        lat: Double,
        lng: Double,
        context: Context,
        callback: (ResultState<String>) -> Unit
    ) {
        callback(ResultState.Loading)

        try {
            val uid = UUID.randomUUID().toString()
            val user = UserPref(context).get()

            val complaint = hashMapOf(
                "uid" to uid,
                "title" to title,
                "category" to category,
                "priority" to priority,
                "description" to description,
                "imageUrl" to imageUrl,
                "lat" to lat,
                "lng" to lng,
                "status" to "Proses",
                "note" to "",
                "created_at" to FieldValue.serverTimestamp(),
                "updated_at" to FieldValue.serverTimestamp(),
                "user" to user
            )

            firestore.collection("complaints")
                .document(uid)
                .set(complaint)
                .addOnSuccessListener {
                    callback(ResultState.Success("Berhasil membuat pengaduan"))
                }
                .addOnFailureListener {
                    callback(ResultState.Error(it.message!!))
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message!!))
        }
    }

    private var listener: ListenerRegistration? = null

    fun get(context: Context, callback: (ResultState<List<Complaint>>) -> Unit) {
        callback(ResultState.Loading)

        try {
            listener?.remove()

            firestore.collection("complaints")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        callback(ResultState.Error(error.message!!))
                        return@addSnapshotListener
                    }

                    val list = value?.documents?.mapNotNull {
                        it.toObject(Complaint::class.java)
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

    fun changeStatus(complaint_uid: String, status: String, note: String, callback: (ResultState<String>) -> Unit) {
        callback(ResultState.Loading)

        try {
            val complaint = mapOf(
                "status" to status,
                "note" to note,
                "updated_at" to FieldValue.serverTimestamp()
            )

            firestore.collection("complaints")
                .document(complaint_uid)
                .update(complaint)
                .addOnSuccessListener {
                    callback(ResultState.Success("Status berhasil diubah"))
                }
                .addOnFailureListener {
                    callback(ResultState.Success(it.message!!))
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message!!))
        }
    }
}