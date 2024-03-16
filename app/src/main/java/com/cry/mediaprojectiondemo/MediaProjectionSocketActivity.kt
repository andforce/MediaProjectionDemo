package com.cry.mediaprojectiondemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cry.mediaprojectiondemo.databinding.MediaprojectionActivityMainBinding
import kotlinx.coroutines.launch


class MediaProjectionSocketActivity : AppCompatActivity() {

    private lateinit var viewMainBinding: MediaprojectionActivityMainBinding

    private lateinit var viewModel: MediaProjectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMainBinding = MediaprojectionActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MediaProjectionViewModel(this@MediaProjectionSocketActivity) as T
            }
        })[MediaProjectionViewModel::class.java]

        viewModel.result.observe(this) {
            when (it) {
                is MediaProjectionViewModel.Result.Success -> {
                    CastService.startService(this, it.data, it.resultCode)
                }
                MediaProjectionViewModel.Result.PermissionDenied -> {
                    Toast.makeText(this, "User did not grant permission", Toast.LENGTH_SHORT).show()
                }
            }
        }


        viewMainBinding.btnStart.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createScreenCaptureIntent()
            }
        }
    }
}
