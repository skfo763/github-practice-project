package com.example.sample_github_rx.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    // 애플리케이션의 Context 제공
    // 다른 Context와의 혼동을 피하기 위해 appContext라는 이름으로 구분.
    @Provides
    @Named("appContext")
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}