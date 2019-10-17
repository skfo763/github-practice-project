package com.example.sample_github_mvp.presentor

import android.content.Intent
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.api.GithubApi
import com.example.sample_github_mvp.api.GithubApiProvider
import com.example.sample_github_mvp.contract.RepoContract
import com.example.sample_github_mvp.model.GithubRepo
import com.example.sample_github_mvp.view.GlideApp
import kotlinx.android.synthetic.main.activity_repository.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepoPresenter(private val context: RepoContract.View): RepoContract.Presenter {

    private lateinit var api: GithubApi
    private lateinit var repoCall: Call<GithubRepo>
    private lateinit var login: String
    private lateinit var repo: String
    private lateinit var dateFormatInResponse: SimpleDateFormat
    private lateinit var dateFormatToShow: SimpleDateFormat

    override fun attachView(view: RepoContract.View) {
        api = GithubApiProvider.provideGithubApi(context.getAppContext())
        dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    override fun detachView() {

    }

    override fun getExtraData(intent: Intent) {
        login = intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalStateException("No login information exists in extras")
        repo = intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalStateException("No repository information exists in extras")
    }

    override fun getRepositoryInfo() {
        context.showProgress()
        repoCall = api.getRepository(login, repo)
        repoCall.enqueue(object: Callback<GithubRepo> {
            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                context.hideProgress(false)
                context.showError("Not successful: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                context.hideProgress(true)
                val rep = response.body()

                if(response.isSuccessful) {
                    rep?.let {
                        checkInformation(it).apply {
                            context.showRepositoryInfo(it, this)
                        }
                    }
                } else {
                    context.showError("Not successful: ${response.message()}")
                }
            }
        })
    }

    override fun checkInformation(repo: GithubRepo): String {
        if(repo.description.isEmpty()) {
            repo.description = context.getAppContext().getText(R.string.no_description_provided).toString()
        }

        if(repo.language.isEmpty()) {
            repo.language = context.getAppContext().getString(R.string.no_language_specified)
        }

        return try {
            dateFormatInResponse.parse(repo.updatedAt)!!.toString()
        } catch (e: ParseException) {
            context.getAppContext().getString(R.string.unknown)
        }
    }

    companion object {
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }
}