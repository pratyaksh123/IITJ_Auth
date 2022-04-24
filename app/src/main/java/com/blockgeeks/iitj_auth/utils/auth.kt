package com.blockgeeks.iitj_auth.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import io.sentry.Sentry
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.jsoup.parser.Parser.htmlParser
import java.net.UnknownHostException

const val TAG = "Auth.kt"

fun authenticate(applicationContext: Context, username: String, password: String): String? {
    val ai: ApplicationInfo = applicationContext.packageManager
        .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)

    try {
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("http://www.gstatic.com/generate_204")
            .method("GET", null)
            .addHeader("Host", "www.gstatic.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("Cache-Control", "max-age=0")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36"
            )
            .addHeader(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Accept-Language", "en-US,en;q=0.9")
            .build()
        val response: Response = client.newCall(request).execute()
        Log.i(TAG, "Gstatic response: ${response}")
        val redirectUrl = response.networkResponse?.request?.url
        if (response.code == 204) {
            return "Already Connected"
        } else if (response.code == 200) {
            return auth(redirectUrl.toString(), password, username)
        }
    } catch (e: Exception) {
        // UnknownHostException will occur if on cellular data instead of IITJ network. Probably not an issue since this will happen only on the worker's thread that too after a long time interval.
        if (e != UnknownHostException::class.java) {
            Sentry.captureException(e)
        }
        e.printStackTrace()
    }
    return null
}

private fun auth(redirectUrl: String, password: String, username: String): String? {
    val magic = redirectUrl.split("?")[1]
    val client = OkHttpClient().newBuilder()
        .build()
    val mediaType: MediaType? = "application/x-www-form-urlencoded".toMediaTypeOrNull()
    // TODO: Encrypt username and password in this request
    val body: RequestBody = RequestBody.create(
        mediaType,
        "4Tredir=http%3A%2F%2Fwww.gstatic.com%2Fgenerate_204&magic=${magic}&username=${username}&password=${password}"
    )
    val res: String?
    val request: Request = Request.Builder()
        .url(redirectUrl)
        .method("POST", body)
        .addHeader("Host", "gateway.iitj.ac.in:1003")
        .addHeader("Connection", "keep-alive")
        .addHeader("Content-Length", "112")
        .addHeader("Cache-Control", "max-age=0")
        .addHeader(
            "sec-ch-ua",
            "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\""
        )
        .addHeader("sec-ch-ua-mobile", "?0")
        .addHeader("sec-ch-ua-platform", "\"macOS\"")
        .addHeader("Upgrade-Insecure-Requests", "1")
        .addHeader("Origin", "https://gateway.iitj.ac.in:1003")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36"
        )
        .addHeader(
            "Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        )
        .addHeader("Sec-Fetch-Site", "same-origin")
        .addHeader("Sec-Fetch-Mode", "navigate")
        .addHeader("Sec-Fetch-User", "?1")
        .addHeader("Sec-Fetch-Dest", "document")
        .addHeader("Referer", redirectUrl)
        .addHeader("Accept-Encoding", "gzip, deflate, br")
        .addHeader("Accept-Language", "en-US,en;q=0.9")
        .build()
    val response = client.newCall(request).execute()
    Log.i(TAG, "$response")

    if (response.code == 200) {
        val responseBody = response.body?.string();
        val doc = htmlParser().parseInput(responseBody, redirectUrl)
        val authFailedMessage = doc.selectFirst("h2:containsOwn(Authentication)")?.text()
        val authSuccessMessage = doc.selectFirst("h1:containsOwn(Keepalive)")?.text()
        if (authSuccessMessage == null && authFailedMessage == "Firewall authentication failed. Please try again.") {
            Log.i(TAG, "Auth Failed")
            res = "Failed"

        } else if (authFailedMessage == null && authSuccessMessage == "Authentication Keepalive") {
            Log.i(TAG, "Auth Success")
            res = "Success"
        } else {
            Sentry.captureMessage("authFailedMessage: $authFailedMessage authSuccessMessage: $authSuccessMessage responseBody:$responseBody")
            res = "Unknown"
        }
    } else {
        Sentry.captureMessage("Response - $response")
        res = null
    }
    response.body?.close()
    return res
}


/*
Captive portal response
2022-04-17 03:34:39.687 31659-31729/com.blockgeeks.iitj_auth I/BroadCastReceiver: Gstatic response: Response{protocol=http/1.1, code=200, message=OK, url=https://gateway.iitj.ac.in:1003/fgtauth?040b2fb341c63b78}
2022-04-17 03:34:39.970 31659-31729/com.blockgeeks.iitj_auth I/BroadCastReceiver: Response{protocol=http/1.1, code=200, message=OK, url=https://gateway.iitj.ac.in:1003/keepalive?0c03050303000402}
 */