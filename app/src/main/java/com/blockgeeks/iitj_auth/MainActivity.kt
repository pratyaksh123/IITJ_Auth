package com.blockgeeks.iitj_auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull


const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var loginButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener{

            // TODO: Remove this shit
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

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
            val redirectUrl = response.networkResponse?.request?.url
            auth(redirectUrl.toString())

        }
    }

    private fun auth(redirectUrl:String){
        Log.i(TAG, "$redirectUrl")
        val magic = redirectUrl.split("?").get(1)
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        // TODO: Encrypt username and password in this request
        val body: RequestBody = RequestBody.create(
            mediaType,
            "4Tredir=http%3A%2F%2Fwww.gstatic.com%2Fgenerate_204&magic=${magic}&username=&password="
        )
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
        Log.i(TAG,"$response")
        if(response.code == 200){
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(response.networkResponse?.request?.url.toString()))
            startActivity(browserIntent)
        }
    }
}