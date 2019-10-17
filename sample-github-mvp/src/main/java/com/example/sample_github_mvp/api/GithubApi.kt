package com.example.sample_github_mvp.api

import com.example.sample_github_mvp.model.GithubRepo
import com.example.sample_github_mvp.model.RepoSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// 저장소 검색 API와 저장소 정보 읽기 API 구현을 위한 인터페이스
interface GithubApi {

    // GET 방식으로 search/repositories 동작을 수행
    // RepoSearchResponse 객체의 필드를 자동으로 맵핑하여 리턴하는 함수 선언
    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>

    // GET 방식으로 repos/{owner}/{name} 동작을 수행
    // RepoSearchResponse 객체의 필드를 자동으로 맵핑하여 리턴하는 함수 선언
    @GET("repos/{owner}/{name}")
    fun getRepository(@Path("owner") ownerLogin: String, @Path("name") repoName: String): Call<GithubRepo>
}
