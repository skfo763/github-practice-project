package com.example.sample_github_rx.dagger

import android.app.Application
import com.example.sample_github_rx.SampleGithubRxApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, LocalDataModule::class,
            OkHttpModule::class, AndroidSupportInjectionModule::class, ActivityBinder::class])
interface AppComponent: AndroidInjector<SampleGithubRxApp> {

    // AppComponent를 생성할 때 사용할 빌더 클래스 정의
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}