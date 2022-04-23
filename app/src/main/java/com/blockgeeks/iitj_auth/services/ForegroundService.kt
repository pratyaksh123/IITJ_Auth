package com.blockgeeks.iitj_auth.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private var username: String? = null
    private var password: String? = null
    var foregroundServiceId: Int = 1001
    var notificationChannelIdForHelperService = "1000"

    private var networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Captive portal Detected
            // First check if username and password are non null
            if (username == null || password == null) {
                Log.i(TAG, "username and password are null")
                Toast.makeText(
                    applicationContext,
                    "Username or Password not set!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                connectivityManager.bindProcessToNetwork(network)
                Log.i(TAG, "Captive Portal detected")
                Toast.makeText(applicationContext, "Logging in..", Toast.LENGTH_LONG).show()
                val response = authenticate(applicationContext, username!!, password!!)
                if (response?.code == 200) {
                    // Dismiss the captive portal using the Captive portal API
                    Log.i(TAG, "Connected!")
                    updateNotification("Login Successful! ✅")
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

        }

        override fun onLost(network: Network) {
            Log.e(TAG, "Lost network")
        }
    }


    override fun onCreate() {
        val sharedPreferences =
            applicationContext.getSharedPreferences("initial_setup", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username1", null)
        password = sharedPreferences.getString("password1", null)
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        createNotificationChannel()
        val foregroundServiceNotification: Notification = NotificationCompat.Builder(
            applicationContext,
            notificationChannelIdForHelperService
        )
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Service is up and running 😉")
            .setContentText("Status: Awaiting Update")
            .setContentIntent(pendingIntent)
            .setGroup("helperServiceGroup")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Status: Awaiting Update")
                    .bigText(getString(R.string.notification_info_text))
            )
            .build()

        startForeground(foregroundServiceId, foregroundServiceNotification)
        connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val builder =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)

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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                notificationChannelIdForHelperService,
                name,
                importance
            ).apply {}

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(message: String?) {
        var notificationMessage = message
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        val mainActivityPendingIntent = PendingIntent.getActivity(
            applicationContext,
            300,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if (notificationMessage == null || notificationMessage == "") {
            notificationMessage = "Unknown"
        }
        val foregroundServiceNotification: Notification = NotificationCompat.Builder(
            applicationContext,
            notificationChannelIdForHelperService
        )
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Service is up and running 😉")
            .setContentText("Status: $notificationMessage")
            .setContentIntent(mainActivityPendingIntent)
            .setGroup("helperServiceGroup")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Status: $notificationMessage")
                    .bigText(getString(R.string.notification_info_text))
            ).build()
        NotificationManagerCompat.from(applicationContext)
            .notify(foregroundServiceId, foregroundServiceNotification)
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
// <a href="https://www.flaticon.com/free-icons/shield" title="shield icons">Shield icons created by Freepik - Flaticon</a>