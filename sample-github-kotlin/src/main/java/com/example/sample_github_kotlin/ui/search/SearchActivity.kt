package com.example.sample_github_kotlin.ui.search

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
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.api.GithubApi
import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_kotlin.api.model.GithubRepo
import com.example.sample_github_kotlin.api.model.RepoSearchResponse
import com.example.sample_github_kotlin.ui.repo.RepositoryActivity
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// SearchAdapter 의 ItemClickListener 인터페이스 상속
class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {
    
    internal lateinit var api: GithubApi
    private lateinit var searchCall: Call<RepoSearchResponse>
    lateinit var adapter: SearchAdapter
    private lateinit var menuSearch: MenuItem
    private lateinit var menuDummy: MenuItem
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 어댑터 객체 초기화 및 설정
        adapter = SearchAdapter()
        adapter.listener = this // 이 클래스 내부에 선언된 onItemClick 함수를 넘겨주겠다.

        // RecyclerView 에 Adpater 넘겨줘서 데이터 띄워줌
        rvActivitySearchList.layoutManager = LinearLayoutManager(this)
        rvActivitySearchList.adapter = adapter

        api = GithubApiProvider.provideGithubApi(this)
    }

    // 안드로이드 기본제공 테마 우측 상단에 위치한 옵션 메뉴 설정
    // onCreateOptionsMenu는 액티비티가 호출될 때 단 한번만 실행됨.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // R.menu.menu_activity_search를 액션바의 메뉴 위치에 inflate
        menuInflater.inflate(R.menu.menu_activity_search, menu)
        menuSearch = menu.findItem(R.id.menu_activity_search_query)
        menuDummy = menu.findItem(R.id.menu_activity_dummy)

        menuDummy.setOnMenuItemClickListener { menuItem ->
            Toast.makeText(this@SearchActivity, "Dummy: $menuItem", Toast.LENGTH_SHORT).show()
            true
        }

        // menuSearch 액션 속성으로 지정해준 SearchView 의 동작 설정.
        searchView = menuSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onQueryTextSubmit(query: String): Boolean {
                // SearchView 의 EditText 로 검색할 String 항목이 넘어왔을 때 동작 설정.
                updateTitle(query)       // 액션 바의 타이틀을 검색한 String 으로 업데이트
                hideSoftKeyboard()       // 검색 시 올라왔던 키보드 자동으로 내려가게
                collapseSearchView()     // 검색을 위한 SearchView(EditText) 닫기
                searchRepository(query)  // 쿼리 String 으로 저장소 검색
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        // 최초 실행시 SearchView 자동으로 expand 해 EditText 띄워주는 코드
        menuSearch.expandActionView()

        return true
    }

    // 최초 실행 시가 아니라 다른 동작을 액티비티가 켜져 있는 상태에서 실행하다가 액션 뷰 아이템 클릭했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_activity_search_query -> {
                item.expandActionView()
                true
            }
            R.id.menu_activity_dummy -> {
                item.setOnMenuItemClickListener { menuItem ->
                    Toast.makeText(this@SearchActivity, "Dummy: $menuItem", Toast.LENGTH_SHORT).show()
                    true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 리사이클러뷰 각 itemView 클릭 이벤트 오버라이딩
    override fun onItemClick(repository: GithubRepo) {
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun searchRepository(query: String) {
        clearResults()     // 검색 전 adapter 클래스 내부 데이터 초기화
        hideError()
        showProgress()

        searchCall = api.searchRepository(query)
        searchCall.enqueue(object : Callback<RepoSearchResponse> {

            // 호출 성공시
            override fun onResponse(call: Call<RepoSearchResponse>, response: Response<RepoSearchResponse>) {
                hideProgress()

                val searchResult = response.body()
                if (response.isSuccessful) {
                    searchResult?.let {
                        adapter.items = it.items.toMutableList()
                        adapter.notifyDataSetChanged()

                        // 검색 결과가 없을 경우 처리
                        if (searchResult.totalCount == 0) {
                            showError(getString(R.string.no_search_result))
                        }
                    }
                } else {
                    showError("Not successful: " + response.message())
                }
            }

            // 호출 실패시
            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                hideProgress()
                showError(t.message)
            }
        })
    }

    // 액션바 타이틀 업데이트
    private fun updateTitle(query: String) {
        val ab = supportActionBar
        ab?.subtitle = query
    }

    // 키보드 숨기기
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
