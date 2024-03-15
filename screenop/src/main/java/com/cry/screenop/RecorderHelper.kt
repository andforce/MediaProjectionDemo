package com.cry.screenop

import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

object RecorderHelper {

    //函数参数，参数名：activity，参数类型：AppCompatActivity
    //函数返回值：Unit

    fun startRecording(activity: WindowManager, width: Int, height: Int, mp: MediaProjection, action: (Bitmap?) -> Unit) {
        RxScreenShot
            .shoot(activity, width, height, mp)
            .subscribe({ bitmap ->
                action(bitmap)
            }, { e -> e.printStackTrace() })
    }
}