package com.example.lapordes.di

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = hashMapOf(
            "cloud_name" to "dl69pcybt"
        )

        MediaManager.init(this, config)
    }
}