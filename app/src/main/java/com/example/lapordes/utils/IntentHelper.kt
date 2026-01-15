package com.example.lapordes.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.lapordes.R

object IntentHelper {
    fun navigate(activity: Activity, destination: Class<out Activity>, extras: Bundle? = null) {
        val intent = Intent(activity, destination)
        if (extras != null) intent.putExtras(extras)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.zoom_fade_in, R.anim.zoom_fade_out)
    }
    fun finish(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(R.anim.zoom_fade_in, R.anim.zoom_fade_out)
    }
}