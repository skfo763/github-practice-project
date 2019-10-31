package com.example.sample_github_rx.api.model

import com.google.gson.annotations.SerializedName

class GithubAccessToken
(
        @field:SerializedName("access_token")
        val accessToken: String, // scope 필드
        val scope: String, // 토큰 타입 필드
        @field:SerializedName("token_type")
        val tokenType: String
)
