package com.androidhuman.example.samplegithub.api;

import com.androidhuman.example.samplegithub.api.model.GithubRepo;
import com.androidhuman.example.samplegithub.api.model.RepoSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// 저장소 검색 API와 저장소 정보 읽기 API 구현을 위한 인터페이스
public interface GithubApi {

    // GET 방식으로 search/repositories 동작을 수행
    // RepoSearchResponse 객체의 필드를 자동으로 맵핑하여 리턴하는 함수 선언
    @GET("search/repositories")
    Call<RepoSearchResponse> searchRepository(@Query("q") String query);

    // GET 방식으로 repos/{owner}/{name} 동작을 수행
    // RepoSearchResponse 객체의 필드를 자동으로 맵핑하여 리턴하는 함수 선언
    @GET("repos/{owner}/{name}")
    Call<GithubRepo> getRepository(@Path("owner") String ownerLogin, @Path("name") String repoName);
}
