package com.example.sample_github_rx.dagger

import android.app.Application
import com.example.sample_github_rx.SampleGithubRxApp
import com.example.sample_github_rx.dagger.common.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, LocalDataModule::class,
            OkHttpModule::class, AndroidSupportInjectionModule::class, ActivityBinder::class])
interface AppComponent: AndroidInjector<SampleGithubRxApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}