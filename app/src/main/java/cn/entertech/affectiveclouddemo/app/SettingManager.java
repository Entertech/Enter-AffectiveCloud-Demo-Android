package cn.entertech.affectiveclouddemo.app;

import android.content.Context;
import android.content.SharedPreferences;

import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_ATTENTION_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_ATTENTION_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_BRAIN_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_BRAIN_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_COHERENCE_REALTIME;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_EEG_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_FLOWTIME_HEADHAND_INTRO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_HELP_CENTER;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_HRV_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_HRV_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_HR_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_HR_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_PRESSURE_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_PRESSURE_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_PRIVACY;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_RELAXATION_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.DEFAULT_LINK_TERMS_OF_USER;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_ATTENTION_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_BRAIN_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_BRAIN_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_COHERENCE_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_EEG_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_HELP_CENTER;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_HRV_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_HRV_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_HR_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_HR_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_PRESSURE_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_PRESSURE_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_PRIVACY;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_RELAXATION_REALTIME_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_RELAXATION_REPORT_INFO;
import static cn.entertech.affectiveclouddemo.app.Constant.REMOTE_CONFIG_TERMS_OF_USER;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_BLE_MAC;
import static cn.entertech.affectiveclouddemo.app.Constant.SP_BRAIN_CHART_LEGEND_SHOW_LIST;
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


    public synchronized void setRemoteConfigHelpCenter(String str) {
        getEditor().putString(REMOTE_CONFIG_HELP_CENTER, str).apply();
    }

    public synchronized String getRemoteConfigHelpCenter() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HELP_CENTER, DEFAULT_LINK_HELP_CENTER);
    }

    public synchronized void setRemoteConfigPressureReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_PRESSURE_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigPressureReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_PRESSURE_REPORT_INFO, DEFAULT_LINK_PRESSURE_REPORT_INFO);
    }

    public synchronized void setRemoteConfigAttentionRelaxationReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigAttentionRelaxationReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO, DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO);
    }

    public synchronized void setRemoteConfigHRVReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HRV_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRVReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HRV_REPORT_INFO, DEFAULT_LINK_HRV_REPORT_INFO);
    }
    public synchronized void setRemoteConfigHRVRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HRV_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRVRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HRV_REALTIME_INFO, DEFAULT_LINK_HRV_REALTIME_INFO);
    }

    public synchronized void setRemoteConfigHRReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HR_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HR_REPORT_INFO, DEFAULT_LINK_HR_REPORT_INFO);
    }

    public synchronized void setRemoteConfigBrainReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_BRAIN_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigBrainReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_BRAIN_REPORT_INFO, DEFAULT_LINK_BRAIN_REPORT_INFO);
    }


    public synchronized void setRemoteConfigPressureRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_PRESSURE_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigPressureRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_PRESSURE_REALTIME_INFO, DEFAULT_LINK_PRESSURE_REALTIME_INFO);
    }

    public synchronized void setRemoteConfigRelaxationRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_RELAXATION_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigRelaxationRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_RELAXATION_REALTIME_INFO, DEFAULT_LINK_RELAXATION_REALTIME_INFO);
    }

    public synchronized void setRemoteConfigAttentionRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_ATTENTION_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigAttentionRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_ATTENTION_REALTIME_INFO, DEFAULT_LINK_ATTENTION_REALTIME_INFO);
    }


    public synchronized void setRemoteConfigBrainRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_BRAIN_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigBrainRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_BRAIN_REALTIME_INFO, DEFAULT_LINK_BRAIN_REALTIME_INFO);
    }


    public synchronized void setRemoteConfigEEGRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_EEG_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigEEGRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_EEG_REALTIME_INFO, DEFAULT_LINK_EEG_REALTIME_INFO);
    }


    public synchronized void setRemoteConfigHRRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HR_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HR_REALTIME_INFO, DEFAULT_LINK_HR_REALTIME_INFO);
    }


    public synchronized void setRemoteConfigFlowtimeHeadhandIntro(String str) {
        getEditor().putString(REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO, str).apply();
    }

    public synchronized String getRemoteConfigFlowtimeHeadhandIntro() {
        return getSharedPreferences().getString(REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO, DEFAULT_LINK_FLOWTIME_HEADHAND_INTRO);
    }


    public synchronized void setRemoteConfigTermsOfUser(String str) {
        getEditor().putString(REMOTE_CONFIG_TERMS_OF_USER, str).apply();
    }

    public synchronized String getRemoteConfigTermsOfUser() {
        return getSharedPreferences().getString(REMOTE_CONFIG_TERMS_OF_USER, DEFAULT_LINK_TERMS_OF_USER);
    }


    public synchronized void setRemoteConfigPrivacy(String str) {
        getEditor().putString(REMOTE_CONFIG_PRIVACY, str).apply();
    }

    public synchronized String getRemoteConfigPrivacy() {
        return getSharedPreferences().getString(REMOTE_CONFIG_PRIVACY, DEFAULT_LINK_PRIVACY);
    }

    public synchronized void setRemoteConfigDeviceCanNotConnect(String str) {
        getEditor().putString(REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT, str).apply();
    }

    public synchronized String getRemoteConfigDeviceCanNotConnect() {
        return getSharedPreferences().getString(REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT, DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT);
    }


    public synchronized void setRemoteConfigCoherenceRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_COHERENCE_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigCoherenceRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_COHERENCE_REALTIME_INFO, DEFAULT_LINK_COHERENCE_REALTIME);
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

    public synchronized void setBrainChartLegendShowList(String flag) {
        getEditor().putString(SP_BRAIN_CHART_LEGEND_SHOW_LIST, flag).apply();
    }

    public synchronized String getBrainChartLegendShowList() {
        return getSharedPreferences().getString(SP_BRAIN_CHART_LEGEND_SHOW_LIST, "1,1,1,1,1");
    }

}
