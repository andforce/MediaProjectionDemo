package com.cry.mediaprojectiondemo.socket;

import android.util.Log;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.cry.mediaprojectiondemo.SocketClient;

import java.net.URISyntaxException;

import io.socket.client.Socket;

/**
 * Created by a2957 on 4/23/2018.
 */

public class SocketIoManager {

    SocketClient socketClient;
    private String localUrl;

    private SocketIoManager() {
        socketClient = new SocketClient();
    }

    private static class SINGLE_TON {
        private static SocketIoManager INSTANCE = new SocketIoManager();
    }

    public static SocketIoManager getInstance() {
        return SINGLE_TON.INSTANCE;
    }



    public void send(byte[] bitmapArray) {
        socketClient.send(bitmapArray);
    }

    public void release() {
        socketClient.release();
    }
}
