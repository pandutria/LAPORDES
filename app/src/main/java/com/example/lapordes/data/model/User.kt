package com.example.lapordes.data.model

data class User (
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val admin: Boolean = false
)