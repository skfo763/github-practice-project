package com.androidhuman.example.samplegithub.ui.search;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhuman.example.samplegithub.R;
import com.androidhuman.example.samplegithub.api.GithubApi;
import com.androidhuman.example.samplegithub.api.GithubApiProvider;
import com.androidhuman.example.samplegithub.api.model.GithubRepo;
import com.androidhuman.example.samplegithub.api.model.RepoSearchResponse;
import com.androidhuman.example.samplegithub.ui.repo.RepositoryActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SearchAdapter 의 ItemClickListener 인터페이스 상속
public class SearchActivity extends AppCompatActivity implements SearchAdapter.ItemClickListener {

    RecyclerView rvList;
    ProgressBar progress;
    TextView tvMessage;
    MenuItem menuSearch, menuDummy;
    SearchView searchView;
    SearchAdapter adapter;
    GithubApi api;

    Call<RepoSearchResponse> searchCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        rvList = findViewById(R.id.rvActivitySearchList);
        progress = findViewById(R.id.pbActivitySearch);
        tvMessage = findViewById(R.id.tvActivitySearchMessage);

        // 어댑터 객체 초기화 및 설정
        adapter = new SearchAdapter();
        adapter.setItemClickListener(this); // 이 클래스 내부에 선언된 onItemClick 함수를 넘겨주겠다.

        // RecyclerView 에 Adpater 넘겨줘서 데이터 띄워줌
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        api = GithubApiProvider.provideGithubApi(this);
    }

    // 안드로이드 기본제공 테마 우측 상단에 위치한 옵션 메뉴 설정
    // onCreateOptionsMenu는 액티비티가 호출될 때 단 한번만 실행됨.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.menu_activity_search를 액션바의 메뉴 위치에 inflate
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        menuSearch = menu.findItem(R.id.menu_activity_search_query);
        menuDummy = menu.findItem(R.id.menu_activity_dummy);

        menuDummy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(SearchActivity.this, "Dummy: " + menuItem, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // menuSearch 액션 속성으로 지정해준 SearchView 의 동작 설정.
        searchView = (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // SearchView 의 EditText 로 검색할 String 항목이 넘어왔을 때 동작 설정.
                updateTitle(query);       // 액션 바의 타이틀을 검색한 String 으로 업데이트
                hideSoftKeyboard();       // 검색 시 올라왔던 키보드 자동으로 내려가게
                collapseSearchView();     // 검색을 위한 SearchView(EditText) 닫기
                searchRepository(query);  // 쿼리 String 으로 저장소 검색
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // 최초 실행시 SearchView 자동으로 expand 해 EditText 띄워주는 코드
        menuSearch.expandActionView();

        return true;
    }

    // 최초 실행 시가 아니라 다른 동작을 액티비티가 켜져 있는 상태에서 실행하다가 액션 뷰 아이템 클릭했을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_activity_search_query :
                item.expandActionView();
                return true;
            case R.id.menu_activity_dummy :
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(SearchActivity.this, "Dummy: " + menuItem, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    // 리사이클러뷰 각 itemview 클릭 이벤트 오버라이딩
    @Override
    public void onItemClick(GithubRepo repository) {
        Intent intent = new Intent(this, RepositoryActivity.class);
        intent.putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login);
        intent.putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name);
        startActivity(intent);
    }

    private void searchRepository(String query) {
        clearResults();     // 검색 전 adapter 클래스 내부 데이터 초기화
        hideError();
        showProgress();

        searchCall = api.searchRepository(query);
        searchCall.enqueue(new Callback<RepoSearchResponse>() {

            // 호출 성공시
            @Override
            public void onResponse(Call<RepoSearchResponse> call, Response<RepoSearchResponse> response) {
                hideProgress();

                RepoSearchResponse searchResult = response.body();
                if (response.isSuccessful() && null != searchResult) {
                    // 검색 결과를 adpater에 반영, notifyDataSetChanged() 메소드로 갱신
                    adapter.setItems(searchResult.items);
                    adapter.notifyDataSetChanged();

                    // 검색 결과가 없을 경우 처리
                    if (0 == searchResult.totalCount) {
                        showError(getString(R.string.no_search_result));
                    }
                } else {
                    showError("Not successful: " + response.message());
                }
            }

            // 호출 실패시
            @Override
            public void onFailure(Call<RepoSearchResponse> call, Throwable t) {
                hideProgress();
                showError(t.getMessage());
            }
        });
    }

    // 액션바 타이틀 업데이트
    private void updateTitle(String query) {
        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setSubtitle(query);
        }
    }

    // 키보드 숨기기
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private void collapseSearchView() {
        menuSearch.collapseActionView();
    }

    private void clearResults() {
        adapter.clearItems();
        adapter.notifyDataSetChanged();
    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvMessage.setText("");
        tvMessage.setVisibility(View.GONE);
    }
}
