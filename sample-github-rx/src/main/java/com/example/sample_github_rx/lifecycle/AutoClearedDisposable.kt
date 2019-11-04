package com.example.sample_github_rx.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.sample_github_rx.dagger.CompositeDisposableModule
import com.example.sample_github_rx.dagger.DaggerCompositeComponent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class AutoClearedDisposable (
        private val lifecycleOwner: AppCompatActivity,
        private val alwaysClearOnStop: Boolean = true
): LifecycleObserver {

    @Inject lateinit var compositeDisposable: CompositeDisposable

    init {
        DaggerCompositeComponent.builder().
                compositeDisposableModule(CompositeDisposableModule())
                .build()
                .inject(this)
    }

    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        compositeDisposable.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
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