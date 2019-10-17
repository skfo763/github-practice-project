package com.example.sample_github_mvp.contract

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import com.example.sample_github_mvp.model.GithubRepo
import java.util.*

interface RepoContract {
    interface View: BaseContract.View {
        fun showRepositoryInfo(repo: GithubRepo, date: String)    // 저장소 정보를 ui에 띄워줌

        fun showError(message: String)

        fun setImageView(uri: String)
        
        fun showProgress()

        fun hideProgress(isSucceed: Boolean)

        fun getAppContext(): Context
    }

    interface Presenter: BaseContract.Presenter<View> {
        override fun attachView(view: View)    // presenter과 뷰가 연결되었을 때

        override fun detachView()  // 연결 해제

        fun getExtraData(intent: Intent)    // 엑스트라 데이터 추출

        fun getRepositoryInfo()    // 저장소 정보 얻어옴

        fun checkInformation(repo: GithubRepo): String  // 얻어온 저장소 정보의 유효성 체크
    }
}