package com.example.sample_github_rx.ui.search

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.GithubApiProvider.provideGithubApi
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.room.provideSerachHistoryDao
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

// SearchAdapter 의 ItemClickListener 인터페이스 상속
class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    private val adapter by lazy { SearchAdapter().apply { listener = this@SearchActivity } }
    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView

    private val disposable = AutoClearedDisposable(this)
    private val viewDisposables = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    private val viewModelFactory by lazy {
        SearchViewModelFactory(provideGithubApi(this), provideSerachHistoryDao(this))
    }

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [SearchViewModel::class.java]

        lifecycle.addObserver(disposable)
        lifecycle.addObserver(viewDisposables)

        rvActivitySearchList.layoutManager = LinearLayoutManager(this)
        rvActivitySearchList.adapter = adapter

        viewDisposables.add(viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    with(adapter) {
                        if(items.isEmpty) {
                            clearItems()
                        } else {
                            this.items = items.value.toMutableList()
                        }
                        notifyDataSetChanged()
                    }
                }
        )

        viewDisposables.add(viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    if(message.isEmpty) {
                        hideError()
                    } else {
                        showError(message.value)
                    }
                })

        viewDisposables.add(viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoading ->
                    if(isLoading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        searchView = menuSearch.actionView as SearchView

        viewDisposables.add(searchView.quertTextChangeEvent
                .)

        menuSearch.expandActionView()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_activity_search_query -> {
                item.expandActionView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(repository: GithubRepo) {
        addDataToDatabase(repository)
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun addDataToDatabase(repository: GithubRepo) {
        disposable.add(viewModel.addToSearchHistory(repository))
    }

    private fun searchRepository(query: String) {



    }

    private fun updateTitle(query: String) {
        val ab = supportActionBar
        ab?.subtitle = query
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun hideSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Objects.requireNonNull(imm).hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        adapter.items.clear()
        adapter.notifyDataSetChanged()
    }

    private fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivitySearch.visibility = View.GONE
    }

    private fun showError(message: String?) {
        tvActivitySearchMessage.text = message
        tvActivitySearchMessage.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvActivitySearchMessage.text = ""
        tvActivitySearchMessage.visibility = View.GONE
    }
}
