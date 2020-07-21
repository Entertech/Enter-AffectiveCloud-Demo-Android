package cn.entertech.flowtimezh.app

import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_APP_VERSION
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_ATTENTION_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_BRAIN_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_BRAIN_REPORT_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_COHERENCE_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_EEG_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_FIRMWARE_URL
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_FIRMWARE_VERSION
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_HELP_CENTER
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_HRV_REPORT_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_HR_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_HR_REPORT_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_PRESSURE_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_PRESSURE_REPORT_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_PRIVACY
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_RELAXATION_REALTIME_INFO
import cn.entertech.flowtimezh.app.Constant.Companion.REMOTE_CONFIG_TERMS_OF_USER
import cn.entertech.flowtimezh.app.Constant.Companion.WX_LOGIN_APP_ID
import cn.entertech.flowtimezh.utils.LogManager
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.stat.StatConfig
import com.tencent.stat.StatService
import java.io.File


class Application : android.app.Application() {

    private var mWXApi: IWXAPI? = null

    override fun onCreate() {
        super.onCreate()
        application = this
        initLogger()
        initMta()
        initLogSystem()
        initBugly()
        initWX()
        downloadFirmware()
    }

    private fun initWX() {
        mWXApi = WXAPIFactory.createWXAPI(this, WX_LOGIN_APP_ID, false)
        mWXApi?.registerApp(WX_LOGIN_APP_ID)
    }

    fun getWXApi(): IWXAPI? {
        return mWXApi
    }
    private fun initLogSystem() {
        LogManager.getInstance().logAuth()
    }

    fun initBugly() {
        CrashReport.initCrashReport(this, "a02bf4189a", true)
    }

    fun initMta() {
        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this)
        SettingManager.getInstance().serverFirmwareVersion =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_FIRMWARE_VERSION)
        SettingManager.getInstance().serverFirmwareUrl =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_FIRMWARE_URL)
        SettingManager.getInstance().serverAppVersion =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_APP_VERSION)
        SettingManager.getInstance().remoteConfigHelpCenter =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_HELP_CENTER)
        SettingManager.getInstance().remoteConfigPressureReportInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_PRESSURE_REPORT_INFO)
        SettingManager.getInstance().remoteConfigAttentionRelaxationReportInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO)
        SettingManager.getInstance().remoteConfigHRVReportInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_HRV_REPORT_INFO)
        SettingManager.getInstance().remoteConfigHRVRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HRV_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigHRReportInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_HR_REPORT_INFO)
        SettingManager.getInstance().remoteConfigBrainReportInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_BRAIN_REPORT_INFO)
        SettingManager.getInstance().remoteConfigPressureRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_PRESSURE_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigRelaxationRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_RELAXATION_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigAttentionRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_ATTENTION_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigBrainRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_BRAIN_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigEEGRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_EEG_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigCoherenceRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_COHERENCE_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigHRRealtimeInfo =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_HR_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigFlowtimeHeadhandIntro =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO)
        SettingManager.getInstance().remoteConfigDeviceCanNotConnect =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT)
        SettingManager.getInstance().remoteConfigTermsOfUser =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_TERMS_OF_USER)
        SettingManager.getInstance().remoteConfigPrivacy =
            StatConfig.getCustomProperty(this, REMOTE_CONFIG_PRIVACY)
    }

    fun initLogger() {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun downloadFirmware() {
        var downloadlistener = object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
//                Log.d("#####", "download firmware warn:$task")
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
//                Log.d("#####", "download firmware error:$e")
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

//                Log.d("#####", "download firmware progress:$soFarBytes")
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun completed(task: BaseDownloadTask?) {
            }
        }
        var firmwareFilePath =
            "${this.filesDir}${File.separator}${SettingManager.getInstance().bleFirmwareCloud}.zip"
        var firmwareUrl =
            "${SettingManager.getInstance().serverFirmwareUrl}${SettingManager.getInstance().bleFirmwareCloud}"
        FileDownloader.setup(this)
        FileDownloader.getImpl().create(firmwareUrl)
            .setPath(firmwareFilePath)
            .setListener(downloadlistener)
            .setForceReDownload(true)
            .start()
    }


    companion object {
        var application: Application? = null
        fun getInstance(): Application {
            return application!!
        }
    }

}