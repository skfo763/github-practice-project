package com.example.sample_github_rx.ui.repo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.GithubApi
import com.example.sample_github_rx.api.GithubApiProvider
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.ui.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RepositoryActivity : AppCompatActivity() {

    internal lateinit var api: GithubApi
    private var dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    private var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val disposable = AutoClearedDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
        lifecycle.addObserver(disposable)
        api = GithubApiProvider.provideGithubApi(this)

        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exists in extras")

        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        disposable.add(api.getRepository(login, repoName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doOnTerminate { hideProgress(true) }
                .doOnError { hideProgress(false) }
                .doOnComplete { hideProgress(true) }
                .subscribe({ repo ->
                    GlideApp.with(this@RepositoryActivity).load(repo.owner.avatarUrl).into(ivActivityRepositoryProfile)
                    tvActivityRepositoryName.text = repo.fullName
                    tvActivityRepositoryStars.text = resources.getQuantityText(R.plurals.star, repo.stars)

                    if(repo.description == null) {
                        tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                    } else {
                        tvActivityRepositoryDescription.text = repo.description
                    }

                    if(repo.language == null) {
                        tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
                    } else {
                        tvActivityRepositoryLanguage.text = repo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                        tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
                    }
                }) {
                    showError(it.message)
                }
        )
    }

    // 받아온 정보가 비어있는지, 잘못 파싱되었는지 처리하는 로직을 함수로 구별하여 정리
    private fun checkInformation(repo: GithubRepo) {
        if (repo.description.isEmpty()) {
            tvActivityRepositoryDescription.text = getText(R.string.no_description_provided)
        } else {
            tvActivityRepositoryDescription.text = repo.description
        }

        if (repo.language.isEmpty()) {
            tvActivityRepositoryLanguage.text = getText(R.string.no_language_specified)
        } else {
            tvActivityRepositoryLanguage.text = repo.language
        }

        try {
            val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
            tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate!!)
        } catch (e: ParseException) {
            tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
        }
    }

    private fun showProgress() {
        llActivityRepositoryContent.visibility = View.GONE
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llActivityRepositoryContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbActivityRepository.visibility = View.GONE
    }

    private fun showError(message: String?) {
        tvActivityRepositoryMessage.text = message
        tvActivityRepositoryMessage.visibility = View.VISIBLE
    }

    companion object {
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }
}
