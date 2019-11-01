package com.example.sample_github_rx.room

import android.content.Context
import androidx.room.Room

private var instance : SimpleGithubDatabase? = null

fun provideSerachHistoryDao(context: Context): SearchHistoryDao
    = provideDatabase(context).searchHistoryDao()

private fun provideDatabase(context: Context): SimpleGithubDatabase {
    if(instance == null) {
        instance = Room.databaseBuilder(context.applicationContext,
                SimpleGithubDatabase::class.java, "simple_github.db")
                .build()
    }
    return instance!!
}