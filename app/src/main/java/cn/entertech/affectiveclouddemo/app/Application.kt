package cn.entertech.affectiveclouddemo.app

import android.util.Log
import cn.entertech.affectiveclouddemo.app.Constant.Companion.MTA_APP_VERSION
import cn.entertech.affectiveclouddemo.app.Constant.Companion.MTA_FIRMWARE_URL
import cn.entertech.affectiveclouddemo.app.Constant.Companion.MTA_FIRMWARE_VERSION
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.stat.StatConfig
import com.tencent.stat.StatService
import java.io.File


class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        initLogger()
        initMta()
        initBugly()
        downloadFirmware()
        DeviceUIConfig.getInstance(this).init(false, false, 1)
    }

    fun initBugly() {
        CrashReport.initCrashReport(this, "a02bf4189a", true)
    }

    fun initMta() {
        StatConfig.setDebugEnable(false);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this)
        Log.d("#######","mta firmware is ${StatConfig.getCustomProperty(this, MTA_FIRMWARE_VERSION)}")
        SettingManager.getInstance().serverFirmwareVersion =
            StatConfig.getCustomProperty(this, MTA_FIRMWARE_VERSION)
        SettingManager.getInstance().serverFirmwareUrl =
            StatConfig.getCustomProperty(this, MTA_FIRMWARE_URL)
        SettingManager.getInstance().serverAppVersion =
            StatConfig.getCustomProperty(this, MTA_APP_VERSION)
    }

    fun initLogger() {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun downloadFirmware() {
        var downloadlistener = object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
                Log.d("#####", "download firmware warn:$task")
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                Log.d("#####", "download firmware error:$e")
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

                Log.d("#####", "download firmware progress:$soFarBytes")
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun completed(task: BaseDownloadTask?) {
            }
        }
        var firmwareFilePath =
            "${this.filesDir}${File.separator}${SettingManager.getInstance().serverFirmwareVersion}.zip"
        var firmwareUrl =
            "${SettingManager.getInstance().serverFirmwareUrl}${SettingManager.getInstance().serverFirmwareVersion}"
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