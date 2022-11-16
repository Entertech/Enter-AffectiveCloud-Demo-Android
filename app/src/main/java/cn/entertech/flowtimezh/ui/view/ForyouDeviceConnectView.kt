package cn.entertech.flowtimezh.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.entertech.ble.cushion.CushionBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.ble.utils.NapBattery
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.utils.ConnectedDevice
import cn.entertech.flowtimezh.utils.ConnectedDeviceHelper
import cn.entertech.uicomponentsdk.utils.dp
import com.airbnb.lottie.LottieAnimationView

class ForyouDeviceConnectView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    RelativeLayout(context, attributeSet, def) {

    var scanTimeout = 10000L
    fun getBatteryRes(deviceType: String, level: Int): Int {
        if (deviceType == DEVICE_TYPE_HEADBAND || deviceType == DEVICE_TYPE_ENTERTECH_VR) {
            when (level) {
                in 0..9 -> return R.drawable.vector_drawable_ble_low_power_flowtime
                else -> return R.drawable.vector_drawable_ble_connect_flowtime
            }
        } else {
            when (level) {
                in 0..9 -> return R.drawable.vector_drawable_ble_low_power_cushion
                else -> return R.drawable.vector_drawable_ble_connect_cushion
            }
        }
    }

    var connectListener = fun(str: String) {
        readBattery(DEVICE_TYPE_HEADBAND)
    }

    var disconnectListener = fun(str: String) {
        toDisconnectView(DEVICE_TYPE_HEADBAND)
    }

    var cushionConnectListener = fun(str: String) {
        toConnectView(DEVICE_TYPE_CUSHION, NapBattery(0, 0, 10))
//        readBattery(DEVICE_TYPE_CUSHION)
    }

    var cushionDisconnectListener = fun(str: String) {
        toDisconnectView(DEVICE_TYPE_CUSHION)
    }


    var self = LayoutInflater.from(context).inflate(R.layout.view_foryou_ble_state, null)

    init {
        var layoutParams = LinearLayout.LayoutParams(
            84f.dp().toInt(),
            32f.dp().toInt()
        )
        self.layoutParams = layoutParams
        addView(self)
        initListener()
        init()
    }


    fun initListener() {
        BiomoduleBleManager.getInstance(Application.getInstance())
            .addConnectListener(connectListener)
        BiomoduleBleManager.getInstance(Application.getInstance())
            .addDisConnectListener(disconnectListener)
        CushionBleManager.getInstance(Application.getInstance())
            .addConnectListener(cushionConnectListener)
        CushionBleManager.getInstance(Application.getInstance())
            .addDisConnectListener(cushionDisconnectListener)
    }

    fun init() {
        findViewById<ImageView>(R.id.iv_state).setImageResource(R.drawable.vector_drawable_ble_disconnect_flowtime)
//        findViewById<RelativeLayout>(R.id.rl_bg).background =
//            ContextCompat.getDrawable(context, R.drawable.shape_device_view_bg_disconnect)
        verifyConnected()
    }

    private fun verifyConnected(): Boolean {
        if (ConnectedDeviceHelper.currentConnectedDeviceType() != ConnectedDevice.NONE) {
            readBattery(ConnectedDeviceHelper.currentConnectedDeviceType().type)
            return true
        }
        return false
    }

    fun readBattery(deviceType: String) {
        BiomoduleBleManager.getInstance(Application.getInstance())
            .readBattery(fun(napBattery: NapBattery) {
                toConnectView(deviceType, napBattery)
            }, fun(error: String) {
                toDisconnectView(deviceType)
            })
    }


    fun reconnect() {
        var isConnected = verifyConnected()
        if (!isConnected) {
            val deviceType = SettingManager.getInstance().deviceType
            if (deviceType != "") {
                toConnectingView()
                ConnectedDeviceHelper.scanNearDeviceAndConnect(deviceType, fun(deviceType) {
                }, fun(e, deviceType) {
                    toDisconnectView(deviceType)
                }, fun(mac, deviceType) {
                    readBattery(deviceType)
                }, fun(error, deviceType) {
                    toDisconnectView(deviceType)
                })
            } else {
                toDisconnectView(DEVICE_TYPE_HEADBAND)
            }
        }
    }

    fun toConnectingView() {
        (context as Activity).runOnUiThread {
            findViewById<LottieAnimationView>(R.id.loading_anim).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.iv_state).visibility = View.GONE
//            findViewById<RelativeLayout>(R.id.rl_bg).background =
//                ContextCompat.getDrawable(context, R.drawable.shape_device_view_bg_connecting)
        }
    }

    fun toDisconnectView(deviceType: String) {
        (context as Activity).runOnUiThread {
            findViewById<LottieAnimationView>(R.id.loading_anim).visibility = View.GONE
            findViewById<ImageView>(R.id.iv_state).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.iv_state).setImageResource(getDisconnectIcon(deviceType))
//            findViewById<RelativeLayout>(R.id.rl_bg).background =
//                ContextCompat.getDrawable(context, R.drawable.shape_device_view_bg_disconnect)
        }
    }

    fun toConnectView(deviceType: String, napBattery: NapBattery) {
        (context as Activity).runOnUiThread {
            findViewById<LottieAnimationView>(R.id.loading_anim).visibility = View.GONE
            findViewById<ImageView>(R.id.iv_state).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.iv_state).setImageResource(
                getBatteryRes(
                    deviceType,
                    napBattery.percent
                )
            )
//            findViewById<RelativeLayout>(R.id.rl_bg).background =
//                ContextCompat.getDrawable(context, getBatteryBackground(napBattery.percent))
        }
    }

    fun release() {
        BiomoduleBleManager.getInstance(Application.getInstance())
            .removeConnectListener(connectListener)
        BiomoduleBleManager.getInstance(Application.getInstance())
            .removeDisConnectListener(disconnectListener)
        CushionBleManager.getInstance(Application.getInstance())
            .removeConnectListener(cushionConnectListener)
        CushionBleManager.getInstance(Application.getInstance())
            .removeDisConnectListener(cushionDisconnectListener)
    }

    fun getDisconnectIcon(deviceType: String): Int {
        if (deviceType == DEVICE_TYPE_HEADBAND || deviceType == DEVICE_TYPE_ENTERTECH_VR) {
            return R.drawable.vector_drawable_ble_disconnect_flowtime
        } else {
            return R.drawable.vector_drawable_ble_disconnect_cushion
        }
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

}