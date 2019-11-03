package com.example.sample_github_rx.ui.main

import androidx.lifecycle.ViewModel
import com.example.sample_github_rx.utils.SupportOptional
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.utils.emptyOptional
import com.example.sample_github_rx.utils.optionalOf
import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.utils.runOnIoScheduler
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(val searchHistoryDao: SearchHistoryDao): ViewModel() {

    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()

    val searchHistory: Flowable<SupportOptional<List<GithubRepo>>>
        get() = searchHistoryDao.searchHistory()
                .map { optionalOf(it) }
                .doOnNext {
                    if(it.value.isEmpty()) {
                        message.onNext(optionalOf("No recent repositories."))
                    } else {
                        message.onNext(emptyOptional())
                    }
                }

    fun clearSearchHistory(): Disposable {
        return runOnIoScheduler { searchHistoryDao.deleteAll() }
    }
}