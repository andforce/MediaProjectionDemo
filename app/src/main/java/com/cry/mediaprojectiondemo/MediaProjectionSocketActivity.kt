package com.cry.mediaprojectiondemo

import android.content.Context
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cry.mediaprojectiondemo.databinding.MediaprojectionActivityMainBinding


class MediaProjectionSocketActivity : AppCompatActivity() {

    private var mpm: MediaProjectionManager? = null
    private lateinit var viewMainBinding: MediaprojectionActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaprojectionActivityMainBinding.inflate(layoutInflater).apply {
            viewMainBinding = this
        }.root.also {
            setContentView(it)
        }

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {
                    it.data?.let { data ->
                        CastService.startService(this, data, it.resultCode)
                    } ?: run {
                        Toast.makeText(this, "User granted permission", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    Toast.makeText(this, "User did not grant permission", Toast.LENGTH_SHORT).show()
                }
            }
        }


        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        viewMainBinding.btnStart.setOnClickListener {
            mpm?.createScreenCaptureIntent()?.let {
                launcher.launch(it)
            }
        }
    }
}
