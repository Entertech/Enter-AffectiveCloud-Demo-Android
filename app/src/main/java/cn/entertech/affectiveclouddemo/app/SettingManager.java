package cn.entertech.affectiveclouddemo.app;

import android.content.Context;
import android.content.SharedPreferences;

import static cn.entertech.affectiveclouddemo.app.Constant.SP_BLE_MAC;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_DEFAULT_FIRMWARE_URL;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_LOG_TOKEN;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_SERVER_APP_VERSION;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_SERVER_FIRMWARE_URL;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_SERVER_FIRMWARE_VERSION;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_SETTING;


/**
 * Created by EnterTech on 2017/1/10.
 */

public class SettingManager {
    private static SettingManager mInstance;
    private android.app.Application mApplication;

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

    public synchronized void setServerFirmwareVersion(String firmwareVersion) {
        getEditor().putString(SP_SERVER_FIRMWARE_VERSION, firmwareVersion).apply();
    }

    public synchronized String getServerFirmwareVersion() {
        return getSharedPreferences().getString(SP_SERVER_FIRMWARE_VERSION, "1.0.0");
    }

    public synchronized void setServerFirmwareUrl(String firmwareUrl) {
        getEditor().putString(SP_SERVER_FIRMWARE_URL, firmwareUrl).apply();
    }

    public synchronized String getServerFirmwareUrl() {
        return getSharedPreferences().getString(SP_SERVER_FIRMWARE_URL, SP_DEFAULT_FIRMWARE_URL);
    }

    public synchronized void setServerAppVersion(String appVersion) {
        getEditor().putString(SP_SERVER_APP_VERSION, appVersion).apply();
    }

    public synchronized String getServerAppVersion() {
        return getSharedPreferences().getString(SP_SERVER_APP_VERSION, "1.0.0");
    }


    public synchronized void setLogToken(String token) {
        getEditor().putString(SP_LOG_TOKEN, "JWT " + token).apply();
    }

    public synchronized String getLogToken() {
        return getSharedPreferences().getString(SP_LOG_TOKEN, "");
    }


    public synchronized void setBleMac(String mac) {
        getEditor().putString(SP_BLE_MAC, mac).apply();
    }

    public synchronized String getBleMac() {
        return getSharedPreferences().getString(SP_BLE_MAC, "");
    }

}
