package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R

class DeviceCanNotConnectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLight()
        setContentView(R.layout.activity_can_not_connect)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_title).visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
    }
}
