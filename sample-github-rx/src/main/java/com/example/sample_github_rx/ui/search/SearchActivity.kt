package com.example.sample_github_rx.ui.search

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.api.GithubApiProvider
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.room.provideSerachHistoryDao
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

// SearchAdapter 의 ItemClickListener 인터페이스 상속
class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {
    
    internal lateinit var api: GithubApi
    internal lateinit var adapter: SearchAdapter
    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView

    private val disposable = AutoClearedDisposable(this)
    // by lazy 구문을 통해 선언과 동시에 초기
    internal val searchHistoryDao by lazy { provideSerachHistoryDao(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        lifecycle.addObserver(disposable)

        adapter = SearchAdapter()
        adapter.listener = this
        rvActivitySearchList.layoutManager = LinearLayoutManager(this)
        rvActivitySearchList.adapter = adapter

        api = GithubApiProvider.provideGithubApi(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)

        searchView = menuSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onQueryTextSubmit(query: String): Boolean {
                updateTitle(query)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

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
        disposable.add(Completable
                .fromCallable { searchHistoryDao.insert(repository) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    Toast.makeText(this, "Uploading data to database...", Toast.LENGTH_SHORT).show()
                }
                .subscribe()
        )
    }

    private fun searchRepository(query: String) {
        disposable.add(api.searchRepository(query)
                .flatMap {
                    if(it.totalCount == 0) {
                        Observable.error(IllegalStateException("No Search Result!!"))
                    } else {
                        Observable.just(it.items)
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    clearResults()
                    hideError()
                    showProgress()
                }
                .doOnTerminate { hideProgress() }
                .subscribe ({ items ->
                    adapter.apply {
                        this.items = items.toMutableList()
                        notifyDataSetChanged()
                    }
                }) {
                    showError(it.message)
                }
        )
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
