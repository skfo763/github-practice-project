package com.example.sample_github_kotlin.data

import android.content.Context
import android.content.SharedPreferences

class AuthTokenProvider(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(KEY_AUTH_TOKEN, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    val token: String?
        get() = sharedPreferences.getString(KEY_AUTH_TOKEN, null)

    fun updateToken(token: String) {
        editor.putString(KEY_AUTH_TOKEN, token).apply()
    }

    companion object {
        // SharedPreference 객체를 활용해, web 상에서 얻을 수 있는 token 값을
        // 클래스 외부에서 접근 가능하도록 저장

        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
