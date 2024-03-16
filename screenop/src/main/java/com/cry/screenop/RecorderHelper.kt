package com.cry.screenop

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager
import com.cry.screenop.listener.ScreenShot2
import com.cry.screenop.rxjava.RxScreenShot

object RecorderHelper {

    fun startRecording2(context: Context, scale: Float, mp: MediaProjection, action: (Bitmap?) -> Unit) {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val finalWidthPixels = (metrics.widthPixels * scale).toInt()
        val finalHeightPixels = (metrics.heightPixels * scale).toInt()

        startRecording2(context, finalWidthPixels, finalHeightPixels, mp, action)
    }

    fun startRecording2(context: Context, width: Int, height: Int, mp: MediaProjection, action: (Bitmap?) -> Unit) {

        ScreenShot2(context, mp).apply {
            start(width, height)
            registerBitmapListener(object : com.cry.screenop.listener.OnBitmapListener {
                override fun onBitmap(bitmap: Bitmap) {
                    action(bitmap)
                }
            })
        }
    }

    private fun startRecording(context: Context, width: Int, height: Int, mp: MediaProjection, action: (Bitmap?) -> Unit) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        RxScreenShot
            .shoot(windowManager, width, height, mp)
            .subscribe({ bitmap ->
                action(bitmap)
            }, { e -> e.printStackTrace() })
    }

    fun startRecording(context: Context, scale: Float, mp: MediaProjection, action: (Bitmap?) -> Unit) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val finalWidthPixels = (metrics.widthPixels * scale).toInt()
        val finalHeightPixels = (metrics.heightPixels * scale).toInt()

        startRecording(context, finalWidthPixels, finalHeightPixels, mp, action)
    }
}