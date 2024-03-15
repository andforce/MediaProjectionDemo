package com.cry.mediaprojectiondemo

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.*

class SocketClient {
    private var socket: Socket? = null

    init {
        try {
            socket = IO.socket("http://10.66.32.51:3001")



        } catch (e: Exception) {
            Log.e("SocketClient", e.toString())
        }
    }


    fun startConnection() {
        Thread.sleep(1000)
        socket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            Log.d("SocketClient", "connect")
        })

        socket?.on("event", Emitter.Listener { args ->
            Log.d("SocketClient", args[0].toString())
        })

        socket?.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            Log.d("SocketClient", "disconnect")
        })

        socket?.connect()
        Thread.sleep(1000)
    }
    fun send(bitmapArray: ByteArray?) {
//        if (!mSocketReady) {
//            return
//        }
//        if (bitmapArray != null) {
//            mSocket.emit("event", *bitmapArray)
//        }
        socket?.emit("image", bitmapArray)
    }

    fun release() {

        socket?.disconnect()
        socket?.off(Socket.EVENT_CONNECT)
        socket?.off(Socket.EVENT_DISCONNECT)
        socket?.off("event")
        socket?.close()
    }
}