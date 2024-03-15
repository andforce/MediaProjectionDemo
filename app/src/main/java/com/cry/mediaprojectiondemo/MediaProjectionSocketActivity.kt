package com.cry.mediaprojectiondemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.socket.SocketIoManager
import com.cry.screenop.RecorderHelper
import java.io.ByteArrayOutputStream

class MediaProjectionSocketActivity : AppCompatActivity() {

    var mpm: MediaProjectionManager? = null
    val code = 1

    private val MAX_IMAGE_HEIGHT = 480

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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code && resultCode == RESULT_OK) {
            data?.let {
                val mp = mpm?.getMediaProjection(
                    resultCode, data
                )

                mp?.let {

                    val metrics = DisplayMetrics()
                    val windowMgr = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    windowMgr.defaultDisplay.getRealMetrics(metrics)

                    // 获取屏幕宽高
                    var widthPixels = metrics.widthPixels.toFloat()
                    var heightPixels = metrics.heightPixels.toFloat()
                    if (heightPixels > MAX_IMAGE_HEIGHT) {
                        // heightPixels        MAX_IMAGE_HEIGHT
                        // ------------  =     ----------------
                        // widthPixels         x
                        widthPixels = widthPixels * MAX_IMAGE_HEIGHT.toFloat() / heightPixels
                        heightPixels = MAX_IMAGE_HEIGHT.toFloat()
                    }
                    val finalWidthPixels = widthPixels.toInt()
                    val finalHeightPixels = heightPixels.toInt()

                    RecorderHelper.startRecording(windowMgr,finalWidthPixels, finalHeightPixels, mp) {
                        it?.let {
                            sendBitmap(it)
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
