package com.cry.screenop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.cry.screenop.ImageReaderAvailableObservable.Companion.of
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function

/**
 * 截取屏幕的单利
 * Created by a2957 on 4/21/2018.
 */
class RxScreenShot private constructor(private val mediaProjection: MediaProjection) {
    private val TAG = "RxScreenShot"

    private val callBackHandler: Handler = CallBackHandler()
    private val mediaCallBack = MediaCallBack()
    private var surfaceFactory: SurfaceFactory? = null
    private var imageReader: ImageReader? = null

    private val metrics = DisplayMetrics()

    private var width = 1080
    private var height = 2280
    private var dpi = 1

    fun createImageReader(manager: WindowManager, width: Int, height: Int): RxScreenShot {
        manager.defaultDisplay.getRealMetrics(metrics)
        this.width = width
        this.height = height
        dpi = metrics.densityDpi

        //注意这里使用RGB565报错提示，只能使用RGBA_8888
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 5)
        surfaceFactory = ImageReaderSurface(imageReader!!)
        createProject(width, height, dpi)
        return this
    }

    private fun createProject(width: Int, height: Int, dpi: Int) {
        mediaProjection.registerCallback(mediaCallBack, callBackHandler)
        val flags =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        mediaProjection.createVirtualDisplay(
            "$TAG-display",
            width, height, dpi, flags,
            surfaceFactory!!.inputSurface, null, null
        )
    }

    fun startCapture(): Observable<Bitmap?> {
        return of(imageReader!!).map { imageReader ->
                val mImageName = System.currentTimeMillis().toString() + ".jpeg"
                Log.e(TAG, "image name is : $mImageName")
                var result: Bitmap? = null
                val image = imageReader.acquireLatestImage()

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
                result
            }
    }

    internal inner class MediaCallBack : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
        }
    }

    internal class CallBackHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    interface SurfaceFactory {
        val inputSurface: Surface?
    }

    internal class ImageReaderSurface(private val imageReader: ImageReader) : SurfaceFactory {
        override val inputSurface: Surface?
            get() = imageReader.surface
    }

    companion object {
        private const val MAX_IMAGE_HEIGHT = 480

        private fun requestCapture(activity: FragmentActivity, mp: MediaProjection): Observable<MediaProjection?> {
            val mpm = activity.applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?
            return if (mpm == null) {
                Observable.just(null)
            } else {
                Observable.just(mp)
            }
        }

        fun shoot(activity: FragmentActivity, mp: MediaProjection): Observable<Bitmap?> {
            val metrics = DisplayMetrics()
            val windowMgr = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowMgr.defaultDisplay.getRealMetrics(metrics)

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


            return requestCapture(activity, mp)
                .map { mediaProjection: MediaProjection ->
                    RxScreenShot(mediaProjection)
                        .createImageReader(
                            activity.windowManager,
                            finalWidthPixels,
                            finalHeightPixels
                        )
                }
                .flatMap { obj: RxScreenShot -> obj.startCapture() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}