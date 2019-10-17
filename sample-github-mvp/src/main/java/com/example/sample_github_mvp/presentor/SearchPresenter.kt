package com.example.sample_github_mvp.presentor

import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.api.GithubApi
import com.example.sample_github_mvp.contract.SearchContract
import com.example.sample_github_mvp.model.RepoSearchResponse
import com.example.sample_github_mvp.view.search.SearchActivity
import com.example.sample_github_mvp.view.search.SearchAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class SearchPresenter: SearchContract.Presenter {
    private lateinit var api: GithubApi
    private lateinit var searchCall: Call<RepoSearchResponse>
    private lateinit var context: SearchContract.View
    internal lateinit var adapter: SearchAdapter

    override fun attachView(view: SearchContract.View) {
        context = view
        api = GithubApiProvider.provideGithubApi((context as SearchActivity))
        adapter = SearchAdapter()
    }

    override fun detachView() {
        clearResults()
    }

    override fun searchRepository(query: String) {
        clearResults()
        context.hideError()
        context.showProgress()

        searchCall = api.searchRepository(query)
        searchCall.enqueue(object: Callback<RepoSearchResponse> {
            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                context.hideProgress()
                context.showError(t.message!!)
            }

            override fun onResponse(call: Call<RepoSearchResponse>, response: Response<RepoSearchResponse>) {
                context.hideProgress()
                val searchResult = response.body()
                if(response.isSuccessful) {
                    searchResult?.let {
                        adapter.items = it.items.toMutableList()

                        if(searchResult.totalCount == 0) {
                            context.showError((context as SearchActivity).getString(R.string.no_search_result))
                        }
                    }
                } else {
                    context.showError("Not successful: ${response.message()}")
                }
            }
        })
    }

    override fun clearResults() {
        adapter.items.clear()
        adapter.notifyDataSetChanged()
    }
}