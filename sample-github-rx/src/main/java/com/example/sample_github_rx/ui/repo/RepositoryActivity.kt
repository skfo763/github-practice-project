package com.example.sample_github_rx.ui.repo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.GithubApiProvider.provideGithubApi
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.ui.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    private var dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    private var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val disposable = AutoClearedDisposable(this)
    private val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    private val viewModelFactory by lazy { RepositoryViewModelFactory(provideGithubApi(this)) }
    private lateinit var viewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [RepositoryViewModel::class.java]

        lifecycle.addObserver(disposable)
        lifecycle.addObserver(viewDisposable)

        viewDisposable.add(viewModel.repository
                .filter{ !it.isEmpty }
                .map { it.value }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { repository ->
                    setUiAtOnSubscribe(repository)
                })

        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exists in extras")

        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exists in extras")

        viewDisposable.add(viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { message -> showError(message) })

        viewDisposable.add(viewModel.isContentVisible
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { visible -> setContentVisibility(visible) })

        viewDisposable.add(viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoading ->
                    if(isLoading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                })

        disposable.add(viewModel.requestRepositoryInfo(login, repo))
    }

    private fun setUiAtOnSubscribe(repo: GithubRepo) {
        GlideApp.with(this@RepositoryActivity).load(repo.owner.avatarUrl).into(ivActivityRepositoryProfile)
        tvActivityRepositoryName.text = repo.fullName
        tvActivityRepositoryStars.text = resources.getQuantityText(R.plurals.star, repo.stars)

        if(repo.description.isNullOrEmpty()) {
            tvActivityRepositoryDescription.setText(R.string.no_description_provided)
        } else {
            tvActivityRepositoryDescription.text = repo.description
        }

        if(repo.language.isNullOrEmpty()) {
            tvActivityRepositoryLanguage.setText(R.string.no_language_specified)
        } else {
            tvActivityRepositoryLanguage.text = repo.language
        }

        try {
            val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
            lastUpdate?.let { tvActivityRepositoryLastUpdate.text = dateFormatToShow.format(lastUpdate) }
        } catch (e: ParseException) {
            tvActivityRepositoryLastUpdate.text = getString(R.string.unknown)
        }
    }

    private fun showProgress() {
        llActivityRepositoryContent.visibility = View.GONE
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun setContentVisibility(show: Boolean) {
        llActivityRepositoryContent.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun hideProgress() {
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
