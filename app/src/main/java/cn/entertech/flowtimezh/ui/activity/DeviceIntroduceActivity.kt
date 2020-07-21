package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceIntroduceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay(false)
        setContentView(R.layout.activity_device_introduce)
        initView()
        EventBus.getDefault().register(this)
    }

    private fun initView() {
        iv_back.setImageResource(R.mipmap.ic_premium_close)
        tv_title.setTextColor(getColorInDarkMode(R.color.common_white_color_light,R.color.common_white_color_dark))
        rl_title_bg.setBackgroundColor(Color.TRANSPARENT)
        findViewById<TextView>(R.id.tv_title).text = getString(R.string.device_introduce_meet_flowtime)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    fun connectDevice(view: View) {
        startActivity(Intent(this, DevicePermissionActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_DEVICE_CONNECT)
            finish()
    }

    fun onKnowMore(view: View){
        var uri = Uri.parse(SettingManager.getInstance().remoteConfigFlowtimeHeadhandIntro)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
