package com.example.sample_github_rx.dagger

import com.example.sample_github_rx.data.AuthTokenProvider
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import javax.inject.Named
import javax.inject.Singleton

@Module
class OkHttpModule {

    @Provides
    @Named("unauthorized")
    @Singleton
    fun provideUnauthorizedHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Provides
    @Named("authorized")
    @Singleton
    fun provideAuthorizedHttpClient(
            loggingInterceptor: HttpLoggingInterceptor, authInterceptorInDagger: AuthInterceptorInDagger
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptorInDagger)
                .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptorInDagger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptorInDagger(provider: AuthTokenProvider): AuthInterceptorInDagger {
        val token =  provider.token ?: throw IllegalStateException("authToken cannot be null.")
        return AuthInterceptorInDagger(token)
    }
}

class AuthInterceptorInDagger(private val token: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().newBuilder()
                .addHeader("Authorization", "token $token")
                .build().apply { return chain.proceed(this) }
    }
}