package com.example.lapordes.data.model

import com.google.firebase.Timestamp

data class Complaint(
    val uid: String = "",
    val title: String = "",
    val category: String = "",
    val priority: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val status: String = "",
    val note: String = "",
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null,
    val user: User = User()
)
