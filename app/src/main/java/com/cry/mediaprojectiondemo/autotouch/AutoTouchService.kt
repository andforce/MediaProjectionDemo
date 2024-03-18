package com.cry.mediaprojectiondemo.autotouch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cry.mediaprojectiondemo.socket.SocketViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AutoTouchService : AccessibilityService() {

    private val socketViewModel: SocketViewModel by inject()

    private var job: Job? = null
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        job = GlobalScope.launch {
            socketViewModel.eventFlow.collect {

                Log.d("AutoTouchService", "collect MouseEvent: $it")

                if (it is com.andforce.socket.MouseEvent.Down) {
                    performClick(it.x.toFloat(), it.y.toFloat())
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