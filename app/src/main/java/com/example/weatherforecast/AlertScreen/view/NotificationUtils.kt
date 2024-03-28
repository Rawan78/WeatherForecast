package com.example.weatherforecast.AlertScreen.view


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.SharedPrefs



private const val CHANNEL_ID: String = "CHANNEL_ID"
const val NOTIFICATION_PERM = 1023

@RequiresApi(Build.VERSION_CODES.S)
fun createNotification(context: Context, cityName: String , alert: String): NotificationCompat.Builder {



    // Create a notification channel if targeting Android Oreo (API level 26) or higher
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Weather Channel"
        val descriptionText = "Channel for weather notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Create the notification builder
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.threedn)
        .setContentTitle("$cityName")
        .setContentText("$alert")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
}