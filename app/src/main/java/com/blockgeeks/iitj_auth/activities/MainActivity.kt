package com.blockgeeks.iitj_auth.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blockgeeks.iitj_auth.R
import com.blockgeeks.iitj_auth.services.MyForegroundService

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.loginButton2)
        loginButton.setOnClickListener {
            val foregroundServiceIntent = Intent(this, MyForegroundService::class.java)
            foregroundServiceIntent.putExtra("inputExtra", "Some Input")
            ContextCompat.startForegroundService(this, foregroundServiceIntent)
//            Log.i(TAG, "isForegroundServiceRunning: ${isServiceRunningInForeground(this, MyForegroundService::class.java)}")

        }
        logoutButton.setOnClickListener {
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            stopService(serviceIntent)
        }
    }

    private fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }
}