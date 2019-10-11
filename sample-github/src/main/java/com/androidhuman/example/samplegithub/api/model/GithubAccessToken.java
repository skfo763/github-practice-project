package com.androidhuman.example.samplegithub.api.model;

import com.google.gson.annotations.SerializedName;

public final class GithubAccessToken {
    // 액세스 토큰 모델 클래스 내부의 필드 이름과, API 호출 인터페이스에서 맵핑시켜줄 필드 이름이 다르다면
    // SerializedName annotation 을 추가하여 맵핑시켜줄 필드 이름을 지정해주면 됨

    // 액세스 토큰 필드
    @SerializedName("access_token")
    public final String accessToken;

    // scope 필드
    public final String scope;

    // 토큰 타입 필드
    @SerializedName("token_type")
    public final String tokenType;

    // setter 생성자
    public GithubAccessToken(String accessToken, String scope, String tokenType) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.tokenType = tokenType;
    }
}
