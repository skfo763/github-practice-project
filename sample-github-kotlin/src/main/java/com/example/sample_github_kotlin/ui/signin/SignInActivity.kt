package com.example.sample_github_kotlin.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.sample_github_kotlin.BuildConfig
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.api.AuthApi
import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_kotlin.api.model.GithubAccessToken
import com.example.sample_github_kotlin.data.AuthTokenProvider
import com.example.sample_github_kotlin.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    internal lateinit var api: AuthApi
    internal lateinit var authTokenProvider: AuthTokenProvider
    private lateinit var accessTokenCall: Call<GithubAccessToken>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // start button setOnClickListener 등록
        btnActivitySignInStart.setOnClickListener {
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()
            Log.i("TAG", "Uri: $authUri")
            CustomTabsIntent.Builder().build().apply { launchUrl(this@SignInActivity, authUri) }
        }

        api = GithubApiProvider.provideAuthApi()
        authTokenProvider = AuthTokenProvider(this)
        authTokenProvider.token?.let { launchMainActivity() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showProgress()

        // 사용자 인증 후 리디렉션된 주소를 가져옴
        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        // 리디렉션 주소에서 액세스 토큰 교환에 필요한 코드 데이터를 parsing
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")
        getAccessToken(code)
    }

    // 주소에서 받은 액세스 토큰을 가지고 실제 토큰을 받는 함수
    private fun getAccessToken(code: String) {
        showProgress()

        // 교환용 코드, client id, client secret code를 활용해 액세스 토큰 받급받는 REST API
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)

        // enqueue 메소드를 사용한 비동기 호출 처리
        // 요청에 대한 처리 완료시 사용할 콜백 등록
        accessTokenCall.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>, response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful) {
                    token?.let {
                        authTokenProvider.updateToken(token.accessToken)
                        launchMainActivity()
                    }
                } else {
                    showError(IllegalStateException("Not successful: " + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 액티비티 스택에서 제거
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) // 새로운 액티비티를 추가
    }
}
