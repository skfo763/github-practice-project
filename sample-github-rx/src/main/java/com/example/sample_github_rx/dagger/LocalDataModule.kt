package com.example.sample_github_rx.dagger

import android.content.Context
import androidx.room.Room
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.room.SearchHistoryDao
import com.example.sample_github_rx.room.SimpleGithubDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalDataModule {

    @Provides
    @Singleton
    fun provideAuthTokenProviderInDagger(
            @Named("appContext") context: Context
    ): AuthTokenProvider {
        return AuthTokenProvider(context)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDaoInDagger(db: SimpleGithubDatabase): SearchHistoryDao {
        return db.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideDatabaseInDao(
            @Named("appContext") context: Context
    ): SimpleGithubDatabase {
        return Room.databaseBuilder(context, SimpleGithubDatabase::class.java,
                "simple_github.db")
                .build()
    }
}