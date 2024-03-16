package com.cry.screenop

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager
import com.cry.screenop.listener.OnBitmapListener
import com.cry.screenop.listener.VirtualDisplayImageReader
import com.cry.screenop.rxjava.RxScreenShot

object RecorderHelper {

    fun startRecording2(context: Context, scale: Float, mp: MediaProjection, action: OnBitmapListener?) {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        val finalWidthPixels = (metrics.widthPixels * scale).toInt()
        val finalHeightPixels = (metrics.heightPixels * scale).toInt()

        VirtualDisplayImageReader(mp).apply {
            start(finalWidthPixels, finalHeightPixels, metrics.densityDpi)
            action?.let { registerBitmapListener(it) }
        }
    }

    fun startRecording2(width: Int, height: Int, dp: Int, mp: MediaProjection, action: OnBitmapListener?) {

        VirtualDisplayImageReader(mp).apply {
            start(width, height, dp)
            action?.let { registerBitmapListener(it) }
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