package com.example.sample_github_rx.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProviders
import com.example.sample_github_rx.BuildConfig
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.GithubApiProvider.provideAuthApi
import com.example.sample_github_rx.data.AuthTokenProvider
import com.example.sample_github_rx.lifecycle.AutoClearedDisposable
import com.example.sample_github_rx.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val disposables = AutoClearedDisposable(this)
    private val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)
    private val viewModelFactory by lazy { SignInViewModelFactory(provideAuthApi(), AuthTokenProvider(this)) }
    private lateinit var viewModel: SignViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        viewModel = ViewModelProviders.of(this, viewModelFactory) [SignViewModel::class.java]

        lifecycle.addObserver(disposables)
        lifecycle.addObserver(viewDisposable)

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

        viewDisposable.add(viewModel.accessToken
                .filter{ !it.isEmpty }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ launchMainActivity() })

        viewDisposable.add(viewModel.message
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ showError(it) })

        viewDisposable.add(viewModel.isLoading
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    if(it) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                })

        disposables.add(viewModel.loadAccessToken())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        showProgress()
        val uri = intent.data ?: throw IllegalArgumentException("No data exists")
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")
        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        disposables.add(viewModel.requestAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
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

    private fun showError(text: String) {
        Log.e("TAG", text)
    }

    private fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 액티비티 스택에서 제거
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) // 새로운 액티비티를 추가
    }
}
