package com.blockgeeks.iitj_auth.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blockgeeks.iitj_auth.utils.authenticate
import io.sentry.Sentry


const val TAG = "LoginInitiatorWorker"

class LoginInitiatorWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences("initial_setup", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username1", null)
        val password = sharedPreferences.getString("password1", null)
        // username and password null check
        if (username == null || password == null){
            Log.e(TAG, "Username or Password not set!")
            return Result.failure()
        }

        Log.i(TAG, "Authenticating..")
        val response = authenticate(applicationContext, username, password)
        if (response?.code == 200) {
            Log.i(TAG, "Connected!")
            return Result.success()
        } else if (response?.code == 204) {
            Log.i(TAG, "Already Connected!")
            return Result.success()
        } else {
            Sentry.captureMessage("Worker Failed!, Response - ${response}");
            Log.e(TAG, "Worker Failed!, Response - ${response}")
            return Result.failure()
        }
    }
}