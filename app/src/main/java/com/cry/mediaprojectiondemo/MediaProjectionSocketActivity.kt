package com.cry.mediaprojectiondemo

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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


        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                if (result.data == null) {
                    Toast.makeText(this, "User granted permission", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, CastService::class.java)
                    intent.putExtra("data", result.data)
                    intent.putExtra("code", result.resultCode)
                    startForegroundService(intent)
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
}
