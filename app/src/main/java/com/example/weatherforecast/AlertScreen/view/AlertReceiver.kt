package com.example.weatherforecast.AlertScreen.view

import android.Manifest
import android.app.Dialog
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherforecast.databinding.NotificationLayoutBinding


class AlertReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {

        val cityName = intent.getStringExtra("cityName")
        val alert = intent.getStringExtra("alert_description")
        //val country = intent.getStringExtra("country")

        if (cityName != null && alert != null ) {
            showNotification(context, cityName, alert)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showNotification(context: Context, cityName: String , alert: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationBuilder = createNotification(context, cityName , alert )

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NOTIFICATION_PERM, notificationBuilder.build())
    }
}