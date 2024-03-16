package com.cry.screenop.coroutine

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import kotlinx.coroutines.flow.Flow

class RecordRepository {

    private val recordDataSource = RecordDataSource()
    suspend fun captureImages(context: Context, mp: MediaProjection, scale: Float): Flow<Bitmap> {
        return recordDataSource.captureImages(context, mp, scale)
    }
}