package com.example.pushnotification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat


class ForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.toString() -> start()
            Action.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun start() {
       var notification= NotificationCompat.Builder(this, "channel_running")
           .setSmallIcon(R.drawable.chat)
            .setContentTitle("Foreground service")
           .setContentText("Hi this is foreground service")
           .build()
        startForeground(1, notification)
    }
    enum class Action {
        START, STOP
    }

}