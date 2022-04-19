package com.blockgeeks.iitj_auth.workers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.blockgeeks.iitj_auth.utils.authenticate

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
            Log.e(TAG, "Worker Failed!, Response - ${response}")
            return Result.failure()
        }
    }
}