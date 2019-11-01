package com.example.sample_github_rx.dagger.modules

import com.example.sample_github_rx.api.AuthInterceptor
import com.example.sample_github_rx.data.AuthTokenProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Named("unauthorized")
    @Singleton
    fun provideUnauthorizedOkHttpclient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
    }

    @Provides
    @Named("authorized")
    @Singleton
    fun provideAuthorizedHttpclient
            (interceptor: HttpLoggingInterceptor, authInterceptor: AuthInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(authInterceptor)
                .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
        val token = provider.token ?: throw IllegalStateException("authToken cannot be null.")
        return AuthInterceptor(token)
    }
}