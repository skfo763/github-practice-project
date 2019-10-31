package com.example.sample_github_rx.data

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
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
