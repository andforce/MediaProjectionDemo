package com.cry.mediaprojectiondemo

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException

class MySocketClient(private val host: String, private val port: Int) {

    private var clientSocket: Socket? = null
    private var out: DataOutputStream? = null
    private var bufferedReader: BufferedReader? = null

    fun startConnection() {
        try {
            clientSocket = Socket(host, port)
            out = DataOutputStream(clientSocket!!.getOutputStream())
            bufferedReader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MySocketClient", "Exception" + e.toString())
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            Log.e("MySocketClient", "UnknownHostException" + e.toString())
        }

    }

    fun sendMessage(msg: String) {
        out!!.writeUTF(msg)
    }

    fun receiveMessage(): String {
        return bufferedReader!!.readLine()
    }

    fun stopConnection() {
        bufferedReader!!.close()
        out!!.close()
        clientSocket!!.close()
    }
}