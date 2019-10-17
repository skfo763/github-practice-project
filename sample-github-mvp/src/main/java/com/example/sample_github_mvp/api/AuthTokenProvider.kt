package com.example.sample_github_mvp.api

import android.content.Context
import android.preference.PreferenceManager

class AuthTokenProvider(private val context: Context) {

    // SharedPreference 에 저장되어 있는 Token 값을 반환
    // 저장되어 있는 값이 없으면 null 반환
    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_AUTH_TOKEN, null)

    // 현재 토큰 값을 인자로 들어오는 String token 으로 업데이트하는 메소드
    // SharedPreferences에 액세스 토큰을 저장
    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply()
    }

    companion object {
        // SharedPreference 객체를 활용해, web 상에서 얻을 수 있는 token 값을
        // 클래스 외부에서 접근 가능하도록 저장
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
