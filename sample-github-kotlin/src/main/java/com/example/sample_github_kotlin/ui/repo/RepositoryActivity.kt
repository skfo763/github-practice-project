package com.example.sample_github_kotlin.ui.repo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.api.GithubApi
import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_kotlin.api.model.GithubRepo
import com.example.sample_github_kotlin.ui.GlideApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    internal lateinit var llContent: LinearLayout
    internal lateinit var ivProfile: ImageView
    internal lateinit var tvName: TextView
    internal lateinit var tvStars: TextView
    internal lateinit var tvDescription: TextView
    internal lateinit var tvLanguage: TextView
    internal lateinit var tvLastUpdate: TextView
    internal lateinit var pbProgress: ProgressBar
    internal lateinit var tvMessage: TextView
    internal lateinit var api: GithubApi

    internal lateinit var repoCall: Call<GithubRepo>

    // REST API 응답에 포함된 날짜 및 시간 표시 형식
    internal var dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    // 사용자에게 보여줄 날짜 및 시간 표시 형식
    internal var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        llContent = findViewById(R.id.llActivityRepositoryContent)
        ivProfile = findViewById(R.id.ivActivityRepositoryProfile)
        tvName = findViewById(R.id.tvActivityRepositoryName)
        tvStars = findViewById(R.id.tvActivityRepositoryStars)
        tvDescription = findViewById(R.id.tvActivityRepositoryDescription)
        tvLanguage = findViewById(R.id.tvActivityRepositoryLanguage)
        tvLastUpdate = findViewById(R.id.tvActivityRepositoryLastUpdate)
        pbProgress = findViewById(R.id.pbActivityRepository)
        tvMessage = findViewById(R.id.tvActivityRepositoryMessage)

        api = GithubApiProvider.provideGithubApi(this)

        // 액티비티 호출 시 전달받은 user login 엑스트라 추출
        val login = intent.getStringExtra(KEY_USER_LOGIN)
                ?: throw IllegalArgumentException("No login info exists in extras")

        // 액티비티 호출 시 전달받은 저장소 이름 엑스트라 추출
        val repo = intent.getStringExtra(KEY_REPO_NAME)
                ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        repoCall = api.getRepository(login, repoName)
        repoCall.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val repo = response.body()
                if (response.isSuccessful() && null != repo) {
                    GlideApp.with(this@RepositoryActivity)
                            .load(repo.owner.avatarUrl)
                            .into(ivProfile)

                    tvName.setText(repo.fullName)
                    tvStars.text = resources
                            .getQuantityString(R.plurals.star, repo.stars, repo!!.stars)
                    if (null == repo!!.description) {
                        tvDescription.setText(R.string.no_description_provided)
                    } else {
                        tvDescription.setText(repo!!.description)
                    }
                    if (null == repo!!.language) {
                        tvLanguage.setText(R.string.no_language_specified)
                    } else {
                        tvLanguage.setText(repo!!.language)
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(repo!!.updatedAt)
                        tvLastUpdate.text = dateFormatToShow.format(lastUpdate!!)
                    } catch (e: ParseException) {
                        tvLastUpdate.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                showError(t.message)
            }
        })
    }

    private fun showProgress() {
        llContent.visibility = View.GONE
        pbProgress.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        llContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pbProgress.visibility = View.GONE
    }

    private fun showError(message: String?) {
        tvMessage.text = message
        tvMessage.visibility = View.VISIBLE
    }

    companion object {

        val KEY_USER_LOGIN = "user_login"
        val KEY_REPO_NAME = "repo_name"
    }
}
