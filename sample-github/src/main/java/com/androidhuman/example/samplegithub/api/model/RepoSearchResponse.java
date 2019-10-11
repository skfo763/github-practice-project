package com.androidhuman.example.samplegithub.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RepoSearchResponse {
    // 액세스 토큰 모델 클래스 내부의 필드 이름과, API 호출 인터페이스에서 맵핑시켜줄 필드 이름이 다르다면
    // SerializedName annotation 을 추가하여 맵핑시켜줄 필드 이름을 지정해주면 됨

    @SerializedName("total_count")
    public final int totalCount;

    public final List<GithubRepo> items;

    public RepoSearchResponse(int totalCount, List<GithubRepo> items) {
        this.totalCount = totalCount;
        this.items = items;
    }
}
