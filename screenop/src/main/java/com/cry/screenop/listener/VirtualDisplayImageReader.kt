package com.cry.screenop.listener

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

class VirtualDisplayImageReader(
    private val mediaProjection: MediaProjection) {

    companion object {
        const val TAG = "VirtualDisplayImageReader"
    }

    private val callBackHandler: Handler = CallBackHandler()
    private val mediaCallBack = MediaCallBack()
    private var imageReader: ImageReader? = null
    private var bitmapListener: OnBitmapListener? = null

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

    fun registerBitmapListener(bitmapListener: OnBitmapListener) {
        this.bitmapListener = bitmapListener
        mediaProjection.registerCallback(mediaCallBack, callBackHandler)

        imageReader?.setOnImageAvailableListener(listener, null)
    }

    fun unregisterBitmapListener() {
        mediaProjection.unregisterCallback(mediaCallBack)
    }

    private val listener = ImageReader.OnImageAvailableListener { reader ->
        var result: Bitmap? = null
        val image = reader.acquireLatestImage()

        Log.i(TAG, "acquireLatestImage : w:${image.width}, h:${image.height}, t:${image.timestamp}")

        image?.let {
            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            ).apply {
                copyPixelsFromBuffer(buffer)
            }

            result = Bitmap.createBitmap(bitmap, 0, 0, width, height).also {
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }

            it.close()
        }

        result?.let {
            bitmapListener?.onBitmap(it)
        }
    }

    internal inner class MediaCallBack : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
            bitmapListener?.onFinished()
        }
    }

    internal class CallBackHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.i(TAG, "handleMessage: $msg")
        }
    }
}