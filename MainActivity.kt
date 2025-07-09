package com.example.pushnotification

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


lateinit var button:Button
lateinit var stop_btn:Button

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button=findViewById(R.id.click_btn)
        stop_btn=findViewById(R.id.stop_btn)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )

        }

        button.setOnClickListener{
            Intent(applicationContext,ForegroundService::class.java).also {
                it.action=ForegroundService.Action.START.toString()
                startService(it)
            }
        }
        stop_btn.setOnClickListener{
            Intent(applicationContext,ForegroundService::class.java).also {
                it.action=ForegroundService.Action.STOP.toString()
                startService(it)
            }
        }



    }


}