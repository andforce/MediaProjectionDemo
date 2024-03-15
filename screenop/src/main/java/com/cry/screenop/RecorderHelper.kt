package com.cry.screenop

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager

object RecorderHelper {

    //函数参数，参数名：activity，参数类型：AppCompatActivity
    //函数返回值：Unit

    private val MAX_IMAGE_HEIGHT = 480

    fun startRecording(context: Context, width: Int, height: Int, mp: MediaProjection, action: (Bitmap?) -> Unit) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        // 获取屏幕宽高
        var widthPixels = metrics.widthPixels.toFloat()
        var heightPixels = metrics.heightPixels.toFloat()
        if (heightPixels > MAX_IMAGE_HEIGHT) {
            // heightPixels        MAX_IMAGE_HEIGHT
            // ------------  =     ----------------
            // widthPixels         x
            widthPixels = widthPixels * MAX_IMAGE_HEIGHT.toFloat() / heightPixels
            heightPixels = MAX_IMAGE_HEIGHT.toFloat()
        }
        val finalWidthPixels = widthPixels.toInt()
        val finalHeightPixels = heightPixels.toInt()

        RxScreenShot
            .shoot(windowManager, width, height, mp)
            .subscribe({ bitmap ->
                action(bitmap)
            }, { e -> e.printStackTrace() })
    }
}