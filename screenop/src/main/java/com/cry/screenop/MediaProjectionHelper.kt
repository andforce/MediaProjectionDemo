package com.cry.screenop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.fragment.app.FragmentActivity
import com.cry.acresult.ActivityResultRequest.rxQuest
import com.cry.acresult.ResultEvent
import io.reactivex.Observable

object MediaProjectionHelper {
    private fun getCaptureIntent(systemService: MediaProjectionManager?): Intent? {
        return systemService?.createScreenCaptureIntent()
    }

    private fun getMediaProjectionManager(context: Context): MediaProjectionManager? {
        return context.applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?
    }

    fun requestCapture(activity: FragmentActivity): Observable<MediaProjection?> {
        val mediaProjectionManager = getMediaProjectionManager(activity)
        return if (mediaProjectionManager == null) {
            Observable.just(null)
        } else {
            Observable
                .just(getCaptureIntent(mediaProjectionManager))
                .filter { it: Intent? -> it != null }
                .flatMap { it: Intent? -> rxQuest(activity, it) }
                .filter { it: ResultEvent -> it.resultCode == Activity.RESULT_OK && it.data != null }
                .map { resultEvent: ResultEvent ->
                    mediaProjectionManager.getMediaProjection(
                        resultEvent.resultCode,
                        resultEvent.data!!
                    )
                }
        }
    }
}