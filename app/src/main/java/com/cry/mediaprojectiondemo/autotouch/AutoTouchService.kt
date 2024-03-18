package com.cry.mediaprojectiondemo.autotouch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.cry.mediaprojectiondemo.socket.SocketViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AutoTouchService : AccessibilityService() {

    private val socketViewModel: SocketViewModel by inject()

    private var job: Job? = null

    private val metrics = DisplayMetrics()

    private var finalWidthPixels = 1
    private var finalHeightPixels = 1
    private val path = Path()
    private var lastTimeStamp = 0L

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        var windowManager: WindowManager? = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?

        if (windowManager == null) {
            return
        }

        windowManager.defaultDisplay?.getRealMetrics(metrics)

        finalWidthPixels = (metrics.widthPixels)
        finalHeightPixels = (metrics.heightPixels)


        job = GlobalScope.launch {
            socketViewModel.eventFlow.collect {

                it?.let {
                    //Log.d("AutoTouchService", "collect MouseEvent: $it")
                    if (lastTimeStamp == 0L) {
                        lastTimeStamp = System.currentTimeMillis()
                    }



                    when (it) {
                        is com.andforce.socket.MouseEvent.Down -> {
                            lastTimeStamp = System.currentTimeMillis()
                            path.reset()
                            val scaleW = finalWidthPixels / it.remoteWidth.toFloat()
                            val scaleH = finalHeightPixels / it.remoteHeight.toFloat()
                            val fromRealX = it.x * scaleW
                            val fromRealY = it.y * scaleH
                            path.moveTo(fromRealX, fromRealY)

                        }
                        is com.andforce.socket.MouseEvent.Move -> {
                            val scaleW = finalWidthPixels / it.remoteWidth.toFloat()
                            val scaleH = finalHeightPixels / it.remoteHeight.toFloat()
                            val fromRealX = it.x * scaleW
                            val fromRealY = it.y * scaleH
                            path.lineTo(fromRealX, fromRealY)
                        }
                        is com.andforce.socket.MouseEvent.Up -> {
                            if (path.isEmpty) {
                                return@collect
                            }
                            val currentTime = System.currentTimeMillis()
                            var duration = if (currentTime - lastTimeStamp < 25) 25 else currentTime - lastTimeStamp
                            if (duration > 500) {
                                duration = 500
                            }

                            Log.d("AutoTouchService", "UP duration: $duration")

                            dispatchMouseGesture(0, duration)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //Log.d("AutoTouchService", "onAccessibilityEvent: $event")
    }

    override fun onInterrupt() {
        Log.d("AutoTouchService", "onInterrupt")
    }

    private fun dispatchMouseGesture(startTime: Long, duration: Long) {
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, startTime, duration))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

}