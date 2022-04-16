package com.blockgeeks.iitj_auth.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.activities.MainActivity
import com.blockgeeks.iitj_auth.receivers.WifiConnectionStateReceiver

const val TAG = "ForegroundService"

class MyForegroundService : Service() {
    private val wifiConnectionStateReceiver = WifiConnectionStateReceiver()
    override fun onCreate() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        createNotificationChannel()
        var notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("IIT-J Auth")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Running in background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        startForeground(1001, notification.build())
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // All the business logic goes here

        // Setting up broadcast receiver
        val intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(wifiConnectionStateReceiver, intentFilter)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "IIT-J Auth"
            val descriptionText = "Testing 123"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        // Cancel WorkManager and then unregister the receiver
        try {
            unregisterReceiver(wifiConnectionStateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}