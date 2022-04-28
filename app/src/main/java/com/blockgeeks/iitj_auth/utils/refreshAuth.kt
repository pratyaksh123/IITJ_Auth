package com.blockgeeks.iitj_auth.utils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

const val TAG_ = "RefreshAuth"
fun refreshAuth(url: String): String? {
    try {
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .addHeader("Accept-Encoding", "gzip, deflate, br")
            .addHeader("Accept-Language", "en-US,en;q=0.9")
            .addHeader("Connection", "keep-alive")
            .addHeader("Host", "gateway.iitj.ac.in:1003")
            .addHeader("Referer", url)
            .addHeader("Sec-Fetch-Dest", "document")
            .addHeader("Sec-Fetch-Mode", "navigate")
            .addHeader("Sec-Fetch-Site", "same-origin")
            .addHeader("Sec-Fetch-User", "?1")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Mobile Safari/537.36"
            )
            .addHeader(
                "sec-ch-ua",
                "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\""
            )
            .addHeader("sec-ch-ua-mobile", "?1")
            .addHeader("sec-ch-ua-platform", "\"Android\"")
            .build()
        val response: Response = client.newCall(request).execute()
        Log.i(TAG_, "$response")
        if (response.code == 200) {
            Log.i(TAG_, "Success")
            return "Success"
        } else {
            // Cancel Work
            Log.i(TAG_, "$response")
            return "Failed"
        }
    } catch (e: Exception) {
        return "Failed"
    }
}

