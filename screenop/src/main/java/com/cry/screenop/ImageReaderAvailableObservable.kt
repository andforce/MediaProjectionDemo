package com.cry.screenop

import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Handler
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by a2957 on 4/21/2018.
 */
class ImageReaderAvailableObservable private constructor(
    private val imageReader: ImageReader,
    private val handler: Handler?
) : Observable<ImageReader?>() {
    override fun subscribeActual(observer: Observer<in ImageReader?>) {
        val listener = Listener(observer, imageReader)
        observer.onSubscribe(listener)
        imageReader.setOnImageAvailableListener(listener, handler)
    }

    internal class Listener(
        private val observer: Observer<in ImageReader?>,
        private val mImageReader: ImageReader
    ) : Disposable, OnImageAvailableListener {
        private val unsubscribed = AtomicBoolean()
        override fun onImageAvailable(reader: ImageReader) {
            if (!isDisposed) {
                observer.onNext(reader)
            }
        }

        override fun dispose() {
            if (unsubscribed.compareAndSet(false, true)) {
                mImageReader.setOnImageAvailableListener(null, null)
            }
        }

        override fun isDisposed(): Boolean {
            return unsubscribed.get()
        }
    }

    companion object {
        fun of(imageReader: ImageReader): ImageReaderAvailableObservable {
            return ImageReaderAvailableObservable(imageReader, null)
        }
    }
}