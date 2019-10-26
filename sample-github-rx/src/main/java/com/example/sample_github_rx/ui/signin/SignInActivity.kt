package com.example.sample_github_rx.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.sample_github_rx.BuildConfig
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.api.GithubApiProvider
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    internal lateinit var api: AuthApi
    private lateinit var authTokenProvider: AuthTokenProvider

    // private lateinit var accessTokenCall: Call<GithubAccessToken>
    // 위에 나타난 accessTokenCall 대신에, RxJava 의 Disposable 객체를 관리할 수 있는
    // CompositeDisposable 객체 초기화 하여 콜백 대신 사용
    internal val disposable = CompositeDisposable()

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

    override fun onStop() {
        super.onStop()

        // 화면 이탈했을 때, 관리하고 있던 disposable 객체를 모두 반환
        disposable.clear()
    }

    // 주소에서 받은 액세스 토큰을 가지고 실제 토큰을 받는 함수
    private fun getAccessToken(code: String) {
        // 이전에 작성했던 콜백 방식의 호출 전부 삭제. disposable 객체를 활용하여 reactive 비동기 호출

        disposable.add(api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
                // map 을 통해서 응답받은 객체에서 accessToken 값만을 받아올 수 있음.
                .map { it.accessToken }

                // observeOn 을 통해서 이후에 실행되는 동작을 메인 스레드 (UI Thread) 에서 실행함.
                // AndroidSchedulers : RxAndroid 에서 제공
                .observeOn(AndroidSchedulers.mainThread())

                // subscribe 함수가 호출되었을 때 수행할 작업
                .doOnSubscribe { showProgress() }

                // onComplete 가 호출되고, 스트림이 종료되었을 때(= api 호출 최종 종료) 수행할 작업
                .doOnTerminate { hideProgress() }

                // 옵저버블 구독 -> add 에서 정의한 방식대로 api 를 호출하여, 데이터인 accessToken 이 변화하면 아래 정의된 동작 수행.
                // 내부에 구현된 람다 함수의 인자인 token 값은 map 에서 걸러진 accessToken 값임.
                .subscribe ({ token ->
                    authTokenProvider.updateToken(token)
                    launchMainActivity()

                }) {
                    // 에러 블록 (네트워크 오류, 데이터 처리 오류 등)
                    showError(it)
                }
        )
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
