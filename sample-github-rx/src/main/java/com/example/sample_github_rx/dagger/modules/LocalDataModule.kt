package com.example.sample_github_rx.dagger.modules

import dagger.Module

@Module
class LocalDataModule {

    /*@Provides
    @Singleton
    fun provideAuthTokenProvider(@Named("appContext") context: Context): AuthTokenProvider {
        return AuthTokenProvider(context)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(db: SimpleGithubDatabase): SearchHistoryDao {
        return db.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext") context: Context): SimpleGithubDatabase {
        return Room.databaseBuilder(context.applicationContext,
                SimpleGithubDatabase::class.java, "simple_github_db")
                .build()
    }*/
}