package com.cry.mediaprojectiondemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.socket.SocketIoManager
import com.cry.screenop.RecorderHelper
import java.io.ByteArrayOutputStream

class MediaProjectionSocketActivity : AppCompatActivity() {

    var mpm: MediaProjectionManager? = null
    val code = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        Thread {
            SocketIoManager.connect()
        }.start()

        findViewById<Button>(R.id.btn_start).setOnClickListener {

            val intent = mpm?.createScreenCaptureIntent()
            intent?.let {
                startActivityForResult(intent, code)
            }


//            RecorderHelper.startRecording(this) {
//                it?.let {
//                    sendBitmap(it)
//                }
//            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                data?.let { data->
                    val mp = mpm?.getMediaProjection(
                        resultCode, data
                    )

                    mp?.let {
                        RecorderHelper.startRecording(this, mp) {
                            it?.let {
                                sendBitmap(it)
                            }
                        }
                    }

                }
            }
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
