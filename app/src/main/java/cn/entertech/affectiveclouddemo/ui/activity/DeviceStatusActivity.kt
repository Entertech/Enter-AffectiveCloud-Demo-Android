package cn.entertech.affectiveclouddemo.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Application
import cn.entertech.affectiveclouddemo.app.Constant.Companion.IS_SHOW_SKIP
import cn.entertech.affectiveclouddemo.model.MessageEvent
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.ble.utils.NapBattery
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.bleuisdk.ui.activity.DeviceActivity
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import cn.entertech.bleuisdk.ui.widget.BatteryCircle
import cn.entertech.bleuisdk.utils.SettingManager
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_device_status.*
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus

class DeviceStatusActivity : BaseActivity() {
    lateinit var bleConnectedListener: (String) -> Unit
    lateinit var bleDisConnectedListener: (String) -> Unit
    private lateinit var bleDeviceManager: MultipleBiomoduleBleManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_status)
        initFullScreenDisplay()
        setStatusBarLight()
        bleDeviceManager =  DeviceUIConfig.getInstance(this).managers[0]
        initViews()
    }


    fun addConnectListener() {
        bleConnectedListener = fun(_: String) {
            Logger.d("device connect success")
            runOnUiThread {
                toConnect()
                updateDeviceInfo()
            }
        }
        bleDeviceManager.addConnectListener(bleConnectedListener)
    }

    fun removeConnectedListener() {
        bleDeviceManager.removeConnectListener(bleConnectedListener)
    }


    fun addDisConnectListener() {
        bleDisConnectedListener = fun(_: String) {
            Logger.d("connect failure")
            runOnUiThread {
                toDisConnect()
            }
        }
        bleDeviceManager.addDisConnectListener(bleDisConnectedListener)
    }

    fun removeDisConnectedListener() {
        bleDeviceManager.removeDisConnectListener(bleDisConnectedListener)
    }

    private fun initTitle() {
        rl_menu_ic.visibility = View.GONE
        findViewById<TextView>(R.id.tv_title).text ="Flowtime"
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
    }


    private fun initViews() {
        initDeviceContactView()
        initTitle()
        addDisConnectListener()
        addConnectListener()
        if (bleDeviceManager.isConnected()) {
            toConnect()
            updateDeviceInfo()
        } else if (bleDeviceManager.isConnecting()) {
            findViewById<View>(R.id.device_battery).visibility = View.GONE
            findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
            findViewById<View>(R.id.device_connecting_layout).visibility = View.VISIBLE
        } else {
            toConnecting()
        }

    }

//    fun initGuideTip() {
//        if (SettingManager.getInstance().isContactTip) {
//            guide_tip.visibility = View.VISIBLE
//        } else {
//            guide_tip.visibility = View.GONE
//        }
//    }

    fun initDeviceContactView() {
        iv_icon.visibility = View.VISIBLE
        iv_icon.setImageResource(R.drawable.vector_drawable_meditation_ble_contact_icon)

    }

    val onBatteryLevel = fun(battery: NapBattery) {
        runOnUiThread {
            val batteryView = findViewById<BatteryCircle>(R.id.device_battery)
            batteryView.setPercent(battery.percent)
            if (battery.hours == 0) {
                batteryView.setDescription(
                    String.format(
                        getString(R.string.device_min_left),
                        battery.minutes
                    )
                )
            } else {
                batteryView.setDescription(
                    String.format(
                        getString(R.string.device_time_left),
                        battery.hours
                    )
                )
            }
        }
    }

    fun updateDeviceInfo() {
        bleDeviceManager.startHeartAndBrainCollection()
        bleDeviceManager.readBattery(onBatteryLevel, null)
//        bleDeviceManager.addBatteryListener(onBatteryLevel)
    }

    private fun toDisConnect() {
        findViewById<View>(R.id.iv_arrow_white).visibility = View.GONE
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(Color.parseColor("#FB9C98"))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(Color.TRANSPARENT)
    }

    private fun toConnecting() {
        findViewById<View>(R.id.iv_arrow_white).visibility = View.GONE
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(resources.getColor(R.color.deviceConnecting,null))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT
        )
        bleDeviceManager.scanNearDeviceAndConnect(fun() {
            Logger.d("scan succ")
        }, fun(_: Exception) {
            runOnUiThread {
                toDisConnect()
            }
        }, fun(mac: String) {
            Logger.d("connect succ shake succ" + mac)
            runOnUiThread {
                SettingManager.getInstance(this@DeviceStatusActivity).bleMac = mac
                SettingManager.getInstance(this@DeviceStatusActivity).isConnectBefore = true
                toConnect()
                updateDeviceInfo()
                bleDeviceManager.readDeviceFirmware(fun(msg: String) {
                    Logger.d(msg)
                    SettingManager.getInstance(this@DeviceStatusActivity).bleFirmware = msg
//                    toDeviceUpdate(this@DeviceStatusActivity)
                }, null)
                var messageEvent = MessageEvent()
                messageEvent.message = "device connect"
                messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DEVICE_CONNECT
                EventBus.getDefault().post(messageEvent)
            }
        }, fun(_: String) {
            Logger.d("connect failure")
            runOnUiThread {
                toDisConnect()
            }
        })
    }

    private fun toConnect() {
        findViewById<View>(R.id.iv_arrow_white).visibility = View.VISIBLE
        findViewById<View>(R.id.device_battery).visibility = View.VISIBLE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(resources.getColor(R.color.deviceConnected,null))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT
        )

    }

    fun onReConnect(@Suppress("UNUSED_PARAMETER")view: View) {
        toConnecting()
    }

    fun toContactCheck(@Suppress("UNUSED_PARAMETER")view: View) {
        startActivity(Intent(this, SensorContactCheckActivity::class.java).putExtra(IS_SHOW_SKIP,false))
    }

    fun toDeviceDetail(@Suppress("UNUSED_PARAMETER")view: View) {
        if (bleDeviceManager.isConnected()) {
            startActivity(Intent(this, DeviceManagerActivity::class.java))
        }
    }

    override fun onDestroy() {
        removeDisConnectedListener()
        removeConnectedListener()
//        bleDeviceManager.stopHeartAndBrainCollection()
//        bleDeviceManager.removeBatteryListener(onBatteryLevel)
        super.onDestroy()
    }
}
