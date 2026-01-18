package com.example.lapordes.data.model

data class Complaint(
    val uid: String = "",
    val title: String = "",
    val category: String = "",
    val priority: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val lat: String = "",
    val lng: String = "",
    val user: User = User()
)
