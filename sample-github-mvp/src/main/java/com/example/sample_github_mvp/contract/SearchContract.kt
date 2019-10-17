package com.example.sample_github_mvp.contract

import android.content.Context
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_github_mvp.view.search.SearchAdapter

interface SearchContract {

    interface View: BaseContract.View {
        override fun showProgress()

        override fun hideProgress()

        override fun showError(message: String)

        fun hideError()

        fun hideSoftKeyBoard(searchView: SearchView)

        fun updateTitle(query: String)

        fun updateRecyclerView(adapter: RecyclerView.Adapter<SearchAdapter.RepositoryHolder>)

        fun getApplicationContext(): Context
    }

    interface Presenter: BaseContract.Presenter<View> {
        override fun attachView(view: View)

        override fun detachView()

        fun searchRepository(query: String)

        fun clearResults()
    }

}