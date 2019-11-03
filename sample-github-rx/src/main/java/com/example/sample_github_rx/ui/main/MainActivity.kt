package com.example.sample_github_rx.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoActivatedDisposable
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.plusAssign
import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import com.example.sample_github_rx.ui.search.SearchActivity
import com.example.sample_github_rx.ui.search.SearchAdapter
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {

    @Inject private lateinit var searchHistoryDao: SearchHistoryDao

    private val adapter by lazy { SearchAdapter().apply { listener = this@MainActivity } }
    private val disposables = AutoClearedDisposable(this)
    private val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    private val viewModelFactory by lazy { MainViewModelFactory(searchHistoryDao) }
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [MainViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposable

        lifecycle += AutoActivatedDisposable(this) {
            viewModel.searchHistory
                    .subscribeOn(Schedulers.io())
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
        }

        btnActivityMainSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        with(rvActivityMainList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewDisposable += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message ->
                    if(message.isEmpty) {
                        hideMessage()
                    } else {
                        showMessage(message.value)
                    }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.menu_activity_main_clearall == item.itemId) {
            clearAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearAll() {
        disposables.add(viewModel.clearSearchHistory())
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
