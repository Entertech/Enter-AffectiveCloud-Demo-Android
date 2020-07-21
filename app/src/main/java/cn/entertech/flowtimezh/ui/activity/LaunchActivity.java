package cn.entertech.flowtimezh.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import cn.entertech.flowtimezh.MainActivity;
import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.app.Application;
import cn.entertech.flowtimezh.app.SettingManager;
import cn.entertech.flowtimezh.mvp.model.AuthSocialEntity;
import cn.entertech.flowtimezh.mvp.presenter.AuthSocialPresenter;
import cn.entertech.flowtimezh.mvp.view.AuthSocialView;
import cn.entertech.flowtimezh.utils.ToastUtil;

import static cn.entertech.flowtimezh.app.Constant.CLIENT_ID;


/**
 * Created by EnterTech on 2017/7/5.
 */

public class LaunchActivity extends Activity {
    private AuthSocialPresenter authPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
        setContentView(R.layout.activity_launch);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initClientToken();
                        overridePendingTransition(0, 0);
                    }
                });
            }
        }, 1000);
    }


    private void hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initClientToken() {
        authPresenter = new AuthSocialPresenter(Application.Companion.getInstance());
        authPresenter.onCreate();
        authPresenter.attachView(authSocialView);
        if ("".equals(SettingManager.getInstance().getRefreshToken())) {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            finish();
        } else {
            authPresenter.refreshToken(CLIENT_ID, SettingManager.getInstance().getRefreshToken());
        }
    }

    private AuthSocialView authSocialView = new AuthSocialView() {
        @Override
        public void onSuccess(@NotNull AuthSocialEntity authSocialEntity) {
            SettingManager.getInstance().setToken(authSocialEntity.getAccess_token());
            SettingManager.getInstance().setRefreshToken(authSocialEntity.getRefresh_token());
            startActivity(new Intent(LaunchActivity.this, MainActivity.class));
            finish();
        }

        @Override
        public void onError(@NotNull String error) {
            ToastUtil.toastShort(LaunchActivity.this, error);
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        if (authPresenter != null){
            authPresenter.onStop();
        }
        super.onDestroy();
    }
}
