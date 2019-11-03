package com.example.sample_github_rx.dagger.ui

import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.ui.main.MainActivity
import com.example.sample_github_rx.ui.main.MainViewModelFactory
import com.example.sample_github_rx.ui.search.SearchAdapter
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun provideMainViewModelFactory(
            searchHistoryDao: SearchHistoryDao
    ): MainViewModelFactory {
        return MainViewModelFactory(searchHistoryDao)
    }

    @Provides
    fun provideSearchAdapter(activity: MainActivity): SearchAdapter {
        return SearchAdapter().apply { listener = activity }
    }
}