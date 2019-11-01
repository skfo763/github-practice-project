package com.example.sample_github_rx.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.room.SearchHistoryDao

class SearchViewModelFactory(val api: GithubApi, val searchHistoryDao: SearchHistoryDao)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(api, searchHistoryDao) as T
    }
}