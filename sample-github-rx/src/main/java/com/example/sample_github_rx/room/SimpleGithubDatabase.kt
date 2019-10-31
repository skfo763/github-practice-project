package com.example.sample_github_rx.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sample_github_rx.api.model.GithubRepo

@Database(entities = [GithubRepo::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {

    // db와 연결할 데이터 접근 객체 정의
    abstract fun searchHistoryDao(): SearchHistoryDao

}