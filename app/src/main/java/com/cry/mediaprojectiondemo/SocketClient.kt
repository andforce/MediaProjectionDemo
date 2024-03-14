package com.cry.mediaprojectiondemo

import android.util.Log
import okhttp3.*
import okio.IOException

object SocketClient {
    private val client = OkHttpClient()

    fun sendGetRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
                Log.d("SocketClient", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Handle the response
                    // print
                    Log.d("SocketClient", response.body!!.string())
                } else {
                    // Handle the error
                    Log.d("SocketClient", response.toString())
                }
            }
        })
    }
}