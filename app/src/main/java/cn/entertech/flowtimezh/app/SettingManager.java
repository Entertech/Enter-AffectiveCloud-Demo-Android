package cn.entertech.flowtimezh.app;

import android.content.Context;
import android.content.SharedPreferences;

import cn.entertech.flowtimezh.R;

import static cn.entertech.flowtimezh.app.Constant.APP_COULD_UPDATE;
import static cn.entertech.flowtimezh.app.Constant.APP_RATE;
import static cn.entertech.flowtimezh.app.Constant.BLE_FIRMWARE;
import static cn.entertech.flowtimezh.app.Constant.BLE_FIRMWARE_CLOUD;
import static cn.entertech.flowtimezh.app.Constant.BLE_FIRMWARE_CLOUD_UPDATE_NOTES;
import static cn.entertech.flowtimezh.app.Constant.BLE_HARDWARE;
import static cn.entertech.flowtimezh.app.Constant.BLE_MAC;
import static cn.entertech.flowtimezh.app.Constant.CAN_ACCESS_PREMIUM;
import static cn.entertech.flowtimezh.app.Constant.CUSTOM_MEDITATION_TIME;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_ATTENTION_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_ATTENTION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_BRAIN_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_BRAIN_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_COHERENCE_REALTIME;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_EEG_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_FILL_ADDRESS;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_FLOWTIME_HEADHAND_INTRO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_HELP_CENTER;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_HRV_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_HRV_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_HR_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_HR_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_PRESSURE_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_PRESSURE_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_PRIVACY;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_RELAXATION_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_RELAXATION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_LINK_TERMS_OF_USER;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_RATE_CONDITION;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_SHARE_CONDITION;
import static cn.entertech.flowtimezh.app.Constant.DEFAULT_STATISTICS_VIEW_ORDER;
import static cn.entertech.flowtimezh.app.Constant.IS_CONNECT_BEFORE;
import static cn.entertech.flowtimezh.app.Constant.IS_CUSTOM_MEDITATION_TIME;
import static cn.entertech.flowtimezh.app.Constant.IS_FIRST_LOAD_VIEW;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_CONTACT;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_FLOWTIME;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_JOURNEY;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_MY_COURSE;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_TOP_COURSE;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_UNGUIDE_MEDITATION;
import static cn.entertech.flowtimezh.app.Constant.IS_TIP_UP_NEXT;
import static cn.entertech.flowtimezh.app.Constant.ITEM_PURCHASE;
import static cn.entertech.flowtimezh.app.Constant.LOCAL_VERSION_NAME;
import static cn.entertech.flowtimezh.app.Constant.LOG_TOKEN;
import static cn.entertech.flowtimezh.app.Constant.MEDITATIONE_EDN_SOUND_INDEX;
import static cn.entertech.flowtimezh.app.Constant.MEDITATIONE_EDN_SOUND_RES;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_TIME;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_TIME_IS_CUSTOM;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_TIME_IS_RESET;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_TIME_SELECTED_INDEX;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_UNGUIDED_TIME;
import static cn.entertech.flowtimezh.app.Constant.MEDITATION_VIEW_ORDER;
import static cn.entertech.flowtimezh.app.Constant.REFRESH_TOKEN;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_ATTENTION_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_ATTENTION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_BRAIN_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_BRAIN_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_COHERENCE_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_EEG_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_FILL_ADDRESS;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_HELP_CENTER;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_HRV_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_HRV_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_HR_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_HR_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_PRESSURE_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_PRESSURE_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_PRIVACY;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_PRODUCT_PRICE;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_RATE_CONDITION;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_RELAXATION_REALTIME_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_RELAXATION_REPORT_INFO;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_SHARE_CONDITION;
import static cn.entertech.flowtimezh.app.Constant.REMOTE_CONFIG_TERMS_OF_USER;
import static cn.entertech.flowtimezh.app.Constant.SOCIAL_IMAGE;
import static cn.entertech.flowtimezh.app.Constant.SOCIAL_TYPE;
import static cn.entertech.flowtimezh.app.Constant.SOCIAL_USER_ID;
import static cn.entertech.flowtimezh.app.Constant.SOCIAL_USER_NAME;
import static cn.entertech.flowtimezh.app.Constant.SP_DEFAULT_FIRMWARE_URL;
import static cn.entertech.flowtimezh.app.Constant.SP_SERVER_APP_VERSION;
import static cn.entertech.flowtimezh.app.Constant.SP_SERVER_FIRMWARE_URL;
import static cn.entertech.flowtimezh.app.Constant.SP_SERVER_FIRMWARE_VERSION;
import static cn.entertech.flowtimezh.app.Constant.SP_SETTING;
import static cn.entertech.flowtimezh.app.Constant.STATISTICS_VIEW_ORDER;
import static cn.entertech.flowtimezh.app.Constant.TOKEN;
import static cn.entertech.flowtimezh.app.Constant.USER_ID;


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

    public synchronized void setLogToken(String token) {
        getEditor().putString(LOG_TOKEN, "JWT " + token).apply();
    }

    public synchronized String getLogToken() {
        return getSharedPreferences().getString(LOG_TOKEN, "");
    }

    public synchronized void setToken(String token) {
        getEditor().putString(TOKEN, "Bearer " + token).apply();
    }

    public synchronized String getToken() {
        return getSharedPreferences().getString(TOKEN, "Bearer 7TaNz2wbBDujxDjCBReAAX5pQ6LxLfvRJGot4RuO");
    }

    public synchronized void setIsCustomMeditationTime(boolean flag){
        getEditor().putBoolean(IS_CUSTOM_MEDITATION_TIME, flag).apply();
    }

    public synchronized boolean isCustomMeditationTime(){
        return getSharedPreferences().getBoolean(IS_CUSTOM_MEDITATION_TIME,false);
    }

    public synchronized void setMeditationTime(int time){
        getEditor().putInt(MEDITATION_TIME, time).apply();
    }

    public synchronized int getMeditationTime(){
        return getSharedPreferences().getInt(MEDITATION_TIME,10);
    }
    public synchronized void setCustomMeditationTime(int time){
        getEditor().putInt(CUSTOM_MEDITATION_TIME, time).apply();
    }

    public synchronized int getCustomMeditationTime(){
        return getSharedPreferences().getInt(CUSTOM_MEDITATION_TIME,0);
    }

    public synchronized void setUserId(int userId) {
        getEditor().putInt(USER_ID, userId).apply();
    }

    public synchronized int getUserId() {
        return getSharedPreferences().getInt(USER_ID, 4);
    }

    public synchronized void setSocialUserId(String userId) {
        getEditor().putString(SOCIAL_USER_ID, userId).apply();
    }

    public synchronized String getSocialUserId() {
        return getSharedPreferences().getString(SOCIAL_USER_ID, "");
    }

    public synchronized void setSocialUserName(String name) {
        getEditor().putString(SOCIAL_USER_NAME, name).apply();
    }

    public synchronized String getSocialUserName() {
        return getSharedPreferences().getString(SOCIAL_USER_NAME, "");
    }

    public synchronized void setSocialImage(String name) {
        getEditor().putString(SOCIAL_IMAGE, name).apply();
    }

    public synchronized String getSocialImage() {
        return getSharedPreferences().getString(SOCIAL_IMAGE, "");
    }

    public synchronized void setSocialType(String name) {
        getEditor().putString(SOCIAL_TYPE, name).apply();
    }

    public synchronized String getSocialType() {
        return getSharedPreferences().getString(SOCIAL_TYPE, "");
    }


    public synchronized void setRefreshToken(String token) {
        getEditor().putString(REFRESH_TOKEN, token).apply();
    }

    public synchronized String getRefreshToken() {
        return getSharedPreferences().getString(REFRESH_TOKEN, "");
    }

    public synchronized void setPremiumAccessable(boolean isAccessable) {
        getEditor().putBoolean(CAN_ACCESS_PREMIUM, isAccessable).apply();
    }

    public synchronized boolean isPremiumAccessable() {
        return getSharedPreferences().getBoolean(CAN_ACCESS_PREMIUM, false);
    }

    public synchronized void setConnectBefore(boolean isConnectBefore) {
        getEditor().putBoolean(IS_CONNECT_BEFORE, isConnectBefore).apply();
    }

    public synchronized boolean isConnectBefore() {
        return getSharedPreferences().getBoolean(IS_CONNECT_BEFORE, false);
    }


    //BLE 相关存储
    public synchronized void setBleMac(String mac) {
        getEditor().putString(BLE_MAC, mac).apply();
    }

    public synchronized String getBleMac() {
        return getSharedPreferences().getString(BLE_MAC, "");
    }

    public synchronized void setBleHardware(String mac) {
        getEditor().putString(BLE_HARDWARE, mac).apply();
    }

    public synchronized String getBleHardware() {
        return getSharedPreferences().getString(BLE_HARDWARE, "");
    }

    public synchronized void setBleFirmware(String mac) {
        getEditor().putString(BLE_FIRMWARE, mac).apply();
    }

    public synchronized String getBleFirmware() {
        return getSharedPreferences().getString(BLE_FIRMWARE, "");
    }
    public synchronized void setBleFirmwareCloud(String mac) {
        getEditor().putString(BLE_FIRMWARE_CLOUD, mac).apply();
    }

    public synchronized String getBleFirmwareCloud() {
        return getSharedPreferences().getString(BLE_FIRMWARE_CLOUD, "");
    }
    public synchronized void setBleFirmwareCloudUpdateNotes(String mac) {
        getEditor().putString(BLE_FIRMWARE_CLOUD_UPDATE_NOTES, mac).apply();
    }

    public synchronized String getBleFirmwareCloudUpdateNotes() {
        return getSharedPreferences().getString(BLE_FIRMWARE_CLOUD_UPDATE_NOTES, "");
    }

    public synchronized void setLocalAppVersion(String version) {
        getEditor().putString(LOCAL_VERSION_NAME, version).apply();
    }

    public synchronized String getLocalAppVersion() {
        return getSharedPreferences().getString(LOCAL_VERSION_NAME, "");
    }

    public synchronized void setIsReportShared(String k, boolean isShared) {
        getEditor().putBoolean(k, isShared).apply();
    }

    public synchronized boolean isReportShared(String k) {
        return getSharedPreferences().getBoolean(k, false);
    }

    public synchronized void setCouldUpdateApp(boolean flag) {
        getEditor().putBoolean(APP_COULD_UPDATE, flag).apply();
    }

    public synchronized boolean isAppCouldUpdate() {
        return getSharedPreferences().getBoolean(APP_COULD_UPDATE, true);
    }


    public synchronized void setNextUpTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_UP_NEXT, tipped).apply();
    }

    public synchronized boolean isNextUpTip() {
        return getSharedPreferences().getBoolean(IS_TIP_UP_NEXT, true);
    }
    public synchronized void setContactTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_CONTACT, tipped).apply();
    }

    public synchronized boolean isContactTip() {
        return getSharedPreferences().getBoolean(IS_TIP_CONTACT, true);
    }

    public synchronized void setJourneyTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_JOURNEY, tipped).apply();
    }

    public synchronized boolean isJourneyTip() {
        return getSharedPreferences().getBoolean(IS_TIP_JOURNEY, true);
    }

    public synchronized void setUnguideMeditationTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_UNGUIDE_MEDITATION, tipped).apply();
    }

    public synchronized boolean isUnguideMeditationTip() {
        return getSharedPreferences().getBoolean(IS_TIP_UNGUIDE_MEDITATION, false);
    }

    public synchronized void setFlowtimeTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_FLOWTIME, tipped).apply();
    }

    public synchronized boolean isFlowtimeUpTip() {
        return getSharedPreferences().getBoolean(IS_TIP_FLOWTIME, true);
    }

    public synchronized void setMyCourseTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_MY_COURSE, tipped).apply();
    }

    public synchronized boolean isMyCourseTip() {
        return getSharedPreferences().getBoolean(IS_TIP_MY_COURSE, true);
    }

    public synchronized void setTopCourseTip(boolean tipped) {
        getEditor().putBoolean(IS_TIP_TOP_COURSE, tipped).apply();
    }

    public synchronized boolean isTopCourseTip() {
        return getSharedPreferences().getBoolean(IS_TIP_TOP_COURSE, true);
    }

    public synchronized void setMeditationViewOrder(String viewOrder) {
        getEditor().putString(MEDITATION_VIEW_ORDER, viewOrder).apply();
    }

    public synchronized String getMeditationViewOrder() {
        return getSharedPreferences().getString(MEDITATION_VIEW_ORDER, "Heart,Brainwave,Emotion");
    }

    public synchronized void setStatisticsViewOrder(String viewOrder) {
        getEditor().putString(STATISTICS_VIEW_ORDER, viewOrder).apply();
    }

    public synchronized String getStatisticsViewOrder() {
        return getSharedPreferences().getString(STATISTICS_VIEW_ORDER, DEFAULT_STATISTICS_VIEW_ORDER);
    }

    public synchronized void setAppRated(boolean flag) {
        getEditor().putBoolean(APP_RATE, flag).apply();
    }

    public synchronized boolean isAppRated() {
        return getSharedPreferences().getBoolean(APP_RATE, false);
    }

    public synchronized void setRemoteConfigRateCondition(String str) {
        getEditor().putString(REMOTE_CONFIG_RATE_CONDITION, str).apply();
    }

    public synchronized String getRemoteConfigRateCondition() {
        return getSharedPreferences().getString(REMOTE_CONFIG_RATE_CONDITION, DEFAULT_RATE_CONDITION);
    }

    public synchronized void setRemoteConfigShareCondition(String str) {
        getEditor().putString(REMOTE_CONFIG_SHARE_CONDITION, str).apply();
    }

    public synchronized String getRemoteConfigShareCondition() {
        return getSharedPreferences().getString(REMOTE_CONFIG_SHARE_CONDITION, DEFAULT_SHARE_CONDITION);
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

    public synchronized void setRemoteConfigRelaxationReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_RELAXATION_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigRelaxationReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_RELAXATION_REPORT_INFO, DEFAULT_LINK_RELAXATION_REPORT_INFO);
    }

    public synchronized void setRemoteConfigAttentionReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_ATTENTION_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigAttentionReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_ATTENTION_REPORT_INFO, DEFAULT_LINK_ATTENTION_REPORT_INFO);
    }

    public synchronized void setRemoteConfigHRVReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HRV_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRVReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HRV_REPORT_INFO, DEFAULT_LINK_HRV_REPORT_INFO);
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

    public synchronized void setRemoteConfigProductPrice(String str) {
        getEditor().putString(REMOTE_CONFIG_PRODUCT_PRICE, str).apply();
    }

    public synchronized String getRemoteConfigProductPrice() {
        return getSharedPreferences().getString(REMOTE_CONFIG_PRODUCT_PRICE, "$198");
    }

    public synchronized void setRemoteConfigFillAddress(String str) {
        getEditor().putString(REMOTE_CONFIG_FILL_ADDRESS, str).apply();
    }

    public synchronized String getRemoteConfigFillAddress() {
        return getSharedPreferences().getString(REMOTE_CONFIG_FILL_ADDRESS, DEFAULT_LINK_FILL_ADDRESS);
    }

    public synchronized void setRemoteConfigDeviceCanNotConnect(String str) {
        getEditor().putString(REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT, str).apply();
    }

    public synchronized String getRemoteConfigDeviceCanNotConnect() {
        return getSharedPreferences().getString(REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT, DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT);
    }

    public synchronized void setIsFirstLoadView(boolean isFirstLoadView) {
        getEditor().putBoolean(IS_FIRST_LOAD_VIEW, isFirstLoadView).apply();
    }

    public synchronized boolean isFirstLoadView() {
        return getSharedPreferences().getBoolean(IS_FIRST_LOAD_VIEW, true);
    }

    public synchronized void setOwnerItemPurchase(String itemPurchase) {
        getEditor().putString(ITEM_PURCHASE, itemPurchase).apply();
    }

    public synchronized String getOwnerItemPurchase() {
        return getSharedPreferences().getString(ITEM_PURCHASE, "");
    }

    public synchronized void setMeditationUnguideTime(int meditationUnguideTime) {
        getEditor().putInt(MEDITATION_UNGUIDED_TIME, meditationUnguideTime).apply();
    }

    public synchronized int getMeditationUnguideTime() {
        return getSharedPreferences().getInt(MEDITATION_UNGUIDED_TIME, 15);
    }

    public synchronized void setMeditationTimeSelectedInex(int meditationTimeSelectedInex) {
        getEditor().putInt(MEDITATION_TIME_SELECTED_INDEX, meditationTimeSelectedInex).apply();
    }

    public synchronized int getMeditationTimeSelectedInex() {
        return getSharedPreferences().getInt(MEDITATION_TIME_SELECTED_INDEX, 1);
    }

    public synchronized void setMeditationEndSoundIndex(int meditationEndSoundIndex) {
        getEditor().putInt(MEDITATIONE_EDN_SOUND_INDEX, meditationEndSoundIndex).apply();
    }

    public synchronized int getMeditationEndSoundIndex() {
        return getSharedPreferences().getInt(MEDITATIONE_EDN_SOUND_INDEX, 1);
    }

    public synchronized void setMeditationEndSoundRes(int meditationEndSoundRes) {
        getEditor().putInt(MEDITATIONE_EDN_SOUND_RES, meditationEndSoundRes).apply();
    }

    public synchronized int getMeditationEndSoundRes() {
        return getSharedPreferences().getInt(MEDITATIONE_EDN_SOUND_RES, R.raw.meditation_end_bell);
    }

    public synchronized void setIsCustomTime(boolean isCustomTime) {
        getEditor().putBoolean(MEDITATION_TIME_IS_CUSTOM, isCustomTime).apply();
    }

    public synchronized boolean isCustomTime() {
        return getSharedPreferences().getBoolean(MEDITATION_TIME_IS_CUSTOM, false);
    }

    public synchronized void setIsMeditationTimeReset(boolean isTimeReset) {
        getEditor().putBoolean(MEDITATION_TIME_IS_RESET, isTimeReset).apply();
    }

    public synchronized boolean getIsMeditationTimeReset() {
        return getSharedPreferences().getBoolean(MEDITATION_TIME_IS_RESET, true);
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

    public synchronized void setRemoteConfigHRVRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_HRV_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigHRVRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_HRV_REALTIME_INFO, DEFAULT_LINK_HRV_REALTIME_INFO);
    }

    public synchronized void setRemoteConfigAttentionRelaxationReportInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO, str).apply();
    }

    public synchronized String getRemoteConfigAttentionRelaxationReportInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO, DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO);
    }

    public synchronized void setRemoteConfigCoherenceRealtimeInfo(String str) {
        getEditor().putString(REMOTE_CONFIG_COHERENCE_REALTIME_INFO, str).apply();
    }

    public synchronized String getRemoteConfigCoherenceRealtimeInfo() {
        return getSharedPreferences().getString(REMOTE_CONFIG_COHERENCE_REALTIME_INFO, DEFAULT_LINK_COHERENCE_REALTIME);
    }

}
