package com.example.sample_github_rx.ui.signin

import androidx.lifecycle.ViewModel
import com.example.sample_github_rx.SupportOptional
import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.optionalOf
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class SigninViewModel (val api: AuthApi, val authTokenProvider: AuthTokenProvider): ViewModel() {

    val accessToken: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    val message: BehaviorSubject<String> = BehaviorSubject.create()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun loadAccessToken(): Disposable
        = Single.fromCallable { optionalOf(authTokenProvider.token) }
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer<SupportOptional<String>> {
                accessToken.onNext(it)
            })

    fun requestAccessToken(clientId: String, clientSecret: String, code: String): Disposable {
        return api.getAccessToken(clientId, clientSecret, code)
                .map { it.accessToken }
                .doOnSubscribe { isLoading.onNext(true) }
                .doOnTerminate { isLoading.onNext(false) }
                .subscribe( {
                    authTokenProvider.updateToken(token = it)
                    accessToken.onNext(optionalOf(it))
                }) {
                    message.onNext(it.message ?: "Unexpected error.")
                }
    }
}