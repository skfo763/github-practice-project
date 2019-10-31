package com.example.sample_github_rx.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sample_github_rx.api.model.GithubRepo
import io.reactivex.Flowable

@Dao
interface SearchHistoryDao  {

    // 데이터 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)    // 이미 저장된 항목이 있을 경우 데이터 덮어쓰기.
    fun insert(repo: GithubRepo)

    // 데이터 검색
    @Query("SELECT * FROM repositories")
    fun searchHistory(): Flowable<List<GithubRepo>>

    // 데이터 삭제
    @Query("DELETE FROM repositories")
    fun deleteAll()

}