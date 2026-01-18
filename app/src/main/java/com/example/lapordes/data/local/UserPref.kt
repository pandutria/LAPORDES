package com.example.lapordes.data.local

import android.content.Context
import com.example.lapordes.data.model.User
import com.google.gson.Gson

class UserPref(context: Context) {
    private val pref = "user"
    private val key = "user"
    private val gson = Gson()

    private val shared = context.getSharedPreferences(pref, Context.MODE_PRIVATE)

    fun save(user: User) {
        shared.edit().putString(key, gson.toJson(user)).apply()
    }

    fun get(): User? {
        val json = shared.getString(key, null)
        return json?.let { gson.fromJson(it, User::class.java) }
    }

    fun remove() {
        shared.edit().remove(key).apply()
    }
}