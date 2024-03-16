package com.cry.screenop.rxjava.image

import android.media.ImageReader
import android.os.Handler
import io.reactivex.Observable
import io.reactivex.Observer

open class ImageAvailableObservable(
    private val imageReader: ImageReader,
    private val handler: Handler? = null
) : Observable<ImageReader>() {
    override fun subscribeActual(observer: Observer<in ImageReader>) {
        val listener = ImageObserver(observer, imageReader)
        observer.onSubscribe(listener)
        imageReader.setOnImageAvailableListener(listener, handler)
    }
}