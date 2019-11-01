package com.example.sample_github_rx.dagger.components

import com.example.sample_github_rx.SampleGithubApp
import com.example.sample_github_rx.dagger.modules.ApiModule
import com.example.sample_github_rx.dagger.modules.AppModule
import com.example.sample_github_rx.dagger.modules.LocalDataModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AppModule::class, LocalDataModule::class,
            ApiModule::class, AndroidSupportInjectionModule::class])
interface AppComponent: AndroidInjector<SampleGithubApp> {

    /*@Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        @BindsInstance
        fun build(): AppComponent
    }*/
}
