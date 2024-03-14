package com.cry.mediaprojectiondemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
//import com.cry.cry.mediaprojectioncode.RecordActivity

class WelComeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wel)

        SocketClient.sendGetRequest("http://192.168.2.183:3000")

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
