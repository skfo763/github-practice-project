package com.example.sample_github_rx.dagger

import com.example.sample_github_rx.dagger.ui.MainModule
import com.example.sample_github_rx.dagger.ui.RepositoryModule
import com.example.sample_github_rx.dagger.ui.SearchModule
import com.example.sample_github_rx.dagger.ui.SignInModule
import com.example.sample_github_rx.ui.main.MainActivity
import com.example.sample_github_rx.ui.repo.RepositoryActivity
import com.example.sample_github_rx.ui.search.SearchActivity
import com.example.sample_github_rx.ui.signin.SignInActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBinder {

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun bindSignInActivity(): SignInActivity

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun bindSearchActivity(): SearchActivity

    @ContributesAndroidInjector(modules = [RepositoryModule::class])
    abstract fun bindRepositoryActivity(): RepositoryActivity

}