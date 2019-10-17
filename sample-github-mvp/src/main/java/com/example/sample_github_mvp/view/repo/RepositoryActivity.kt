package com.example.sample_github_mvp.view.repo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.contract.RepoContract
import com.example.sample_github_mvp.model.GithubRepo
import com.example.sample_github_mvp.presentor.RepoPresenter
import com.example.sample_github_mvp.view.GlideApp
import kotlinx.android.synthetic.main.activity_repository.*

class RepositoryActivity : AppCompatActivity(), RepoContract.View {
    private lateinit var repoPresenter: RepoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
        repoPresenter = RepoPresenter(this).apply {
            attachView(this@RepositoryActivity)
            getExtraData(intent)
            getRepositoryInfo()
        }
    }

    override fun showRepositoryInfo(repo: GithubRepo, date: String) {
        setImageView(repo.owner.avatarUrl)
        tvActivityRepositoryName.text = repo.fullName
        tvActivityRepositoryStars.text = resources.getQuantityString(R.plurals.star, repo.stars, repo.stars)
        tvActivityRepositoryLanguage.text = repo.language
        tvActivityRepositoryDescription.text = repo.description
        tvActivityRepositoryLastUpdate.text = date
    }

    override fun setImageView(uri: String) {
        GlideApp.with(this@RepositoryActivity)
                .load(uri)
                .into(ivActivityRepositoryProfile)
    }

    override fun showError(message: String) {
        tvActivityRepositoryMessage.text = message
        tvActivityRepositoryMessage.visibility = View.VISIBLE
    }

    override fun showProgress() {
        llActivityRepositoryContent.visibility = View.GONE
        pbActivityRepository.visibility = View.VISIBLE
    }

    override fun hideProgress(isSucceed: Boolean) {
        llActivityRepositoryContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbActivityRepository.visibility = View.GONE
    }

    override fun getAppContext(): Context = this.applicationContext

}
