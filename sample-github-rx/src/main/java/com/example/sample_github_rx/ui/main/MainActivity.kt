package com.example.sample_github_rx.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.room.provideSerachHistoryDao
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import com.example.sample_github_rx.ui.search.SearchActivity
import com.example.sample_github_rx.ui.search.SearchAdapter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    private val adapter by lazy { SearchAdapter().apply { listener = this@MainActivity } }
    private val searchHistoryDao by lazy { provideSerachHistoryDao(this) }
    private val disposables = AutoClearedDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(disposables)
        lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun fetch() {
                fetchSearchHistory()
            }
        })

        btnActivityMainSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        with(rvActivityMainList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun fetchSearchHistory(): Disposable =
            searchHistoryDao.searchHistory()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( {
                        with(adapter) {
                            this.items = it.toMutableList()
                            notifyDataSetChanged()
                        }

                        if(it.isEmpty()) {
                            showMessage(getString(R.string.no_recent_repositories))
                        } else {
                            hideMessage()
                        }
                    }) {
                        showMessage("network error has detacted.")
                    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.menu_activity_main_clearall == item.itemId) {
            clearAll()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun clearAll() {
        disposables.add(Completable
                .fromCallable {
                    searchHistoryDao.deleteAll()
                }.subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    override fun onItemClick(repository: GithubRepo) {
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun showMessage(msg: String?) {
        with(tvActivityMainMessage) {
            text = msg ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage() {
        with(tvActivityMainMessage) {
            text = ""
            visibility = View.GONE
        }
    }
}
