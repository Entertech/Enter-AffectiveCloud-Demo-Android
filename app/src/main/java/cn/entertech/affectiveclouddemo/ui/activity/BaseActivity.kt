package cn.entertech.affectiveclouddemo.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.MotionEvent
import cn.entertech.affectiveclouddemo.utils.LogManager


var currentActivity: String = ""
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var activity = this.toString().substring(0, this.toString().indexOf("@"))
        currentActivity = activity.split(".")[activity.split(".").size - 1]
        LogManager.getInstance().logPost("View ${currentActivity} onCreate")
    }


    fun initFullScreenDisplay() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

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
}