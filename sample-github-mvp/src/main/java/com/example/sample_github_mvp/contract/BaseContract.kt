package com.example.sample_github_mvp.contract

class BaseContract {

    interface View {

    }

    interface Presenter<T> {
        fun attachView(view: T)

        fun detachView()
    }

}