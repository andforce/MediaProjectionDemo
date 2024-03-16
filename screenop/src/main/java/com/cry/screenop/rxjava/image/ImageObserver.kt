package com.cry.screenop.rxjava.image

import android.media.ImageReader
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class ImageObserver(
    private val observer: Observer<in ImageReader>,
    private val reader: ImageReader
) : Disposable, ImageReader.OnImageAvailableListener {
    private val unsubscribed = AtomicBoolean()
    override fun onImageAvailable(reader: ImageReader) {
        if (!isDisposed) {
            observer.onNext(reader)
        }
    }

    override fun dispose() {
        if (unsubscribed.compareAndSet(false, true)) {
            reader.setOnImageAvailableListener(null, null)
        }
    }

    override fun isDisposed(): Boolean {
        return unsubscribed.get()
    }
}