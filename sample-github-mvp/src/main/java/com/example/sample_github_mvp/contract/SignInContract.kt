package com.example.sample_github_mvp.contract

import android.net.Uri

interface SignInContract {
    interface View: BaseContract.View {
        fun showProgress()

        fun hideProgress()

        fun showError(throwable: Throwable)

        fun launchMainActivity()
    }

    interface Presenter: BaseContract.Presenter<View> {
        override fun attachView(view: View)

        override fun detachView()

        fun getAccessToken(code: String)
    }
}