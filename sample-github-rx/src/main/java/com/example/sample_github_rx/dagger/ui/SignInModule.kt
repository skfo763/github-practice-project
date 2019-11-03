package com.example.sample_github_rx.dagger.ui

import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.ui.signin.SignInViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SignInModule {

    @Provides
    @Singleton
    fun provideSignInViewModelFactory(
            api: AuthApi, authTokenProvider: AuthTokenProvider
    ): SignInViewModelFactory {
        return SignInViewModelFactory(api, authTokenProvider)
    }
}