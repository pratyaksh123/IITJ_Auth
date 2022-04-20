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
        Log.i(TAG, "Authenticating..")
        val response = authenticate(applicationContext)
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