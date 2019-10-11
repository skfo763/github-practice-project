package com.androidhuman.example.samplegithub.data;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class AuthTokenProvider {
    // SharedPreference 객체를 활용해, web 상에서 얻을 수 있는 token 값을
    // 클래스 외부에서 접근 가능하도록 저장

    private static final String KEY_AUTH_TOKEN = "auth_token";
    private Context context;

    // context를 인자로 받아 클래스 내부의 멤버 필드에 초기화시켜주는 생성자
    public AuthTokenProvider(@NonNull Context context) {
        this.context = context;
    }

    // 현재 토큰 값을 인자로 들어오는 String token 으로 업데이트하는 메소드
    // SharedPreferences에 액세스 토큰을 저장
    public void updateToken(@NonNull String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply();
    }

    // SharedPreference 에 저장되어 있는 Token 값을 반환
    // 저장되어 있는 값이 없으면 null 반환
    @Nullable
    public String getToken() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_AUTH_TOKEN, null);
    }
}
