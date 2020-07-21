package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.utils.LogManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe


class DeviceTurnOnActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setStatusBarLight()
        setContentView(R.layout.activity_device_turn_on)
        initView()
        EventBus.getDefault().register(this)
    }


    private fun initView() {
        findViewById<TextView>(R.id.tv_title).text = getString(R.string.turn_deveice_on)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
    }

    fun toDeviceConnect(view: View) {
        LogManager.getInstance().logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        startActivity(Intent(this, DeviceStatusActivity::class.java))
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
}
