package com.cry.screenop

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

object RecorderHelper {

    //函数参数，参数名：activity，参数类型：AppCompatActivity
    //函数返回值：Unit

    fun startRecording(activity: AppCompatActivity, action: (Bitmap) -> Unit) {
        RxScreenShot
            .shoot(activity)
            .subscribe({ bitmap ->
                if (bitmap is Bitmap) {
                    action(bitmap)
                }

            }, { e -> e.printStackTrace() })
    }
}