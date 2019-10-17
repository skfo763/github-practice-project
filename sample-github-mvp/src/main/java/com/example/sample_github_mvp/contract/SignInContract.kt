package com.example.sample_github_mvp.contract

import android.content.Context

interface SignInContract {
    interface View: BaseContract.View {
        override fun showProgress()

        override fun hideProgress()

        fun showError(throwable: Throwable)

        fun launchMainActivity()

        fun getAppContext(): Context
    }

    interface Presenter: BaseContract.Presenter<View> {
        override fun attachView(view: View)

        override fun detachView()

        fun getAccessToken(code: String)
    }
}