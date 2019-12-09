package cn.entertech.flowtimezh.app;

import android.content.Context;
import android.content.SharedPreferences;

import static cn.entertech.flowtimezh.app.Constant.SP_APP_KEY;
import static cn.entertech.flowtimezh.app.Constant.SP_APP_SECRET;
import static cn.entertech.flowtimezh.app.Constant.SP_SETTING;
import static cn.entertech.flowtimezh.app.Constant.SP_TOKEN;


/**
 * Created by EnterTech on 2017/1/10.
 */

public class SettingManager {
    private static SettingManager mInstance;
    private Application mApplication;

    public static SettingManager getInstance() {
        if (null == mInstance) {
            synchronized (SettingManager.class) {
                if (null == mInstance) {
                    mInstance = new SettingManager(Application.Companion.getInstance());
                }
            }
        }
        return mInstance;
    }

    private SettingManager(Application application) {
        mApplication = application;
    }

    private SharedPreferences getSharedPreferences() {
        return mApplication.getSharedPreferences(SP_SETTING, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public synchronized void setAppKey(String appKey) {
        getEditor().putString(SP_APP_KEY,appKey).apply();
    }

    public synchronized String getAppKey() {
        return getSharedPreferences().getString(SP_APP_KEY, "");
    }

    public synchronized void setAppSecret(String appSecret) {
        getEditor().putString(SP_APP_SECRET, appSecret).apply();
    }

    public synchronized String getAppSecret() {
        return getSharedPreferences().getString(SP_APP_SECRET, "");
    }
    public synchronized void setToken(String token) {
        getEditor().putString(SP_TOKEN, token).apply();
    }

    public synchronized String getToken() {
        return getSharedPreferences().getString(SP_TOKEN, "");
    }
}
