package com.blockgeeks.iitj_auth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.blockgeeks.iitj_auth.utils.CheckWalledGardenConnection
import com.blockgeeks.iitj_auth.utils.authenticate
import com.blockgeeks.iitj_auth.utils.isWalledGardenConnection
import com.blockgeeks.iitj_auth.workers.LoginInitiatorWorker
import java.util.concurrent.TimeUnit


const val TAG = "BroadCastReceiver"

class WifiConnectionStateReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent?.action) {
            val networkInfo: NetworkInfo? =
                intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
//            Log.i(TAG, networkInfo?.state.toString())
            if (networkInfo?.state == NetworkInfo.State.CONNECTED) {
                val task = CheckWalledGardenConnection()
                val result = task.execute().get() // UI blocking
                Log.i(TAG, "WalledGardenCheck - $result")
                if (result) {
                    Log.i(TAG, "Captive Portal detected")
                    Toast.makeText(p0, "Logging in..", Toast.LENGTH_SHORT).show()

                    // Run auth one time
                    val response = authenticate(p0!!)
                    // Schedule periodic work

//                    val periodicLoginWork = PeriodicWorkRequest.Builder(
//                        LoginInitiatorWorker::class.java, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
//                    ).build()
//                    WorkManager.getInstance(p0!!).enqueueUniquePeriodicWork(
//                        "periodicLoginWorkName",
//                        ExistingPeriodicWorkPolicy.KEEP,
//                        periodicLoginWork
//                    )
                } else {
                    Log.i(TAG, "Connected!")
                    Toast.makeText(p0, "Connected!", Toast.LENGTH_LONG).show()
                    // TODO: Update notifications
                }
            } else if (networkInfo?.state == NetworkInfo.State.DISCONNECTED) {
                Log.i(TAG, "Disconnected")
                Toast.makeText(p0, "Disconnected!", Toast.LENGTH_LONG).show()

            }
        }
    }


}