package com.example.sample_github_rx.dagger

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

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

    @ContributesAndroidInjector
    abstract fun bindRepositoryActivity(): RepositoryActivity

}