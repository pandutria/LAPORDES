package com.example.lapordes.data.model

import com.google.firebase.Timestamp

data class Comment(
    val uid: String = "",
    val comment: String = "",
    val created_at: Timestamp? = null,
    val complaint_uid: String? = "",
    val user: User = User()
)
