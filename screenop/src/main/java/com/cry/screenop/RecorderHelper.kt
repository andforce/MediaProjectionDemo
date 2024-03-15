package com.cry.screenop

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager

object RecorderHelper {

    //函数参数，参数名：activity，参数类型：AppCompatActivity
    //函数返回值：Unit

    fun startRecording(context: Context, width: Int, height: Int, mp: MediaProjection, action: (Bitmap?) -> Unit) {
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