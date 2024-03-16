package com.cry.screenop.listener

import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

class VirtualDisplayImageReader(
    private val mediaProjection: MediaProjection
) {

    companion object {
        const val TAG = "VirtualDisplayImageReader"
    }

    private var imageReader: ImageReader? = null
    private var imageListener: OnImageListener? = null

    fun start(width: Int, height: Int, dpi: Int) {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 5)

        val flags =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        mediaProjection.createVirtualDisplay(
            "$TAG-display",
            width, height, dpi, flags,
            imageReader!!.surface, null, null
        )
    }

    private val mediaCallBack = object : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
            imageListener?.onFinished()
        }
    }

    private val callBackHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.i(TAG, "handleMessage: $msg")
        }
    }

    fun registerListener(imageListener: OnImageListener) {
        this.imageListener = imageListener
        mediaProjection.registerCallback(mediaCallBack, callBackHandler)

        imageReader?.setOnImageAvailableListener(listener, null)
    }

    fun unregisterListener() {
        mediaProjection.unregisterCallback(mediaCallBack)
    }

    private val listener = ImageReader.OnImageAvailableListener { reader ->
        imageListener?.onImage(reader.acquireLatestImage())
    }
}