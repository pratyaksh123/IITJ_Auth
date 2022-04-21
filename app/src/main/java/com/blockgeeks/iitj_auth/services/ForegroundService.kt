package com.blockgeeks.iitj_auth.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.activities.MainActivity
import com.blockgeeks.iitj_auth.utils.authenticate
import com.blockgeeks.iitj_auth.workers.LoginInitiatorWorker
import io.sentry.Sentry
import java.util.concurrent.TimeUnit


const val TAG = "ForegroundService"

class MyForegroundService : Service() {
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: NetworkCallback = object : NetworkCallback() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onAvailable(network: Network) {
            // network available
            connectivityManager.bindProcessToNetwork(network)
            Log.i(TAG, "Captive Portal detected")
            Toast.makeText(applicationContext, "Logging in..", Toast.LENGTH_LONG).show()
            val response = authenticate(applicationContext)
            if (response?.code == 200) {
                // Dismiss the captive portal using the Captive portal API
                Log.i(TAG, "Connected!")
                Toast.makeText(applicationContext, "Connected!", Toast.LENGTH_LONG).show()
            } else if (response?.code == 204) {
                // Already Authenticated
                Log.i(TAG, "Already Connected!")
            }

            // Use WorkManager to schedule work
            val periodicLoginWork = PeriodicWorkRequest.Builder(
                LoginInitiatorWorker::class.java, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "periodicLoginWorkName",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicLoginWork
            )
        }


        override fun onLost(network: Network) {
            Log.e(TAG, "Lost network")
        }
    }


    override fun onCreate() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        createNotificationChannel()
        var notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("IIT-J Auth")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Automating Authentication...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        startForeground(1001, notification.build())
        connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        connectivityManager.registerNetworkCallback(
            builder.build(),
            networkCallback
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
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
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Sentry.captureException(e)
            e.printStackTrace()
        }
    }
}

//2022-04-20 02:11:23.740 30618-30880/com.blockgeeks.iitj_auth E/ForegroundService: The default network changed link properties: {InterfaceName: wlan0 LinkAddresses: [fe80::20a:f5ff:fe89:89ff/64,172.30.13.233/19,]  Routes: [fe80::/64 -> :: wlan0,172.30.0.0/19 -> 0.0.0.0 wlan0,0.0.0.0/0 -> 172.30.0.1 wlan0,] DnsAddresses: [172.16.100.4,172.16.100.3,8.8.8.8,] UsePrivateDns: false PrivateDnsServerName: null Domains: null MTU: 0 TcpBufferSizes: 524288,2097152,4194304,262144,524288,1048576}