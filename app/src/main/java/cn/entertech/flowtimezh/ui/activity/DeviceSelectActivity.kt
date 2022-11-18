package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.app.SettingManager
import kotlinx.android.synthetic.main.activity_device_select.*

class DeviceSelectActivity : BaseActivity() {
    private var isFromMe: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_select)
        initFullScreenDisplay()
        setStatusBarLight()
        isFromMe = intent.getBooleanExtra("fromMe", false)
        initView()
    }
    fun initTitle(){
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun initView() {
        initTitle()
        device_headband.setOnClickListener {
            btn_next.btnEnable = true
            clearSelect()
            device_headband.isSelect = true
            SettingManager.getInstance().deviceType = DEVICE_TYPE_HEADBAND
        }
        device_cushion.setOnClickListener {
            btn_next.btnEnable = true
            SettingManager.getInstance().deviceType = DEVICE_TYPE_CUSHION
            clearSelect()
            device_cushion.isSelect = true
        }
        device_vr.setOnClickListener {
            btn_next.btnEnable = true
            SettingManager.getInstance().deviceType = DEVICE_TYPE_ENTERTECH_VR
            clearSelect()
            device_vr.isSelect = true
        }
        btn_next.setOnClickListener {
            if (isFromMe) {
                finish()
            } else {
                val intent = Intent(this@DeviceSelectActivity, LabSelectActivity::class.java)
                startActivity(intent)
            }
        }
        if (isFromMe) {
            btn_next.btnText = getString(R.string.confirm)
        }
    }

    fun clearSelect() {
        device_headband.isSelect = false
        device_cushion.isSelect = false
        device_vr.isSelect = false
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_bottom_silent, R.anim.anim_bottom_out)
    }

}