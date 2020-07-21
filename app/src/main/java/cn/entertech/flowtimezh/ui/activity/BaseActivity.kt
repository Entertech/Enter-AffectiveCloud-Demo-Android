package cn.entertech.flowtimezh.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.getDarkModeStatus


var currentActivity: String = ""
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var activity = this.toString().substring(0, this.toString().indexOf("@"))
        currentActivity = activity.split(".")[activity.split(".").size - 1]
        LogManager.getInstance().logPost("View ${currentActivity} onCreate")
    }


    fun initFullScreenDisplay(isStatusBarLight:Boolean = true) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        if (!getDarkModeStatus(this) && isStatusBarLight) {
            setStatusBarLight()
        }

    }

    //修改状态栏字体
    fun setStatusBarLight() {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onDestroy() {
        super.onDestroy()
        LogManager.getInstance().logPost("View ${currentActivity} onDestroy")
    }

    fun getColorInDarkMode(lightColor: Int, darkColor: Int): Int {
        if (getDarkModeStatus(this)) {
            return ContextCompat.getColor(
                this,
                darkColor
            )
        } else {
            return ContextCompat.getColor(
                this,
                lightColor
            )
        }
    }
}