package com.example.lapordes.data.repository

import android.util.Log
import com.example.lapordes.data.model.User
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue

class AuthRepository {
    private val auth = FirebaseHelper.auth()
    private val firestore = FirebaseHelper.firestore()

    fun register(
        email: String,
        password: String,
        callback: (ResultState<String>) -> Unit
    ) {
        callback(ResultState.Loading)

        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val user = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "password" to password,
                        "admin" to false,
                        "created_at" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            callback(ResultState.Success("Daftar akun berhasil"))
                        }
                        .addOnFailureListener {
                            callback(ResultState.Error(it.message!!))
                        }
                }
                .addOnFailureListener {
                    callback(ResultState.Error(it.message!!))
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message.toString()))
        }
    }

    fun login(
        email: String,
        password: String,
        callback: (ResultState<User>) -> Unit
    ) {
        callback(ResultState.Loading)

        firestore.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    callback(ResultState.Error("Data pengguna tidak ditemukan"))
                    return@addOnSuccessListener
                }

                val document = snap.documents.firstOrNull()
                val user = document?.toObject(User::class.java)

                if (user == null) {
                    callback(ResultState.Error("Gagal membaca data pengguna"))
                    return@addOnSuccessListener
                }

                callback(ResultState.Success(user))
            }
            .addOnFailureListener {
                callback(ResultState.Error(it.message!!))
            }
    }
}