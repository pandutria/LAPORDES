package com.example.lapordes.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseHelper {
    fun auth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    fun firestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}