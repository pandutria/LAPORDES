package com.example.lapordes.utils

import android.content.Context
import android.widget.Toast

object ToastHelper {
    fun showToast(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
}