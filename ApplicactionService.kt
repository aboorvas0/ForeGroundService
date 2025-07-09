package com.example.pushnotification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class ApplicactionService:Application() {


    override fun onCreate() {
        super.onCreate()
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ // upto 8+
           val channel=NotificationChannel("channel_running",
               "My Foreground Channel",
               NotificationManager.IMPORTANCE_HIGH)
           val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
           manager.createNotificationChannel(channel)
       }


    }
}