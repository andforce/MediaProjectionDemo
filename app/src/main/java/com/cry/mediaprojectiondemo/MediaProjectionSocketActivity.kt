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
import com.andforce.socket.SocketClient
import com.cry.mediaprojectiondemo.databinding.MediaprojectionActivityMainBinding
import com.cry.screenop.coroutine.RecordViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class MediaProjectionSocketActivity : AppCompatActivity() {

    private var mpm: MediaProjectionManager? = null
    private var viewModel: RecordViewModel? = null

    private var socketClient: SocketClient = SocketClient("http://192.168.2.183:3001")

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
                it?.let { bitmap->
                    withContext(Dispatchers.IO) {
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()

                        socketClient.send(byteArray)
                        runCatching {
                            byteArrayOutputStream.close()
                        }
                        if (bitmap.isRecycled.not()) {
                            bitmap.recycle()
                        }
                    }
                }
            }
        }

        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        socketClient.startConnection()


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


    override fun onDestroy() {
        super.onDestroy()
        socketClient.release()
    }
}
