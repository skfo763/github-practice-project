package com.example.sample_github_rx.dagger

import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import dagger.Component
import io.reactivex.disposables.CompositeDisposable

@Component(modules = [CompositeDisposableModule::class])
interface CompositeComponent {
    fun compositeDisposable(): CompositeDisposable

    fun inject(disposable: AutoClearedDisposable)
}