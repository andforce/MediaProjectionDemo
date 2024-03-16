package com.cry.screenop.coroutine

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordViewModel(private val scope: CoroutineScope) {

    private val repo: RecordRepository = RecordRepository()

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> get() = _capturedImage

    fun startCaptureImages(context: Context, mp: MediaProjection, scale: Float) {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        scope.launch(handler) {
            repo.captureBitmap(context.applicationContext, mp, scale).collect() {
                _capturedImage.value = it
            }
        }
    }

}