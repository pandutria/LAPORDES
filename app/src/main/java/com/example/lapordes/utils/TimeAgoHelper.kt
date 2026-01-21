package com.example.lapordes.utils

import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

object TimeAgoHelper {

    fun getTimeAgo(timestamp: Timestamp?): String {
        if (timestamp == null) return "-"

        val now = System.currentTimeMillis()
        val time = timestamp.toDate().time

        val diff = now - time

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) ->
                "Baru saja"

            diff < TimeUnit.HOURS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toMinutes(diff)} Menit yang lalu"

            diff < TimeUnit.DAYS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toHours(diff)} Jam yang lalu"

            diff < TimeUnit.DAYS.toMillis(7) ->
                "${TimeUnit.MILLISECONDS.toDays(diff)} Hari yang lalu"

            else ->
                formatDate(timestamp)
        }
    }

    private fun formatDate(timestamp: Timestamp): String {
        val sdf = java.text.SimpleDateFormat(
            "dd MMM yyyy",
            java.util.Locale("id", "ID")
        )
        return sdf.format(timestamp.toDate())
    }
}