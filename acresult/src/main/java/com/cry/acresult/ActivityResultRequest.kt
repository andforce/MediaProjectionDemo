package com.cry.acresult

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.cry.acresult.OnActivityResultDispatcherFragment.OnResultListener
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * Created by a2957 on 4/21/2018.
 */
object ActivityResultRequest {
    private fun getDispatcherFragment(activity: FragmentActivity): OnActivityResultDispatcherFragment {
        val fragmentManager = activity.supportFragmentManager
        val dispatcherFragment = fragmentManager.findFragmentByTag(OnActivityResultDispatcherFragment.TAG) ?: OnActivityResultDispatcherFragment()

        fragmentManager.beginTransaction().apply {
            add(dispatcherFragment, OnActivityResultDispatcherFragment.TAG)
            commitNowAllowingStateLoss()
        }
        fragmentManager.executePendingTransactions()

        return dispatcherFragment as OnActivityResultDispatcherFragment
    }

    fun rxQuest(activity: FragmentActivity, intent: Intent?): Observable<ResultEvent> {
        return Observable
            .just(activity)
            .map { getDispatcherFragment(activity) }
            .flatMap { dispatcherFragment: OnActivityResultDispatcherFragment ->
                OnResultObservable(
                    dispatcherFragment,
                    intent
                )
            }
    }

    private class OnResultObservable(
        private val dispatcherFragment: OnActivityResultDispatcherFragment,
        private val intent: Intent?
    ) : Observable<ResultEvent?>() {
        private val requestCode = OnActivityResultDispatcherFragment.AUTO_REQ_CODE.incrementAndGet()
        override fun subscribeActual(observer: Observer<in ResultEvent?>) {
            val listener = Listener(dispatcherFragment, observer, requestCode)
            observer.onSubscribe(listener)
            dispatcherFragment.startIntentForResult(intent, listener, requestCode)
        }

        private class Listener(
            private val dispatch: OnActivityResultDispatcherFragment,
            private val observer: Observer<in ResultEvent?>,
            private val requestCode: Int
        ) : MainThreadDisposable(), OnResultListener {
            override fun onActivityResult(resultCode: Int, data: Intent?) {
                if (!isDisposed) {
                    observer.onNext(ResultEvent(resultCode, data))
                }
            }

            override fun onDispose() {
                dispatch.remove(requestCode)
            }
        }
    }
}