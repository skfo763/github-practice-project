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
import com.example.sample_github_rx.api.model.RepoSearchResponse
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*

// SearchAdapter 의 ItemClickListener 인터페이스 상속
class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {
    
    internal lateinit var api: GithubApi
    internal lateinit var adapter: SearchAdapter
    private lateinit var menuSearch: MenuItem
    private lateinit var menuDummy: MenuItem
    private lateinit var searchView: SearchView

    // private lateinit var searchCall: Call<RepoSearchResponse>
    // 위에 나타난 searchCall 대신에, RxJava 의 Disposable 객체를 관리할 수 있는
    // CompositeDisposable 객체 초기화 하여 콜백 대신 사용
    private val disposable = CompositeDisposable()

    // flatMap() 메소드에 넣을 인자로 함수형 변수 checkData 를 선언.
    private val checkData: Function<RepoSearchResponse, Observable<List<GithubRepo>>> = Function {
        if(it.totalCount == 0) {
            // 검색 결과가 없을 경우 Observable 에서 바로 onError() 함를 띄우고 에러 블록을 실행
            Observable.error(IllegalStateException("No Search Result!!"))
        } else {
            // 검색 결과 리스트를, just() 함수로 그대로 다음 리스트로 보냄.
            Observable.just(it.items)
        }
    }

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

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    // 리사이클러뷰 각 itemView 클릭 이벤트 오버라이딩
    override fun onItemClick(repository: GithubRepo) {
        val intent = Intent(this, RepositoryActivity::class.java)
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        startActivity(intent)
    }

    private fun searchRepository(query: String) {
        // 이전에 작성했던 콜백 방식의 호출 전부 삭제. disposable 객체를 활용하여 reactive 비동기 호출
        disposable.add(api.searchRepository(query)
                // Observable 형태로 결과를 바꿔주기 위해 flatMap 사용. (여러 개 활용해야 해서)
                // RepoSearchResponse 클래스 -> Observable<GithubRepo> 클래스로 변환
                .flatMap(checkData)

                // observeOn 을 통해서 이후에 실행되는 동작을 메인 스레드 (UI Thread) 에서 실행함.
                // AndroidSchedulers : RxAndroid 에서 제공
                .observeOn(AndroidSchedulers.mainThread())

                // 구독할 시 수행할 작업 명시
                .doOnSubscribe {
                    clearResults()
                    hideError()
                    showProgress()
                }

                // 스트림 종료 시 수행할 작업 명시
                .doOnTerminate { hideProgress() }

                .subscribe ({ items ->
                    // 검색 결과가 정상적으로 수신되었을 때 처리될 동작 구현
                    adapter.apply {
                        this.items = items.toMutableList()
                        notifyDataSetChanged()
                    }
                }) {
                    // 오류 발생 시 처리될 동작 구현
                    showError(it.message)
                }
        )
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
