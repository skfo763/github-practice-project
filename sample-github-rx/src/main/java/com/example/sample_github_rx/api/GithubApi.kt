package com.example.sample_github_rx.api

import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.api.model.RepoSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String)
            // RxJava 라이브러리의 Observable 클래스 형태로 응답을 전달.
            : Observable<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepository(@Path("owner") ownerLogin: String, @Path("name") repoName: String)
            // RxJava 라이브러리의 Observable 클래스 형태로 응답을 전달.
            : Observable<GithubRepo>
}
