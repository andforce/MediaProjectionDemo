package com.cry.mediaprojectiondemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.socket.SocketIoManager

//import com.cry.cry.mediaprojectioncode.RecordActivity

class WelComeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wel)

        Thread {
            SocketIoManager.getInstance()

//            val socketClient = MySocketClient("192.168.2.183", 8088)
//            socketClient.startConnection()
//
//            socketClient.sendMessage("Hello, Server!")
//            val response = socketClient.receiveMessage()
//            Log.d("SocketClient", "Response from server, $response")
//            socketClient.stopConnection()

        }.start()

        findViewById<Button>(R.id.btn_1).setOnClickListener{
            _->
            val intent = Intent(this@WelComeActivity, MainActivity::class.java)
            startActivity(intent)
        }
//        findViewById<Button>(R.id.btn_2).setOnClickListener{
//            _->
//            val intent = Intent(this@WelComeActivity, RecordActivity::class.java)
//            startActivity(intent)
//        }

    }


}
