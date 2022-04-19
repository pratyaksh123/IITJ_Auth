package com.blockgeeks.iitj_auth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import com.blockgeeks.iitj_auth.utils.checkWalledGardenConnectionAsync
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


const val TAG = "BroadCastReceiver"

//class WifiConnectionStateReceiver : BroadcastReceiver() {
//    override fun onReceive(p0: Context?, intent: Intent?) {
//        if (intent?.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
//            val networkInfo: NetworkInfo? =
//                intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
//            Log.i(TAG, networkInfo?.state.toString())
//            if (networkInfo?.isConnected == true) {
//                Toast.makeText(p0, "Connected!", Toast.LENGTH_LONG).show()
//
//            } else if (networkInfo?.state == NetworkInfo.State.DISCONNECTED) {
//                Log.i(TAG, "Disconnected")
//                Toast.makeText(p0, "Disconnected!", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}