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
            if (!isPermissionGranted()) {
                requestPermission()
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

    fun onPermissionNotGranted() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
    fun onPermissionGranted() {
        connectDevice()
    }

    fun requestPermission() {
        if (!isLocationEnable()) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RxPermissions(this).request(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ).subscribe { granted ->
                if (!granted) {
                    onPermissionNotGranted()
                }else{
                    onPermissionGranted()
                }
            }
        } else {
            RxPermissions(this).request(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
                .subscribe { granted ->
                    if (!granted) {
                        onPermissionNotGranted()
                    }else{
                        onPermissionGranted()
                    }
                }
        }
    }

    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RxPermissions(this).isGranted(Manifest.permission.BLUETOOTH_SCAN) && RxPermissions(
                this
            ).isGranted(Manifest.permission.BLUETOOTH_CONNECT) && RxPermissions(this).isGranted(
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun isLocationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }
}