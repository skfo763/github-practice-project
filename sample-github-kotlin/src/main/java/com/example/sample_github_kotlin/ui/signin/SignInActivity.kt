package com.example.sample_github_kotlin.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    internal lateinit var btnStart: Button
    internal lateinit var progress: ProgressBar
    internal lateinit var api: AuthApi
    internal lateinit var authTokenProvider: AuthTokenProvider

    internal lateinit var accessTokenCall: Call<GithubAccessToken>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // UI view 요소들 findViewById로 R 주소랑 맵핑
        btnStart = findViewById(R.id.btnActivitySignInStart)
        progress = findViewById(R.id.pbActivitySignIn)

        // start button setOnClickListener 등록
        btnStart.setOnClickListener {
            // 사용자 인증 처리를 위한 Url 구성
            // 형식 : https://github.com/login/oauth/authorize?client_id=application_client_id
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                    // 경로 추가 "/~/ : 로그인 기능 수행
                    .appendPath("login")
                    // 경로 추가 "/~/ : oauth 인증 방법 사용
                    .appendPath("oauth")
                    // 경로 추가 "/~/ : authorize. 인증 수행
                    .appendPath("authorize")
                    // module build.gradle 파일에 정의된 buildConfigField 'client id' 참조하여 url 쿼리로 넘겨줌
                    .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                    .build()
            Log.i("TAG", "Uri: $authUri")

            // 신속한 뷰 처리를 위해, webView 사용하는 것이 아니라 CustomTabsIntent 사용, 크롬 탭의 UI 커스텀
            // 주의할 점 : 기기의 기본 웹 브라우저가 크롬이 아닌 다른 브라우저로 설정되어 있으면 크롬 탭이 열리지 않는다.
            // 문서 : https://developer.android.com/reference/android/support/customtabs/CustomTabsIntent
            // 설명 : https://sirubomber.tistory.com/38
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        api = GithubApiProvider.provideAuthApi()
        authTokenProvider = AuthTokenProvider(this)

        if (null != authTokenProvider.token) {
            launchMainActivity()
        }
    }

    // web에서 인증이 종료시, samplegithub://authorize?code={액세스 토큰 교환용 코드} 주소로 반환.
    // manifest.xml의 signinactivity의 intent-filter를 참조하면, 이 액티비티가 해당 주소를 열 수 있음.
    // CustomTabsIntent 호출로 인해서, web 브라우저로 SigninActivity가 가려졌다가 인증 종료 시 다시 화면에 띄워짐.
    // 이 경우, 이미 SigninActivity가 화면에 표시된 상태라 onCreate()가 호출되지 않고 onNewIntent()가 호출됨.
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
                if (response.isSuccessful && null != token) {
                    // 발급받은 토큰 업데이트 -> 저장
                    authTokenProvider.updateToken(token.accessToken)
                    launchMainActivity()

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
        btnStart.visibility = View.GONE
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnStart.visibility = View.VISIBLE
        progress.visibility = View.GONE
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
