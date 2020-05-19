package cn.entertech.affectiveclouddemo.utils

import android.util.Log
import cn.entertech.affectiveclouddemo.app.Application
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.mvp.presenter.LogAuthPresenter
import cn.entertech.affectiveclouddemo.mvp.presenter.LogPostPresenter
import cn.entertech.affectiveclouddemo.mvp.view.LogAuthView
import cn.entertech.affectiveclouddemo.mvp.view.LogPostView
import cn.entertech.flowtime.mvp.model.LogAuthResult
import cn.entertech.flowtime.mvp.model.LogPostResult
import com.orhanobut.logger.Logger

class LogManager {
    var mLogPostPresenter: LogPostPresenter? = null
    var mLogAuthPresenter: LogAuthPresenter? = null

    var mLogAuthView = object : LogAuthView {
        override fun onError(error: String) {
            Logger.d("log auth failed:${error}")
        }

        override fun onSuccess(result: LogAuthResult?) {
            Logger.d("log auth success:${result?.token}")
            SettingManager.getInstance().logToken = result?.token
        }

    }

    var mLogPostView = object : LogPostView {
        override fun onError(error: String) {
            Logger.d("log post failed:${error}")
        }

        override fun onSuccess(result: LogPostResult?) {
            Logger.d("log post success")
        }

    }

    init {
        mLogAuthPresenter = LogAuthPresenter(Application.getInstance())
        mLogPostPresenter = LogPostPresenter(Application.getInstance())
        mLogAuthPresenter?.attachView(mLogAuthView)
        mLogPostPresenter?.attachView(mLogPostView)
        mLogAuthPresenter?.onCreate()
        mLogPostPresenter?.onCreate()
    }

    companion object {
        const val LOG_ACCOUNT = "heartflow"
        const val LOG_PASSWORD = "t]9m18|\"Q(Y!SfV["
        const val LOG_EVENT_BLE_SCAN = "Bluetooth scanning"
        const val LOG_EVENT_BLE_SCAN_COMPLETE = "Bluetooth scan complete"
        const val LOG_EVENT_BLE_SCAN_FAILED = "Bluetooth scan failed"
        const val LOG_EVENT_BLE_CONNECTING = "Bluetooth connecting"
        const val LOG_EVENT_BLE_CONNECT_COMPLETE = "Bluetooth connect complete"
        const val LOG_EVENT_BLE_CONNECT_FAILED = "Bluetooth connect failed"
        const val LOG_EVENT_BLE_DISCONNECTED = "Bluetooth disconnected"
        const val LOG_EVENT_BLE_START_EEG = "Bluetooth start eeg"
        const val LOG_EVENT_BLE_START_HR = "Bluetooth start hr"
        const val LOG_EVENT_BLE_FINISH_EEG = "Bluetooth finish eeg"
        const val LOG_EVENT_BLE_FINISH_HR = "Bluetooth finish hr"

        const val LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECTING = "AffectiveCloud websocket connecting"
        const val LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECT_COMPLETE =
            "AffectiveCloud websocket connect complete"
        const val LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECT_DISCONNECT =
            "AffectiveCloud websocket disconnect"
        const val LOG_EVENT_AFFECTIVE_CLOUD_INIT = "AffectiveCloud init"
        const val LOG_EVENT_AFFECTIVE_CLOUD_INIT_COMPLETE = "AffectiveCloud init complete"
        const val LOG_EVENT_AFFECTIVE_CLOUD_INIT_FAILED = "AffectiveCloud init failed"
        const val LOG_EVENT_AFFECTIVE_CLOUD_RESTORE = "AffectiveCloud restore"
        const val LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_COMPLETE = "AffectiveCloud restore complete"
        const val LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_FAILED = "AffectiveCloud restore failed"
        const val LOG_EVENT_AFFECTIVE_CLOUD_BIODATA = "AffectiveCloud biodata"
        const val LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA = "AffectiveCloud affective data"
        const val LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GETTING =
            "AffectiveCloud biodata report getting"
        const val LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GET_COMPLETE =
            "AffectiveCloud biodata report get complete"
        const val LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GET_FAILED =
            "AffectiveCloud biodata report get failed"
        const val LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GETTING =
            "AffectiveCloud affective data report getting"
        const val LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GET_COMPLETE =
            "AffectiveCloud affective data report get complete"
        const val LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GET_FAILED =
            "AffectiveCloud affective data report get failed"
        const val LOG_EVENT_AFFECTIVE_CLOUD_FINISHING = "AffectiveCloud finishing"
        const val LOG_EVENT_AFFECTIVE_CLOUD_FINISH_COMPLETE = "AffectiveCloud finish complete"
        const val LOG_EVENT_AFFECTIVE_CLOUD_FINISH_FAILED = "AffectiveCloud finish failed"

        const val LOG_EVENT_PERMISSION_WRITE_EXTERNAL_STORAGE_APPLY =
            "Permission WRITE_EXTERNAL_STORAGE apply "
        const val LOG_EVENT_PERMISSION_WRITE_EXTERNAL_STORAGE_GRANTED =
            "Permission WRITE_EXTERNAL_STORAGE granted"
        const val LOG_EVENT_PERMISSION_WRITE_EXTERNAL_STORAGE_DENIED =
            "Permission WRITE_EXTERNAL_STORAGE denied"
        const val LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_APPLY =
            "Permission ACCESS_COARSE_LOCATION apply"
        const val LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_GRANTED =
            "Permission ACCESS_COARSE_LOCATION granted"
        const val LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_DENIED =
            "Permission ACCESS_COARSE_LOCATION denied"

        @Volatile
        var mInstance: LogManager? = null

        fun getInstance(): LogManager {
            if (mInstance == null) {
                synchronized(LogManager::class.java) {}
                if (mInstance == null) {
                    mInstance = LogManager()
                }
            }
            return mInstance!!
        }
    }

    fun logPost(
        event: String,
        message: String = ""
    ) {
        mLogPostPresenter?.logPost(
            LOG_ACCOUNT,
            "android",
            getAppVersionName(Application.getInstance()),
            SettingManager.getInstance().bleMac,
            TimeUtils.getFormatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"),
            event,
            message
        )
    }

    fun logAuth() {
        mLogAuthPresenter?.logAuth(LOG_ACCOUNT, LOG_PASSWORD)
    }

}