package cn.entertech.flowtimezh.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.app.SettingManager;

/**
 * Created by EnterTech on 2017/7/5.
 */

public class LaunchActivity extends BaseActivity {

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
        initFullScreenDisplay();
        setStatusBarLight();
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
        if ("".equals(SettingManager.getInstance().getToken())) {
            startActivity(new Intent(LaunchActivity.this, AuthActivity.class));
            finish();
        } else {
            startActivity(new Intent(LaunchActivity.this, MainActivity.class));
            finish();
        }
    }
}
