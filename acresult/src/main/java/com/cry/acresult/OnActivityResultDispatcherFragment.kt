package com.cry.acresult

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

/**
 * 转发所有onActivityResult的Fragment
 * Created by a2957 on 4/21/2018.
 */
class OnActivityResultDispatcherFragment : Fragment() {
    private val listeners = SparseArray<OnResultListener>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val onResultListener = listeners[requestCode]
        if (onResultListener != null) {
            listeners.remove(requestCode)
            onResultListener.onActivityResult(resultCode, data)
        }
    }

    fun startIntentForResult(intent: Intent?, listener: OnResultListener, requestCode: Int) {
        listeners.put(requestCode, listener)
        startActivityForResult(intent, requestCode)
    }

    fun remove(requestCode: Int) {
        listeners.remove(requestCode)
    }

    interface OnResultListener {
        fun onActivityResult(resultCode: Int, data: Intent?)
    }

    companion object {
        const val TAG = "com.cry.acresult.OnActivityResultDispatcherFragment"
        val AUTO_REQ_CODE = AtomicInteger(1000)
    }
}