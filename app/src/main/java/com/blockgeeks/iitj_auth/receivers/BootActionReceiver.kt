package com.blockgeeks.iitj_auth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.blockgeeks.iitj_auth.services.MyForegroundService


class BootActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(serviceIntent)
            }else{
                context?.startService(serviceIntent)
            }
        }
    }
}