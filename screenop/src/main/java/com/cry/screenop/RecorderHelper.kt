package com.cry.screenop

import android.graphics.Bitmap
import android.media.projection.MediaProjection
import androidx.appcompat.app.AppCompatActivity

object RecorderHelper {

    //函数参数，参数名：activity，参数类型：AppCompatActivity
    //函数返回值：Unit

    fun startRecording(activity: AppCompatActivity, mp: MediaProjection, action: (Bitmap?) -> Unit) {
        RxScreenShot
            .shoot(activity, mp)
            .subscribe({ bitmap ->
                action(bitmap)
            }, { e -> e.printStackTrace() })
    }
}