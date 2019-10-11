package com.example.sample_github_kotlin.api

import com.example.sample_github_kotlin.api.model.GithubAccessToken

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

// Retrofit2 에서는 사용할 API를 인터페이스 내부의 메소드로 선언
// 호출 방식, 경로, 반환되는 데이터 등의 정보를 인터페이스 내부에서 지정
// 사용자 인증을 위한 Retrofit2 API 인터페이스 선언
interface AuthApi {

    @FormUrlEncoded    // URI 형태로 HTTP 통신을 수행할 것
    @POST("login/oauth/access_token")       // login/oauth/access_token 동작을 POST 방식으로 수행
    @Headers("Accept: application/json")    // 수행 결과를 json 형태로 받을 것
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("code") code: String):

    // AccessToken 을 받아서, GithubAccessToken 클래스 형태로 변환 후 멤버 필드 이름에 자동으로 맵핑.
            Call<GithubAccessToken>
}
