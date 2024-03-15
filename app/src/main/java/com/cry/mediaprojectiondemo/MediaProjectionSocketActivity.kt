package com.cry.mediaprojectiondemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.socket.SocketIoManager
import com.cry.screenop.RecorderHelper
import java.io.ByteArrayOutputStream


class MediaProjectionSocketActivity : AppCompatActivity() {

    private var mpm: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        Thread {
            SocketIoManager.connect()
        }.start()

        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data == null) {
                    Toast.makeText(this, "User granted permission", Toast.LENGTH_SHORT).show()
                } else {
                    mpm?.getMediaProjection(result.resultCode, result.data!!)?.let { mp ->
                        RecorderHelper.startRecording(applicationContext, 720, 1080, mp) { data->
                            data?.let {
                                sendBitmap(data)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "User did not grant permission", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {

            mpm?.createScreenCaptureIntent()?.let {
                launcher.launch(it)
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
