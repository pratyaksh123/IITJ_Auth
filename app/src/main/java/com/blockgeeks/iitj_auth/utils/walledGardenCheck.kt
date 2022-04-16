package com.blockgeeks.iitj_auth.utils

import android.os.AsyncTask
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


const val TAG = "BroadCastReceiver"
private const val mWalledGardenUrl = "http://clients3.google.com/generate_204"
private const val WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10000

/**
 * DNS based detection techniques do not work at all hotspots. The one sure
 * way to check a walled garden is to see if a URL fetch on a known address
 * fetches the data we expect
 */

class CheckWalledGardenConnection() : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        return isWalledGardenConnection()
    }
}

fun isWalledGardenConnection(): Boolean {
    var urlConnection: HttpURLConnection? = null
    try {
        val url = URL(mWalledGardenUrl) // "http://clients3.google.com/generate_204"
        urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.instanceFollowRedirects = false
        urlConnection.connectTimeout = WALLED_GARDEN_SOCKET_TIMEOUT_MS
        urlConnection.readTimeout = WALLED_GARDEN_SOCKET_TIMEOUT_MS
        urlConnection.useCaches = false
        urlConnection.inputStream
        // We got a valid response, but not from the real google
        return urlConnection.responseCode != 204
    } catch (e: IOException) {
        Log.i(
            TAG,
            "Walled garden check - probably not a portal: exception "
                    + e
        )
        return false
    } finally {
        urlConnection?.disconnect()
    }
}
