package com.example.lapordes.data.repository

import android.content.Context
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Comment
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import java.util.UUID

class CommentRepository {
    private val firestore = FirebaseHelper.firestore()

    fun create(
        comment: String,
        complaint_uid: String,
        context: Context,
        callback: (ResultState<String>) -> Unit
    ) {
        callback(ResultState.Loading)

        try {
            val uid = UUID.randomUUID().toString()
            val user = UserPref(context).get()

            val complaint = hashMapOf(
                "uid" to uid,
                "comment" to comment,
                "complaint_uid" to complaint_uid,
                "created_at" to FieldValue.serverTimestamp(),
                "user" to user
            )

            firestore.collection("comments")
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

    fun get(complaint_uid: String, callback: (ResultState<List<Comment>>) -> Unit) {
        callback(ResultState.Loading)

        try {
            listener?.remove()

            firestore.collection("comments")
                .whereEqualTo("complaint_uid", complaint_uid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        callback(ResultState.Error(error.message!!))
                        return@addSnapshotListener
                    }

                    val list = value?.documents?.mapNotNull {
                        it.toObject(Comment::class.java)
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