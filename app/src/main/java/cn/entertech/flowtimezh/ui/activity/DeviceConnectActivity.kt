package cn.entertech.flowtimezh.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.utils.ConnectedDeviceHelper
import cn.entertech.flowtimezh.utils.isPermissionGranted
import cn.entertech.flowtimezh.utils.requestPermission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_device_connect.*

class DeviceConnectActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_connect)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    fun initView() {
        btn_connect.setOnClickListener {
            if (!isPermissionGranted(this)) {
                requestPermission(this){
                    connectDevice()
                }
            } else {
                connectDevice()
            }
        }
        initTip()
    }

    fun initTip(){
        val deviceType = SettingManager.getInstance().deviceType
        when(deviceType){
            DEVICE_TYPE_CUSHION->{
                tv_device_name.text = "Flowtime cushion"
                tv_device_tip.text = "The cushion wakes up when you sit on it. The indicator on the cushion will light up."
            }
            DEVICE_TYPE_HEADBAND->{
                tv_device_name.text = "Flowtime headband"
                tv_device_tip.text = "Press and hold the button until the indicator lights up."
            }
            DEVICE_TYPE_ENTERTECH_VR->{
                tv_device_name.text = "Entertech VR module"
                tv_device_tip.text = "Press and hold the button until the indicator lights up."
            }
        }
    }

    fun connectDevice(){
        showLoading("Connecting")
        ConnectedDeviceHelper.scanNearDeviceAndConnect(SettingManager.getInstance().deviceType,
            fun(deviceType) {

            }, fun(e, deviceType) {
                toDisconnected(e.toString())
            }, fun(mac, deviceType) {
                toConnected()
            }, fun(error, deviceType) {
                toDisconnected(error)
            })
    }

    fun toConnected() {
        runOnUiThread {
            dismissLoading()
            showTipSuccess(){
                val intent = Intent(this@DeviceConnectActivity,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun toDisconnected(error: String) {
        runOnUiThread {
            dismissLoading()
            showTipError(error)
        }
    }
}