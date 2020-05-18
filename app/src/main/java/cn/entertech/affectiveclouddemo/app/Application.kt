package cn.entertech.affectiveclouddemo.app

import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.stat.StatConfig
import com.tencent.stat.StatService


class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        initLogger()
        initMta()
        initBugly()
        DeviceUIConfig.getInstance(this).init(false,false,1)
    }


    fun initBugly(){
        CrashReport.initCrashReport(applicationContext, "a02bf4189a", false);
    }

    fun initMta(){
        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
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