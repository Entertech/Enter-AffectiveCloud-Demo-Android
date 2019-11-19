package cn.entertech.flowtimezh.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.MotionEvent



open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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

    override fun onResume() {
        super.onResume()
//        Bugtags.onResume(this)
    }

    override fun onPause() {
        super.onPause()
//        Bugtags.onPause(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //注：回调 3
//        Bugtags.onDispatchTouchEvent(this, event)
        return super.dispatchTouchEvent(event)
    }
}