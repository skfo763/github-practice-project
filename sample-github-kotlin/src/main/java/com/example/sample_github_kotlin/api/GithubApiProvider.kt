package com.example.sample_github_kotlin.api

import android.content.Context
import com.example.sample_github_kotlin.data.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object GithubApiProvider {

    // 액세스 토큰 획득을 위한 객체 생성
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(), null))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi::class.java)
    }

    // Repository 정보에 접근하기 위한 객체 생성
    fun provideGithubApi(context: Context): GithubApi {
        return Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(),
                        provideAuthInterceptor(provideAuthTokenProvider(context))))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi::class.java)
    }

    // 네트워크 통신에 사용할 클라이언트 객체 생성
    private fun provideOkHttpClient(
            interceptor: HttpLoggingInterceptor,
            authInterceptor: AuthInterceptor?): OkHttpClient {
        val b = OkHttpClient.Builder()
        if (null != authInterceptor) {
            // AuthInterceptor 클래스를 활용해 매 요펑의 해더에 액세스 토큰 정보 추가
            b.addInterceptor(authInterceptor)
        }
        // 네트워크 요청 및 응답을 로그로 표시 - 일단은 표시 안함
        // b.addInterceptor(interceptor);
        return b.build()
    }

    // 응답 및 요청에 대한 로그를 표시하는 Interceptor 객체 생성
    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    // 액세스 토큰을 헤더에 추가하는 Interceptor 객체 생성
    private fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
        val token = provider.token ?: throw IllegalStateException("authToken cannot be null.")
        return AuthInterceptor(token)
    }

    // SharedPreferences 에 저장된 AuthToken을 받아오는 함수
    private fun provideAuthTokenProvider(context: Context): AuthTokenProvider {
        return AuthTokenProvider(context.applicationContext)
    }

    internal class AuthInterceptor(private val token: String) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            // 요청 헤더에 애겟스 토큰 정보 추가
            val b = original.newBuilder()
                    .addHeader("Authorization", "token $token")

            val request = b.build()
            return chain.proceed(request)
        }
    }
}
