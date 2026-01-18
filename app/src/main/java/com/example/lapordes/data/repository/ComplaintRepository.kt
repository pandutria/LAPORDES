package com.example.lapordes.data.repository

import android.content.Context
import com.bumptech.glide.Priority
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.model.User
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import java.util.UUID
import kotlin.random.Random

class ComplaintRepository {
    private val firestore = FirebaseHelper.firestore()

    fun create(
        title: String,
        category: String,
        priority: String,
        description: String,
        imageUrl: String,
        lat: String,
        lng: String,
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
}