package com.example.sample_github_rx.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.data.AuthTokenProvider

class SignInViewModelFactory(private val api: AuthApi, private val authTokenProvider: AuthTokenProvider)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SignViewModel(api, authTokenProvider) as T
    }
}