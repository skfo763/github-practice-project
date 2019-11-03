package com.example.sample_github_rx.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun Lifecycle.plusAssign(observer: LifecycleObserver) {
    this.addObserver(observer)
}

fun runOnIoScheduler(func: () -> Unit): Disposable {
    return Completable.fromCallable(func)
            .subscribeOn(Schedulers.io())
            .subscribe()
}