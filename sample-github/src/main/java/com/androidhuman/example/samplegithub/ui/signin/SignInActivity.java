package com.androidhuman.example.samplegithub.ui.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidhuman.example.samplegithub.BuildConfig;
import com.androidhuman.example.samplegithub.R;
import com.androidhuman.example.samplegithub.api.AuthApi;
import com.androidhuman.example.samplegithub.api.GithubApiProvider;
import com.androidhuman.example.samplegithub.api.model.GithubAccessToken;
import com.androidhuman.example.samplegithub.data.AuthTokenProvider;
import com.androidhuman.example.samplegithub.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    Button btnStart;
    ProgressBar progress;
    AuthApi api;
    AuthTokenProvider authTokenProvider;

    Call<GithubAccessToken> accessTokenCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // UI view 요소들 findViewById로 R 주소랑 맵핑
        btnStart = findViewById(R.id.btnActivitySignInStart);
        progress = findViewById(R.id.pbActivitySignIn);

        // start button setOnClickListener 등록
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 사용자 인증 처리를 위한 Url 구성
                // 형식 : https://github.com/login/oauth/authorize?client_id=application_client_id
                Uri authUri = new Uri.Builder().scheme("https").authority("github.com")
                        // 경로 추가 "/~/ : 로그인 기능 수행
                        .appendPath("login")
                        // 경로 추가 "/~/ : oauth 인증 방법 사용
                        .appendPath("oauth")
                        // 경로 추가 "/~/ : authorize. 인증 수행
                        .appendPath("authorize")
                        // module build.gradle 파일에 정의된 buildConfigField 'client id' 참조하여 url 쿼리로 넘겨줌
                        .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                        .build();
                Log.i("TAG", "Uri: " + authUri);

                // 신속한 뷰 처리를 위해, webView 사용하는 것이 아니라 CustomTabsIntent 사용, 크롬 탭의 UI 커스텀
                // 주의할 점 : 기기의 기본 웹 브라우저가 크롬이 아닌 다른 브라우저로 설정되어 있으면 크롬 탭이 열리지 않는다.
                // 문서 : https://developer.android.com/reference/android/support/customtabs/CustomTabsIntent
                // 설명 : https://sirubomber.tistory.com/38
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(SignInActivity.this, authUri);
            }
        });

        api = GithubApiProvider.provideAuthApi();
        authTokenProvider = new AuthTokenProvider(this);

        if (null != authTokenProvider.getToken()) {
            launchMainActivity();
        }
    }

    // web에서 인증이 종료시, samplegithub://authorize?code={액세스 토큰 교환용 코드} 주소로 반환.
    // manifest.xml의 signinactivity의 intent-filter를 참조하면, 이 액티비티가 해당 주소를 열 수 있음.
    // CustomTabsIntent 호출로 인해서, web 브라우저로 SigninActivity가 가려졌다가 인증 종료 시 다시 화면에 띄워짐.
    // 이 경우, 이미 SigninActivity가 화면에 표시된 상태라 onCreate()가 호출되지 않고 onNewIntent()가 호출됨.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showProgress();

        // 사용자 인증 후 리디렉션된 주소를 가져옴
        Uri uri = intent.getData();
        if (null == uri) {
            throw new IllegalArgumentException("No data exists");
        }

        // 리디렉션 주소에서 액세스 토큰 교환에 필요한 코드 데이터를 parsing
        String code = uri.getQueryParameter("code");
        if (null == code) {
            throw new IllegalStateException("No code exists");
        }

        getAccessToken(code);
    }

    // 주소에서 받은 액세스 토큰을 가지고 실제 토큰을 받는 함수
    private void getAccessToken(@NonNull String code) {
        showProgress();

        // 교환용 코드, client id, client secret code를 활용해 액세스 토큰 받급받는 REST API
        accessTokenCall = api.getAccessToken(
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code);

        // enqueue 메소드를 사용한 비동기 호출 처리
        // 요청에 대한 처리 완료시 사용할 콜백 등록
        accessTokenCall.enqueue(new Callback<GithubAccessToken>() {
            @Override
            public void onResponse(Call<GithubAccessToken> call, Response<GithubAccessToken> response) {
                hideProgress();

                GithubAccessToken token = response.body();
                if (response.isSuccessful() && null != token) {
                    // 발급받은 토큰 업데이트 -> 저장
                    authTokenProvider.updateToken(token.accessToken);
                    launchMainActivity();

                } else {
                    showError(new IllegalStateException("Not successful: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<GithubAccessToken> call, Throwable t) {
                hideProgress();
                showError(t);
            }
        });
    }

    private void showProgress() {
        btnStart.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        btnStart.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void launchMainActivity() {
        startActivity(new Intent(
                SignInActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 액티비티 스택에서 제거
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // 새로운 액티비티를 추가
    }
}
