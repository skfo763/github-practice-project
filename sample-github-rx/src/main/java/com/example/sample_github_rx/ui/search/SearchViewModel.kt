package com.example.sample_github_rx.ui.search

import androidx.lifecycle.ViewModel
import com.example.sample_github_rx.SupportOptional
import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.emptyOptional
import com.example.sample_github_rx.optionalOf
import com.example.sample_github_rx.room.SearchHistoryDao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class SearchViewModel (val api: GithubApi, val searchHistoryDao: SearchHistoryDao): ViewModel() {

    val searchResult: BehaviorSubject<SupportOptional<List<GithubRepo>>>
            = BehaviorSubject.createDefault(emptyOptional())

    val lastSearchKeyword: BehaviorSubject<SupportOptional<String>>
            = BehaviorSubject.createDefault(emptyOptional())

    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun searchRespository(query: String): Disposable {
        return api.searchRepository(query)
                .doOnNext { lastSearchKeyword.onNext(optionalOf(query)) }
                .flatMap {
                    if(it.totalCount == 0) {
                        Observable.error(IllegalStateException("No search result."))
                    } else {
                        Observable.just(it.items)
                    }
                }.doOnSubscribe {
                    searchResult.onNext(emptyOptional())
                    message.onNext(emptyOptional())
                    isLoading.onNext(true)
                }.doOnTerminate { isLoading.onNext(false) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {items ->
                    searchResult.onNext(optionalOf(items))
                }) {
                    message.onNext(optionalOf(it.message ?: "Unexpected error"))
                }
    }

    fun addToSearchHistory(repo: GithubRepo): Disposable {
        return Completable.fromCallable { searchHistoryDao.insert(repo) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

}