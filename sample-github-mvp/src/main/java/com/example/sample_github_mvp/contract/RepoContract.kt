package com.example.sample_github_mvp.contract

import android.content.Intent
import com.example.sample_github_mvp.model.GithubRepo

interface RepoContract {
    interface View: BaseContract.View {
        fun showRepositoryInfo(repo: GithubRepo)    // 저장소 정보를 ui에 띄워줌

        override fun showError(message: String)

        override fun showProgress()

        override fun hideProgress()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun onAttachView(view: View)    // presenter과 뷰가 연결되었을 때

        fun getExtraData(intent: Intent)    // 엑스트라 데이터 추출

        fun onDetachView()  // 연결 해제

        fun getRepositoryInfo(): Boolean    // 저장소 정보 얻어옴

        fun checkInformation(repo: GithubRepo)  // 얻어온 저장소 정보의 유효성 체크
    }
}