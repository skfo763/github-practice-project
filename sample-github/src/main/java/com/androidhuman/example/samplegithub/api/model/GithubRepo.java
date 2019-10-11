package com.androidhuman.example.samplegithub.api.model;

import com.google.gson.annotations.SerializedName;

public final class GithubRepo {
    // 액세스 토큰 모델 클래스 내부의 필드 이름과, API 호출 인터페이스에서 맵핑시켜줄 필드 이름이 다르다면
    // SerializedName annotation 을 추가하여 맵핑시켜줄 필드 이름을 지정해주면 됨

    public final String name;

    @SerializedName("full_name")
    public final String fullName;

    public final GithubOwner owner;
    public final String description;
    public final String language;

    @SerializedName("updated_at")
    public final String updatedAt;

    @SerializedName("stargazers_count")
    public final int stars;

    public GithubRepo(String name, String fullName, GithubOwner owner, String description, String language,
            String updatedAt, int stars) {
        this.name = name;
        this.fullName = fullName;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.updatedAt = updatedAt;
        this.stars = stars;
    }
}
