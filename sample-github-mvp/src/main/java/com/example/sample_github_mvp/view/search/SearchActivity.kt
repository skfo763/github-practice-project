package com.example.sample_github_mvp.view.search

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_github_mvp.presentor.SearchPresenter
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.contract.SearchContract
import com.example.sample_github_mvp.model.GithubRepo
import com.example.sample_github_mvp.presentor.RepoPresenter
import com.example.sample_github_mvp.view.repo.RepositoryActivity
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

class SearchActivity : AppCompatActivity(), SearchContract.View, SearchAdapter.ItemClickListener {
    private lateinit var searchPresenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchPresenter = SearchPresenter().apply { attachView(this@SearchActivity) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        val menuSearch = menu?.findItem(R.id.menu_activity_search_query)
        val menuDummy = menu?.findItem(R.id.menu_activity_dummy)
        val searchView = menuSearch?.actionView as SearchView

        menuDummy?.setOnMenuItemClickListener {
            Toast.makeText(this@SearchActivity, "Dummy $it", Toast.LENGTH_SHORT).show()
            true
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideError()
                showProgress()
                updateTitle(query!!)
                hideSoftKeyBoard(searchView)
                menuSearch.collapseActionView()

                searchPresenter.searchRepository(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        return true
    }

    override fun updateRecyclerView(adapter: RecyclerView.Adapter<SearchAdapter.RepositoryHolder>) {
        if(rvActivitySearchList.adapter == null) {
            rvActivitySearchList.layoutManager = LinearLayoutManager(this)
            rvActivitySearchList.adapter = adapter
        } else {
            println("notify")
            rvActivitySearchList.adapter!!.notifyDataSetChanged()
        }
    }

    override fun updateTitle(query: String) {
        val ab = supportActionBar
        ab?.subtitle = query
    }

    override fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE    }

    override fun hideProgress() {
        pbActivitySearch.visibility = View.GONE    }

    override fun showError(message: String) {
        tvActivitySearchMessage.text = message
        tvActivitySearchMessage.visibility = View.VISIBLE
    }

    override fun hideError() {
        tvActivitySearchMessage.text = ""
        tvActivitySearchMessage.visibility = View.GONE
    }

    override fun hideSoftKeyBoard(searchView: SearchView) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(imm).hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    override fun onClick(repository: GithubRepo) {
        println(repository.description)
        val intent = Intent(this@SearchActivity, RepositoryActivity::class.java)
                .putExtra(RepoPresenter.KEY_USER_LOGIN, repository.owner.login)
                .putExtra(RepoPresenter.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }
}
