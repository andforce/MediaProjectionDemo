package com.andforce.socket

object SocketIoManager {
    private var socketClient: SocketClient = SocketClient()

    fun send(bitmapArray: ByteArray?) {
        socketClient.send(bitmapArray)
    }

    fun release() {
        socketClient.release()
    }

    fun connect() {
        socketClient.startConnection()
    }
}