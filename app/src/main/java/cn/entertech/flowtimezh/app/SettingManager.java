package cn.entertech.flowtimezh.app;

import android.content.Context;
import android.content.SharedPreferences;

import static cn.entertech.flowtimezh.app.Constant.DEVICE_TYPE_HEADBAND;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_VIEW_ORDER;
import static cn.entertech.flowtimezh.app.Constant.SP_AFFECTIVE_CLOUD_SERVER;
import static cn.entertech.flowtimezh.app.Constant.SP_API_SERVER;
import static cn.entertech.flowtimezh.app.Constant.SP_APP_KEY;
import static cn.entertech.flowtimezh.app.Constant.SP_APP_SECRET;
import static cn.entertech.flowtimezh.app.Constant.SP_CURRENT_SERVER;
import static cn.entertech.flowtimezh.app.Constant.SP_DEVICE_TYPE;
import static cn.entertech.flowtimezh.app.Constant.SP_SETTING;
import static cn.entertech.flowtimezh.app.Constant.SP_TIME_COUNT_EEG;
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

    public synchronized void setAffectiveCloudServer(String url) {
        getEditor().putString(SP_AFFECTIVE_CLOUD_SERVER,url).apply();
    }

    public synchronized String getAffectiveCloudServer() {
        return getSharedPreferences().getString(SP_AFFECTIVE_CLOUD_SERVER, "server.affectivecloud.cn");
    }

    public synchronized void setApiServer(String url) {
        getEditor().putString(SP_API_SERVER,url).apply();
    }

    public synchronized String getApiServer() {
        return getSharedPreferences().getString(SP_API_SERVER, "api.affectivecloud.cn");
    }

    public synchronized void setCurrentServer(int currentServer) {
        getEditor().putInt(SP_CURRENT_SERVER,currentServer).apply();
    }

    public synchronized int getCurrentServer() {
        return getSharedPreferences().getInt(SP_CURRENT_SERVER, 0);
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


    public synchronized void setMeditationViewOrder(String viewOrder) {
        getEditor().putString(MEDITATION_VIEW_ORDER, viewOrder).apply();
    }

    public synchronized String getMeditationViewOrder() {
        return getSharedPreferences().getString(MEDITATION_VIEW_ORDER, "0,1,2");
    }
    public synchronized void setDeviceType(String deviceType) {
        getEditor().putString(SP_DEVICE_TYPE, deviceType).apply();
    }

    public synchronized String getDeviceType() {
        return getSharedPreferences().getString(SP_DEVICE_TYPE, DEVICE_TYPE_HEADBAND);
    }

    public synchronized void setTimeCountIsEEG(boolean isEEG) {
        getEditor().putBoolean(SP_TIME_COUNT_EEG, isEEG).apply();
    }

    public synchronized boolean timeCountIsEEG() {
        return getSharedPreferences().getBoolean(SP_TIME_COUNT_EEG, true);
    }

}
