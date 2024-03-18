package com.cry.mediaprojectiondemo.autotouch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.getSystemService
import com.cry.mediaprojectiondemo.socket.SocketViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AutoTouchService : AccessibilityService() {

    private val socketViewModel: SocketViewModel by inject()

    private var job: Job? = null

    private val metrics = DisplayMetrics()

    private var finalWidthPixels = 1
    private var finalHeightPixels = 1

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

                Log.d("AutoTouchService", "collect MouseEvent: $it")

                if (it is com.andforce.socket.MouseEvent.Down) {
                    val scaleW = finalWidthPixels / it.remoteWidth.toFloat()
                    val scaleH = finalHeightPixels / it.remoteHeight.toFloat()
                    Log.d("AutoTouchService", "collect click: scale ${scaleW},${scaleH} ${it.x * scaleW}, ${it.y * scaleH}")
                    performClick(it.x * scaleW, it.y * scaleH)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("AutoTouchService", "onAccessibilityEvent: $event")
    }

    override fun onInterrupt() {
        Log.d("AutoTouchService", "onInterrupt")
    }

    private fun performClick(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 1))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

}