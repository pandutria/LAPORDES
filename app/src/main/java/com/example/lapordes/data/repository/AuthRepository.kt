package com.example.lapordes.data.repository

import android.content.Context
import android.util.Log
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.User
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.utils.FirebaseHelper
import com.google.firebase.Timestamp
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

    fun me(context: Context, callback: (ResultState<User>) -> Unit) {
        callback(ResultState.Loading)

        try {
            val user = UserPref(context).get()

            firestore.collection("users")
                .whereEqualTo("uid", user!!.uid)
                .get()
                .addOnSuccessListener { snap ->
                    val document = snap.documents.firstOrNull()
                    val userData = document!!.toObject(User::class.java)

                    if (userData == null) {
                        callback(ResultState.Error("Gagal membaca data pengguna"))
                        return@addOnSuccessListener
                    }

                    callback(ResultState.Success(userData))
                }
                .addOnFailureListener {
                    callback(ResultState.Error(it.message!!))
                }
        } catch (e: Exception) {
            callback(ResultState.Error(e.message!!))
        }
    }

    fun updateProfile(
        context: Context,
        email: String,
        password: String,
        image: String,
        fullname: String,
        username: String,
        phone: String,
        nik: String,
        gender: String,
        birth: Timestamp,
        callback: (ResultState<User>) -> Unit
    ) {
        callback(ResultState.Loading)

        try {
            val userPref = UserPref(context)
            val user = userPref.get() ?: run {
                callback(ResultState.Error("User tidak ditemukan"))
                return
            }

            val userUpdate = mapOf(
                "email" to email,
                "password" to password,
                "image" to image,
                "fullname" to fullname,
                "username" to username,
                "phone" to phone,
                "nik" to nik,
                "gender" to gender,
                "birth" to birth
            )

            firestore.collection("users")
                .document(user.uid)
                .update(userUpdate)
                .addOnSuccessListener {
                    val updatedUser = user.copy(
                        email = email,
                        password = password,
                        image = image,
                        fullname = fullname,
                        username = username,
                        phone = phone,
                        nik = nik,
                        gender = gender,
                        birth = birth
                    )

                    userPref.save(updatedUser)

                    callback(ResultState.Success(updatedUser))
                }
                .addOnFailureListener {
                    callback(ResultState.Error(it.message ?: "Gagal update profile"))
                }

        } catch (e: Exception) {
            callback(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

}