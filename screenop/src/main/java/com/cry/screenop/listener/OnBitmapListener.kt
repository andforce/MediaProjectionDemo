package com.cry.screenop.listener

import android.graphics.Bitmap

interface OnBitmapListener {
    fun onBitmap(bitmap: Bitmap)

    fun onFinished()
}