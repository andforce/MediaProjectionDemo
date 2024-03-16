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

    private val viewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MediaProjectionViewModel(this@MediaProjectionSocketActivity) as T
            }
        })[MediaProjectionViewModel::class.java]
    }

    private val viewMainBinding by lazy {
        MediaprojectionActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewMainBinding.root)

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
