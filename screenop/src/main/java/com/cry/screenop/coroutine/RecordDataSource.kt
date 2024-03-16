package com.cry.screenop.coroutine

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager
import com.cry.screenop.listener.OnBitmapListener
import com.cry.screenop.listener.ScreenShot2
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

// https://www.jianshu.com/p/281093cabbc7
// https://www.jianshu.com/p/e73863ae9ae9

class RecordDataSource(
    private val context: Context
) {

    suspend fun captureImages(mp:MediaProjection, scale: Float) = callbackFlow<Bitmap> {

        val callback = object: OnBitmapListener {
            override fun onBitmap(bitmap: Bitmap) {
                trySendBlocking(bitmap)
            }

            override fun onFinished() {
                channel.close()
            }
        }

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val finalWidthPixels = (metrics.widthPixels * scale).toInt()
        val finalHeightPixels = (metrics.heightPixels * scale).toInt()

        val screenShot2 = ScreenShot2(context, mp).apply {
            start(finalWidthPixels, finalHeightPixels)
            registerBitmapListener(callback)
        }

        awaitClose { screenShot2.unregisterBitmapListener() }
    }
}