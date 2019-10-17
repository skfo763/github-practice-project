package com.example.sample_github_mvp.view.repo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.api.GithubApi
import com.example.sample_github_mvp.api.GithubApiProvider

class RepositoryActivity : AppCompatActivity() {

    internal lateinit var api: GithubApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        api = GithubApiProvider.provideGithubApi(this)
        val login = intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists in extras")
        val repo = intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")
    }

    companion object {
        // const 예약어를 통해 상수로 설정
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }
}
