package com.example.sample_github_rx.api

import com.example.sample_github_rx.api.model.GithubAccessToken
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String

    // RxJava 와 retrofit 동시 활용을 위해, retrofit 에서 받은 응답을
    // Callback 이 아닌 RxJava 의 Observable 클래스로 함.
    ): Observable<GithubAccessToken>
}
