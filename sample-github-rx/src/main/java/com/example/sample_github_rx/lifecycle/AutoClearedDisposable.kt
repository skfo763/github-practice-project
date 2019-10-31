package com.example.sample_github_rx.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable (
        private val lifecycleOwner: AppCompatActivity,
        private val alwaysClearOnStop: Boolean = true,
        private val compositeDisposable: CompositeDisposable = CompositeDisposable()
): LifecycleObserver {

    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        // isFinsishing() 메소드를 사용하여 액티비티가 종료되지 않는 시점 (다른 액티비티 호출 등)
        // 에는 disposable 을 해제하지 않도록 해줌
        if(!alwaysClearOnStop && !lifecycleOwner.isFinishing) {
            return
        }
        compositeDisposable.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        compositeDisposable.clear()
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}