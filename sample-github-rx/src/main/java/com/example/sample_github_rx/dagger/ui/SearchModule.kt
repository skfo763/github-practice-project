package com.example.sample_github_rx.dagger.ui

import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.ui.search.SearchActivity
import com.example.sample_github_rx.ui.search.SearchAdapter
import com.example.sample_github_rx.ui.search.SearchViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun provideSearchViewModelFactory(
            githubApi: GithubApi,
            searchHistoryDao: SearchHistoryDao
    ): SearchViewModelFactory {
        return SearchViewModelFactory(githubApi, searchHistoryDao)
    }

    @Provides
    fun provideAdapter(activity: SearchActivity): SearchAdapter {
        return SearchAdapter().apply { listener = activity }
    }
}