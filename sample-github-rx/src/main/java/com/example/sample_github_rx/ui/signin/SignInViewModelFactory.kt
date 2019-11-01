package com.example.sample_github_rx.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.data.AuthTokenProvider

class SignInViewModelFactory(val api: AuthApi, val authTokenProvider: AuthTokenProvider): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SigninViewModel(api, authTokenProvider) as T
    }
}