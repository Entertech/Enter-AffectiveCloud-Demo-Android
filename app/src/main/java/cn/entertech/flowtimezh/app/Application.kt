package cn.entertech.flowtimezh.app

import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        initLogger()

        DeviceUIConfig.getInstance(this).init(false,false,1)
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