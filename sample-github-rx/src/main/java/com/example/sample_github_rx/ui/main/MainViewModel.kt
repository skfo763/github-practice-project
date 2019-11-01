package com.example.sample_github_rx.ui.main

import androidx.lifecycle.ViewModel
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.viewmodels.SupportOptional
import com.example.sample_github_rx.viewmodels.emptyOptional
import com.example.sample_github_rx.viewmodels.optionalOf
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(val searchHistoryDao: SearchHistoryDao): ViewModel() {

    val message: BehaviorSubject<SupportOptional<String>>
        = BehaviorSubject.create()

    fun clearSearchHistory(): Disposable {
        return Completable.fromCallable { searchHistoryDao.deleteAll() }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    val searchHisory: Flowable<SupportOptional<List<GithubRepo>>>
        get() {
            return searchHistoryDao.searchHistory()
                    .map { optionalOf(it) }
                    .doOnNext {
                        if(it.value.isEmpty()) {
                            message.onNext(optionalOf("No recent repositories,"))
                        } else {
                            message.onNext(emptyOptional())
                        }
                    }.doOnError {
                        message.onNext(optionalOf(it.message ?: "Unexpected error."))
                    }.onErrorReturn { emptyOptional() }
        }
}