package com.example.sample_github_kotlin.ui.repo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.api.GithubApi
import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_kotlin.api.model.GithubRepo
import com.example.sample_github_kotlin.ui.GlideApp
import kotlinx.android.synthetic.main.activity_repository.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    // 같은 모듈 안에서 사용하도록 internal 제한자 사용
    internal lateinit var api: GithubApi

    // 클래스 내부에서만 사용하도록 private 제한자 사용
    private lateinit var repoCall: Call<GithubRepo>

    // REST API 응답에 포함된 날짜 및 시간 표시 형식
    private var dateFormatInResponse = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())

    // 사용자에게 보여줄 날짜 및 시간 표시 형식
    private var dateFormatToShow = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
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

                // 여기서 널 체크를 했으므로, if 블록 안의 repo 는 !!로 non-null 표시를 해줄 필요가 없음.
                if (response.isSuccessful) {
                    repo.let {
                        GlideApp.with(this@RepositoryActivity)
                                .load(it!!.owner.avatarUrl)
                                .into(ivActivityRepositoryProfile)

                        tvActivityRepositoryName.text = repo!!.fullName
                        tvActivityRepositoryStars.text = resources.getQuantityString(R.plurals.star, repo.stars, repo.stars)
                        checkInformation(it)
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

    // 동반 객체로 설정하여 다른 클래스에서도 참조할 수 있도록 함.
    companion object {
        // const 예약어를 통해 상수로 설정
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }
}
