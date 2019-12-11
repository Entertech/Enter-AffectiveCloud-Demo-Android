package cn.entertech.flowtimezh.app

import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport


class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        initLogger()
        initBugly()
        DeviceUIConfig.getInstance(this).init(false, false, 1)
    }


    fun initBugly() {
        CrashReport.initCrashReport(applicationContext, "02c3f299d0", false)
    }

    fun initLogger() {
        Logger.addLogAdapter(AndroidLogAdapter())
    }


    companion object {
        var application: Application? = null
        fun getInstance(): Application {
            return application!!
        }
    }

}