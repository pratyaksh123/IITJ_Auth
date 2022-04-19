package com.blockgeeks.iitj_auth.activities

import android.content.Intent
import android.os.Bundle
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
            ContextCompat.startForegroundService(this, foregroundServiceIntent)
        }
        logoutButton.setOnClickListener {
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            stopService(serviceIntent)
        }
    }
}