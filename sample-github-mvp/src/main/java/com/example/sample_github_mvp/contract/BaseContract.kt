package com.example.sample_github_mvp.contract

class BaseContract {

    interface View {
        fun showError(message: String)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter<T> {
        fun attachView(view: T)

        fun detachView()
    }

}