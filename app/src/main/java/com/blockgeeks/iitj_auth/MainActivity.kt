package com.blockgeeks.iitj_auth

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

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
            Log.i(TAG, "${response}")
            Log.i(TAG, "${response.header("Location")}")
            Log.i(TAG, "${response.networkResponse?.request?.url}")
        }
    }
}