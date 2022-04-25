package com.blockgeeks.iitj_auth.services

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import com.blockgeeks.iitj_auth.utils.getMasterKey


@RequiresApi(Build.VERSION_CODES.N)
class TileService : TileService() {
    private lateinit var authTile: Tile

    override fun onClick() {
        Log.i(TAG, "onClick: " + "called")
        super.onClick()
        val sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "initial_setup",
            getMasterKey(applicationContext),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        val editor = sharedPreferences.edit()

        if (authTile.state == Tile.STATE_ACTIVE) {
            // stop the service
            applicationContext.stopService(
                Intent(
                    applicationContext,
                    MyForegroundService::class.java
                )
            )
            authTile.state = Tile.STATE_INACTIVE
            authTile.updateTile()
        } else if (authTile.state == Tile.STATE_INACTIVE) {
            if (sharedPreferences.getBoolean("initial_setup", false)) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, MyForegroundService::class.java)
                )
                authTile.state = Tile.STATE_ACTIVE
                authTile.updateTile()
            } else {
                Toast.makeText(this, "Complete the initial setup first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTileAdded() {
        Log.i(TAG, "onTileAdded")
        super.onTileAdded()
        authTile = qsTile
        if (isServiceRunningInForeground(applicationContext, MyForegroundService::class.java)) {
            authTile.state = Tile.STATE_ACTIVE
        } else {
            authTile.state = Tile.STATE_INACTIVE
        }
        authTile.updateTile()
    }

    override fun onStartListening() {
        Log.i(TAG, "onStartListening")
        super.onStartListening()
        // Called when the Tile becomes visible
        authTile = qsTile
        if (isServiceRunningInForeground(applicationContext, MyForegroundService::class.java)) {
            authTile.state = Tile.STATE_ACTIVE
        } else {
            authTile.state = Tile.STATE_INACTIVE
        }
        authTile.updateTile()
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