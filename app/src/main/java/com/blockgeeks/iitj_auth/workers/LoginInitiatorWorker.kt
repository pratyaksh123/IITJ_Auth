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
            Toast.makeText(applicationContext, "Connected!", Toast.LENGTH_LONG).show()
            return Result.success()
        } else if (response?.code == 204) {
            Toast.makeText(applicationContext, "Already Connected!", Toast.LENGTH_LONG).show()
            return Result.success()
        } else {
            Toast.makeText(applicationContext, "Worker Failed!", Toast.LENGTH_LONG).show()
            return Result.failure()
        }
    }
}