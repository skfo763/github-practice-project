package com.example.sample_github_rx.dagger.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Named("appContext")
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}