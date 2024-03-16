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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cry.mediaprojectiondemo.databinding.MediaprojectionActivityMainBinding
import com.cry.mediaprojectiondemo.socket.SocketIoManager
import com.cry.screenop.coroutine.RecordViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class MediaProjectionSocketActivity : AppCompatActivity() {

    private var mpm: MediaProjectionManager? = null
    private var viewModel: RecordViewModel? = null

    private lateinit var viewMainBinding: MediaprojectionActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaprojectionActivityMainBinding.inflate(layoutInflater).apply {
            viewMainBinding = this
        }.root.also {
            setContentView(it)
        }

        ViewModelProvider(this)[RecordViewModel::class.java].apply {
            viewModel = this
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        lifecycleScope.launch(handler) {
            viewModel?.capturedImage?.collect {
                it?.let {
                    withContext(Dispatchers.IO) {
                        sendBitmap(it)
                    }
                }
            }
        }

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
                        viewModel?.startCaptureImages(this, mp, 0.35f)
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
