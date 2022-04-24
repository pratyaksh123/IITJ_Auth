package com.blockgeeks.iitj_auth.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.activities.MainActivity
import com.blockgeeks.iitj_auth.utils.authenticate
import io.sentry.Sentry

const val TAG = "LoginInitiatorWorker"

class LoginInitiatorWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    var foregroundServiceId: Int = 1001
    var notificationChannelIdForHelperService = "1000"
    override suspend fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences("initial_setup", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username1", null)
        val password = sharedPreferences.getString("password1", null)
        // username and password null check
        if (username == null || password == null) {
            Log.e(TAG, "Username or Password not set!")
            updateNotification("Login Failed: Username or Password not set!")
            return Result.failure()
        }

        Log.i(TAG, "Authenticating..")
        val response = authenticate(applicationContext, username, password)

        if (response == "Success") {
            // Dismiss the captive portal using the Captive portal API
            Log.i(com.blockgeeks.iitj_auth.services.TAG, "Connected!")
            updateNotification("Login Successful! ✅")
            return Result.success()
        } else if (response == "Already Connected") {
            // Already Authenticated
            Log.i(com.blockgeeks.iitj_auth.services.TAG, "Already Connected!")
            return Result.success()
        } else if (response == "Failed") {
            Log.i(com.blockgeeks.iitj_auth.services.TAG, "Authentication Failed!")
            updateNotification(
                "Login Failed! ❌",
                "Please check if your username and password are correct."
            )
            return Result.failure()
        } else if (response == "Unknown") {
            Log.i(TAG, "Authentication Failed!")
            Sentry.captureMessage("Worker Failed!, Response - $response")
            return Result.failure()
        } else {
            Log.i(TAG, "null Response")
            Sentry.captureMessage("Worker Failed!, Response - $response")
            return Result.failure()
        }
    }

    private fun updateNotification(
        message: String?,
        description: String = applicationContext.getString(R.string.notification_info_text)
    ) {
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
                    .bigText(description)
            ).build()
        NotificationManagerCompat.from(applicationContext)
            .notify(foregroundServiceId, foregroundServiceNotification)
    }


}