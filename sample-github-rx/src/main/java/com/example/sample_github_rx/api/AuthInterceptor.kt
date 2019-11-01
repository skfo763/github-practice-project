package com.example.sample_github_rx.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val token: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().newBuilder()
                .addHeader("Authorization", "token $token")
                .build().apply { return chain.proceed(this) }
    }
}