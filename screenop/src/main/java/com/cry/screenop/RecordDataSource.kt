package com.cry.screenop

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.activity.result.ActivityResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.Flow

// https://www.jianshu.com/p/281093cabbc7
class RecordDataSource(
    private val context: Context,
    private val result: ActivityResult
) {

//    val recordFlow = flow {
//        val mpm = context.applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//        val mp = mpm.getMediaProjection(result.resultCode, result.data!!)
//        mp?.let {
//            RecorderHelper.startRecording(context, 0.5f, mp, { data->
//                emit(data)
//            })
//        }
//    }

    // demo
    fun flowFrom(mp:MediaProjection) = callbackFlow {
//        val callback = object : Callback {
//            override fun onNextValue(value: T) {
//                try {
//                    sendBlocking(value)
//                } catch (e: Exception) {
//
//                }
//            }
//            override fun onApiError(cause: Throwable) {
//                cancel(CancellationException("API Error", cause))
//            }
//            override fun onCompleted() = channel.close()
//        }
//        api.register(callback)
//        awaitClose { api.unregister(callback) }
        RecorderHelper.startRecording(context, 0.5f, mp) { data ->
            trySendBlocking(data)
        }

        awaitClose {
            mp.stop()
        }
    }
}