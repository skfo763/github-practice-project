package com.example.sample_github_rx.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoActivatedDisposable
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.room.provideSerachHistoryDao
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import com.example.sample_github_rx.ui.search.SearchActivity
import com.example.sample_github_rx.ui.search.SearchAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    private val adapter by lazy { SearchAdapter().apply { listener = this@MainActivity } }
    private val disposables = AutoClearedDisposable(this)
    private val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    private val viewModelFactory by lazy { MainViewModelFactory(provideSerachHistoryDao(this)) }
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [MainViewModel::class.java]

        lifecycle.addObserver(disposables)
        lifecycle.addObserver(viewDisposable)
        lifecycle.addObserver(AutoActivatedDisposable(this) {
            viewModel.searchHisory
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        with(adapter) {
                            if(items.isEmpty()) {
                                clearItems()
                            } else {
                                println(it.value)
                                items = it.value.toMutableList()
                            }
                            notifyDataSetChanged()
                        }
                    }
        })

        viewDisposable.add(viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it.isEmpty) { hideMessage()}
                    else { showMessage(it.value) }
                })

        btnActivityMainSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        with(rvActivityMainList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.menu_activity_main_clearall == item.itemId) {
            disposables.add(viewModel.clearSearchHistory())
            return true
        }
        return super.onOptionsItemSelected(item)
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
