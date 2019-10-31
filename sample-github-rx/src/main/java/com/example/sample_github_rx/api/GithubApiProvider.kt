package com.example.sample_github_rx.api

import android.content.Context
import com.example.sample_github_rx.data.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object GithubApiProvider {

    // 액세스 토큰 획득을 위한 객체 생성
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(), null))
                // retrofit 으로부터 받은 응답을 Observable 형태로 바꿔주는 adapter 설
                // adapter factory 값을 비동기 형태의 RxJavaCall 로 설정해주겠다.
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
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
                // 받은 응답을 옵저버블 형태로 변환, 비동기 방식으로 api 호출
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi::class.java)
    }

    // 네트워크 통신에 사용할 클라이언트 객체 생성
    private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor,
                                    authInterceptor: AuthInterceptor?): OkHttpClient {
        val b = OkHttpClient.Builder()
        authInterceptor.let { b.addInterceptor(interceptor) }
        // 네트워크 요청 및 응답을 로그로 표시 - 일단은 표시 안함
        // b.addInterceptor(interceptor);
        return b.build()
    }

    // 응답 및 요청에 대한 로그를 표시하는 Interceptor 객체 생성
    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    // 액세스 토큰을 헤더에 추가하는 Interceptor 객체 생성
    private fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
        val token = provider.token ?: throw IllegalStateException("authToken cannot be null.")
        return AuthInterceptor(token)
    }

    // SharedPreferences 에 저장된 AuthToken 을 받아오는 함수
    private fun provideAuthTokenProvider(context: Context): AuthTokenProvider {
        return AuthTokenProvider(context.applicationContext)
    }

    internal class AuthInterceptor(private val token: String) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            // 요청 헤더에 액세스 토큰 정보 추가
            chain.request().newBuilder()
                    .addHeader("Authorization", "token $token")
                    .build().apply { return chain.proceed(this) }
        }
    }
}
