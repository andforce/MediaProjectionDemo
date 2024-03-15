package com.cry.mediaprojectiondemo

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.socket.SocketIoManager
import com.cry.screenop.RecorderHelper
import java.io.ByteArrayOutputStream

class MediaProjectionSocketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            SocketIoManager.connect()
        }.start()

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            RecorderHelper.startRecording(this) {
                sendBitmap(it)
            }

//            //start to screenshot
//            RxScreenShot
//                    .shoot(this@MediaProjectionSocketActivity)
//                    .subscribe({ bitmap ->
//                        if (bitmap is Bitmap) {
//                            if (!isBack) {
//                                moveTaskToBack(true)
//                                isBack = true
//                            } else {
//                                sendBitmap(bitmap)
//                            }
//                        }
//
//                    }, { e -> e.printStackTrace() })
        }
    }

    //bitmap to byteArray to send through socket
    private fun sendBitmap(it: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        SocketIoManager.send(byteArray)
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketIoManager.release()
    }
}
