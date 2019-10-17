package com.example.sample_github_mvp.view.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.sample_github_mvp.presentor.SignInPresenter
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.contract.SignInContract
import com.example.sample_github_mvp.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), SignInContract.View {
    private lateinit var signInPresenter: SignInPresenter

    override fun getAppContext(): Context = applicationContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signInPresenter = SignInPresenter().apply { attachView(this@SignInActivity) }

        btnActivitySignInStart.setOnClickListener {
            signInPresenter.onButtonClicked().apply {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(this@SignInActivity, this)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { signInPresenter.setOnNewIntentCalled(intent) }
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
