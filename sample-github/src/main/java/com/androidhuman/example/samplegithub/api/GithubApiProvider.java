package com.androidhuman.example.samplegithub.api;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidhuman.example.samplegithub.data.AuthTokenProvider;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class GithubApiProvider {

    // 액세스 토큰 획득을 위한 객체 생성
    public static AuthApi provideAuthApi() {
        return new Retrofit.Builder()
                .baseUrl("https://github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(), null))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApi.class);
    }

    // Repository 정보에 접근하기 위한 객체 생성
    public static GithubApi provideGithubApi(@NonNull Context context) {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(provideOkHttpClient(provideLoggingInterceptor(),
                        provideAuthInterceptor(provideAuthTokenProvider(context))))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi.class);
    }

    // 네트워크 통신에 사용할 클라이언트 객체 생성
    private static OkHttpClient provideOkHttpClient(
            @NonNull HttpLoggingInterceptor interceptor,
            @Nullable AuthInterceptor authInterceptor) {
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        if (null != authInterceptor) {
            // AuthInterceptor 클래스를 활용해 매 요펑의 해더에 액세스 토큰 정보 추가
            b.addInterceptor(authInterceptor);
        }
        // 네트워크 요청 및 응답을 로그로 표시 - 일단은 표시 안함
        // b.addInterceptor(interceptor);
        return b.build();
    }

    // 응답 및 요청에 대한 로그를 표시하는 Interceptor 객체 생성
    private static HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    // 액세스 토큰을 헤더에 추가하는 Interceptor 객체 생성
    private static AuthInterceptor provideAuthInterceptor(@NonNull AuthTokenProvider provider) {
        String token = provider.getToken();
        if (null == token) {
            throw new IllegalStateException("authToken cannot be null.");
        }
        return new AuthInterceptor(token);
    }

    // SharedPreferences 에 저장된 AuthToken을 받아오는 함수
    private static AuthTokenProvider provideAuthTokenProvider(@NonNull Context context) {
        return new AuthTokenProvider(context.getApplicationContext());
    }

    static class AuthInterceptor implements Interceptor {
        private final String token;

        AuthInterceptor(String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            // 요청 헤더에 애겟스 토큰 정보 추가
            Request.Builder b = original.newBuilder()
                    .addHeader("Authorization", "token " + token);

            Request request = b.build();
            return chain.proceed(request);
        }
    }
}
