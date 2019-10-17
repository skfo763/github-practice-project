package com.example.sample_github_mvp.model

import com.google.gson.annotations.SerializedName

class GithubAccessToken// setter 생성자
(
        // 액세스 토큰 모델 클래스 내부의 필드 이름과, API 호출 인터페이스에서 맵핑시켜줄 필드 이름이 다르다면
        // SerializedName annotation 을 추가하여 맵핑시켜줄 필드 이름을 지정해주면 됨

        // 액세스 토큰 필드
        @field:SerializedName("access_token")
        val accessToken: String, // scope 필드
        val scope: String, // 토큰 타입 필드
        @field:SerializedName("token_type")
        val tokenType: String)
