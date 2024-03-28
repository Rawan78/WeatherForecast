package com.example.weatherforecast.AlertScreen.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.NotificationLayoutBinding
import android.provider.Settings

class AlarmReceiver: BroadcastReceiver() {

    private var ringtone: Ringtone? = null
    private var windowManager: WindowManager? = null
    private var view: View? = null

    private  val TAG = "AlarmReceiver"

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun showAlarmOverlay(context: Context , city: String , desc: String){
        try {
            view = LayoutInflater.from(context).inflate(R.layout.notification_layout , null, false)
            val cityName = view!!.findViewById<TextView>(R.id.txtCityName)
            val alertDesc = view!!.findViewById<TextView>(R.id.txtAlertDescription)
            val btnDismiss = view!!.findViewById<Button>(R.id.btn_dismiss)
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            layoutParams.gravity = Gravity.TOP

            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager!!.addView(view , layoutParams)
            cityName.text = city
            alertDesc.text = desc

            btnDismiss.setOnClickListener {
                cancelAlarm()
            }
        }catch (e: WindowManager.BadTokenException){
            Log.i(TAG, "showAlarmOverlay: error occurred ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun cancelAlarm(){
        windowManager?.removeView(view)
        ringtone?.stop()
    }



    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onReceive(context: Context, intent: Intent) {
        val cityName = intent.getStringExtra("cityName")
        val alertDescription = intent.getStringExtra("alert_description")

        if (cityName != null && alertDescription != null) {
            showAlarmOverlay(context, cityName , alertDescription)
            playAlarmSound(context)
        }else {
            Log.e("AlarmReceiver", "Missing cityName or alertDescription in intent extras")
        }
    }

    private fun playAlarmSound(context: Context) {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context.applicationContext , ringtoneUri)

        ringtone?.play()
    }

}