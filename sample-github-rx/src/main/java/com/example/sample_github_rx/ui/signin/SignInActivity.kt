package com.example.sample_github_rx.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProviders
import com.example.sample_github_rx.BuildConfig
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.AuthApi
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.plusAssign
import com.example.sample_github_rx.ui.main.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject

class SignInActivity : DaggerAppCompatActivity() {

    @Inject private lateinit var authApi: AuthApi
    @Inject private lateinit var authTokenProvider: AuthTokenProvider

    private val disposables = AutoClearedDisposable(this)
    private val viewDisposables = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    private val viewModelFactory by lazy {
        SignInViewModelFactory(authApi, authTokenProvider)
    }

    private lateinit var viewModel: SigninViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [SigninViewModel::class.java]
        lifecycle += disposables
        lifecycle += viewDisposables

        viewDisposables += viewModel.accessToken
                .filter { !it.isEmpty }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { launchMainActivity() }

        viewDisposables += viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showError(it)}

        viewDisposables += viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoading ->
                    if(isLoading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }

        disposables += viewModel.loadAccessToken()

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
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showProgress()
        val uri = intent.data ?: throw IllegalArgumentException("No data exists")
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")
        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        disposables.add(viewModel.requestAccessToken(BuildConfig.GITHUB_CLIENT_ID,
                BuildConfig.GITHUB_CLIENT_SECRET, code))
    }

    private fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btnActivitySignInStart.visibility = View.VISIBLE
        pbActivitySignIn.visibility = View.GONE
    }

    private fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 액티비티 스택에서 제거
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) // 새로운 액티비티를 추가
    }
}
