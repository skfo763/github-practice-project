package com.example.sample_github_rx.dagger.ui

import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.ui.repo.RepositoryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepoModelFactory(api: GithubApi): RepositoryViewModelFactory {
        return RepositoryViewModelFactory(api)
    }
}