package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.IS_SHOW_SKIP
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.toDeviceUpdate
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.ble.utils.NapBattery
import cn.entertech.testpull.BatteryCircle
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_device_status.*
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus

class DeviceStatusActivity : BaseActivity() {
    lateinit var bleConnectedListener: (String) -> Unit
    lateinit var bleDisConnectedListener: (String) -> Unit
    private lateinit var bleDeviceManager: BiomoduleBleManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setContentView(R.layout.activity_device_status)
        bleDeviceManager = BiomoduleBleManager.getInstance(Application.getInstance())
        initViews()
    }


    fun addConnectListener() {
        bleConnectedListener = fun(result: String) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_COMPLETE)
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
        bleDisConnectedListener = fun(result: String) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_FAILED)
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
        findViewById<TextView>(R.id.tv_title).text = getString(R.string.flowtime)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
    }


    private fun initViews() {
        initDeviceContactView()
        initTitle()
        addDisConnectListener()
        addConnectListener()
        initGuideTip()
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

    fun initGuideTip() {
        if (SettingManager.getInstance().isContactTip) {
            guide_tip.visibility = View.VISIBLE
        } else {
            guide_tip.visibility = View.GONE
        }
    }

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
//        bleDeviceManager.startHeartAndBrainCollection()
        bleDeviceManager.readBattery(onBatteryLevel, null)
//        bleDeviceManager.addBatteryListener(onBatteryLevel)
    }

    fun toConnectGuide(view:View){
        var uri = Uri.parse(SettingManager.getInstance().remoteConfigDeviceCanNotConnect)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun toDisConnect() {
        card_disconnect.visibility = View.VISIBLE
        findViewById<View>(R.id.iv_arrow_white).visibility = View.GONE
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(
            getColorInDarkMode(
                R.color.common_red4_color_light,
                R.color.common_red4_color_dark
            )
        )
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(Color.TRANSPARENT)
        ll_wear_guide.visibility = View.GONE
    }

    private fun toConnecting() {
        card_disconnect.visibility = View.GONE
        findViewById<View>(R.id.iv_arrow_white).visibility = View.GONE
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(resources.getColor(R.color.deviceConnecting))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT
        )
        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_SCAN)
        bleDeviceManager.scanNearDeviceAndConnect(fun() {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_SCAN_COMPLETE)
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECTING)
            Logger.d("scan succ")
        }, fun(e: Exception) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_SCAN_FAILED)
            runOnUiThread {
                toDisConnect()
            }
        }, fun(mac: String) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_COMPLETE)
            Logger.d("connect succ shake succ" + mac)
            runOnUiThread {
                SettingManager.getInstance().bleMac = mac
                SettingManager.getInstance().isConnectBefore = true
                toConnect()
                updateDeviceInfo()
                bleDeviceManager.readDeviceFirmware(fun(msg: String) {
                    Logger.d(msg)
                    SettingManager.getInstance().bleFirmware = msg
                    toDeviceUpdate(this@DeviceStatusActivity)
                }, null)
                var messageEvent = MessageEvent()
                messageEvent.message = "device connect"
                messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DEVICE_CONNECT
                EventBus.getDefault().post(messageEvent)
            }
        }, fun(error: String) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_FAILED, error)
            Logger.d("connect failure")
            runOnUiThread {
                toDisConnect()
            }
        })
    }

    private fun toConnect() {
        card_disconnect.visibility = View.GONE
        findViewById<View>(R.id.iv_arrow_white).visibility = View.VISIBLE
        findViewById<View>(R.id.device_battery).visibility = View.VISIBLE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.rl_device_connect_bg).setBackgroundColor(resources.getColor(R.color.deviceConnected))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT
        )
        ll_wear_guide.visibility = View.VISIBLE
    }

    fun onReConnect(view: View) {
        LogManager.getInstance()
            .logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        toConnecting()
    }

    fun toContactCheck(view: View) {
        startActivity(Intent(this, SensorContactCheckActivity::class.java).putExtra(IS_SHOW_SKIP,false))
    }

    fun toDeviceDetail(view: View) {
        if (bleDeviceManager.isConnected()) {
            startActivity(Intent(this, DeviceActivity::class.java))
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
