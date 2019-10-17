package com.example.sample_github_mvp.presentor

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.sample_github_kotlin.api.GithubApiProvider
import com.example.sample_github_mvp.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import com.example.sample_github_mvp.api.AuthApi
import com.example.sample_github_mvp.api.AuthTokenProvider
import com.example.sample_github_mvp.contract.SignInContract
import com.example.sample_github_mvp.model.GithubAccessToken
import com.example.sample_github_mvp.view.signin.SignInActivity
import retrofit2.Response
import java.lang.IllegalStateException

internal class SignInPresenter: SignInContract.Presenter {
    private lateinit var api: AuthApi
    private lateinit var context: SignInContract.View
    private lateinit var accessTokenCall: Call<GithubAccessToken>

    override fun attachView(view: SignInContract.View) {
        context = view
        api = GithubApiProvider.provideAuthApi()
    }

    override fun detachView() {

    }

    override fun getAccessToken(code: String) {
        context.showProgress()
        accessTokenCall = api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
        println(code)

        accessTokenCall.enqueue(object : Callback<GithubAccessToken> {
            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                context.hideProgress()
                context.showError(t)
            }

            override fun onResponse(call: Call<GithubAccessToken>, response: Response<GithubAccessToken>) {
                context.hideProgress()

                val token = response.body()
                if (response.isSuccessful) {
                    token?.let {
                        (context as SignInActivity).authTokenProvider.updateToken(token.accessToken)
                        context.launchMainActivity()
                    }
                } else {
                    context.showError(IllegalStateException("Not successful: ${response.message()}"))
                }
            }
        })
    }
}