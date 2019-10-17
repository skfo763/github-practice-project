package com.example.sample_github_mvp.view.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.sample_github_mvp.BuildConfig
import com.example.sample_github_mvp.presentor.SignInPresenter
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.api.AuthTokenProvider
import com.example.sample_github_mvp.contract.SignInContract
import com.example.sample_github_mvp.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class SignInActivity : AppCompatActivity(), SignInContract.View {

    private lateinit var signInPresenter: SignInPresenter
    lateinit var authTokenProvider: AuthTokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signInPresenter = SignInPresenter().apply { attachView(this@SignInActivity) }
        authTokenProvider = AuthTokenProvider(this)
        println(authTokenProvider.token)

        btnActivitySignInStart.setOnClickListener {
            val authUri = setURL()
            println(authUri.toString())
            CustomTabsIntent.Builder().build().apply { launchUrl(this@SignInActivity, authUri) }
        }
    }

    private fun setURL(): Uri {
        return Uri.Builder().scheme("https").authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                .build()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println(intent?.data.toString())
        showProgress()
        val uri = intent?.data ?: throw IllegalArgumentException("No data exists")
        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")
        signInPresenter.getAccessToken(code)
    }

    override fun showProgress() {
        btnActivitySignInStart.visibility = View.GONE
        pbActivitySignIn.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pbActivitySignIn.visibility = View.GONE
        btnActivitySignInStart.visibility = View.VISIBLE
    }

    override fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun launchMainActivity() {
        startActivity(Intent(
                this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
