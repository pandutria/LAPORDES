package com.example.lapordes.data.model

import com.google.firebase.Timestamp

data class User (
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val image: String = "",
    val fullname: String = "",
    val username: String = "",
    val phone: String = "",
    val nik: String = "",
    val gender: String = "",
    val birth: Timestamp? = null,
    val admin: Boolean = false
)