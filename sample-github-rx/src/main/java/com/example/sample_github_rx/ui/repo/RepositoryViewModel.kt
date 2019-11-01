package com.example.sample_github_rx.ui.repo

import androidx.lifecycle.ViewModel
import com.example.sample_github_rx.SupportOptional
import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.optionalOf
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RepositoryViewModel(val api: GithubApi): ViewModel() {

    val repository: BehaviorSubject<SupportOptional<GithubRepo>> = BehaviorSubject.create()
    val message: BehaviorSubject<String> = BehaviorSubject.create()
    val isContentVisible: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun requestRepositoryInfo(login: String, repoName: String): Disposable {
        val repoObservable = if(!repository.hasValue()) {
            api.getRepository(login, repoName)
        } else {
            Observable.empty()
        }

        return repoObservable
                .doOnSubscribe { isLoading.onNext(true) }
                .doOnTerminate { isLoading.onNext(false) }
                .subscribeOn(Schedulers.io())
                .subscribe({ repo ->
                    repository.onNext(optionalOf(repo))
                    isContentVisible.onNext(true)
                }) {
                    message.onNext(it.message ?: "Unexpected error")
                }
    }
}