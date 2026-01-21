package com.example.lapordes.data.model

import com.google.firebase.Timestamp

data class Notification (
    val uid: String = "",
    val user_uid: String = "",
    val created_at: Timestamp? = null,
    val complaint: Complaint = Complaint()
)