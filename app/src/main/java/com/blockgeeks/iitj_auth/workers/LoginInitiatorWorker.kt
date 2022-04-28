package com.blockgeeks.iitj_auth.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.activities.MainActivity
import com.blockgeeks.iitj_auth.utils.getMasterKey
import com.blockgeeks.iitj_auth.utils.refreshAuth
import io.sentry.Sentry

const val TAG = "LoginInitiatorWorker"

class LoginInitiatorWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    var foregroundServiceId: Int = 1001
    var notificationChannelIdForHelperService = "1000"
    override suspend fun doWork(): Result {
        val sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "initial_setup",
            getMasterKey(applicationContext),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        val sessionUrl = sharedPreferences.getString("session_url", null)
        // session url null check
        if (sessionUrl != null) {
            val response = refreshAuth(sessionUrl)
            if (response == "Success") {
                // usual stuff
                Log.i(TAG, "Refresh Successful!")
//                updateNotification("Authentication Success!", "Enjoy interruption free internet")
                return Result.success()
            } else {
                // stop the worker if connection state changes which will give response != 200 in refreshAuth.kt
                WorkManager.getInstance(applicationContext)
                    .cancelUniqueWork("periodicLoginWorkName")
                Log.i(TAG, "WorkerStopped")
//                updateNotification("Standby mode","Session url expired or not on IITJ network.")
                return Result.success()
            }
        } else {
            // Probably an unreachable state
            Log.i(TAG, "session url null")
            WorkManager.getInstance(applicationContext)
                .cancelUniqueWork("periodicLoginWorkName")
            Sentry.captureMessage("session url null")
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