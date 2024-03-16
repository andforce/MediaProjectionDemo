package com.andforce.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class SocketClient(url: String) {
    private var socket: Socket? = null

    init {
        try {
            socket = IO.socket(url)
        } catch (e: Exception) {
            Log.e("SocketClient", e.toString())
        }
    }

    fun startConnection() {
        socket?.on(Socket.EVENT_CONNECT, Emitter.Listener {
            Log.d("SocketClient", "connect")
        })

        socket?.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
            Log.d("SocketClient", "disconnect")
        })

        socket?.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener {
            Log.d("SocketClient", "connect error")
        })

        socket?.on("event", Emitter.Listener { args ->
            Log.d("SocketClient", args[0].toString())
        })

        socket?.connect()
    }
    fun send(bitmapArray: ByteArray) {
        socket?.emit("image", bitmapArray)
    }

    fun release() {

        socket?.off(Socket.EVENT_CONNECT)
        socket?.off(Socket.EVENT_CONNECT_ERROR)
        socket?.off(Socket.EVENT_DISCONNECT)
        socket?.off("event")

        socket?.disconnect()
        socket?.close()
    }
}