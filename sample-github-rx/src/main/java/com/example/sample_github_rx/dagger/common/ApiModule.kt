package com.example.sample_github_rx.dagger.common

import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.api.GithubApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.createAsync()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideAuthApiInDagger(
            @Named("unauthorized") client: OkHttpClient,
            callAdapter: CallAdapter.Factory,
            converter: Converter.Factory
    ): AuthApi {
        return Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(client)
                .addCallAdapterFactory(callAdapter)
                .addConverterFactory(converter)
                .build()
                .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGithubApiInDagger(
            @Named("authorized") client: OkHttpClient,
            callAdapter: CallAdapter.Factory,
            converter: Converter.Factory
    ): GithubApi {
        return Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(client)
                .addCallAdapterFactory(callAdapter)
                .addConverterFactory(converter)
                .build()
                .create(GithubApi::class.java)
    }
}