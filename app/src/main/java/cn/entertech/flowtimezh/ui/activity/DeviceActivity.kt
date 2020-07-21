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
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.toDeviceUpdate
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.ble.utils.NapBattery
import cn.entertech.testpull.BatteryCircle
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by EnterTech on 2017/11/21.
 */

class DeviceActivity : BaseActivity() {
    private lateinit var bleDeviceManager: BiomoduleBleManager
    lateinit var bleDisConnectedListener: (String) -> Unit
    lateinit var bleConnectedListener: (String) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        initFullScreenDisplay()
        bleDeviceManager = BiomoduleBleManager.getInstance(Application.getInstance())
        initViews()
    }


    override fun onResume() {
        super.onResume()
    }
    fun addConnectListener() {
        bleConnectedListener = fun(result: String) {
            Logger.d("device connect success")
            runOnUiThread {
                toConnect()
                updateDeviceInfo()
                initListview()
            }
        }
        bleDeviceManager.addConnectListener(bleConnectedListener)
    }

    fun removeConnectedListener() {
        bleDeviceManager.removeConnectListener(bleConnectedListener)
    }


    fun addDisConnectListener() {
        bleDisConnectedListener = fun(result: String) {
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
        findViewById<TextView>(R.id.tv_title).setTextColor(Color.WHITE)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
        findViewById<ImageView>(R.id.iv_back).setImageResource(R.mipmap.ic_back_white)
    }


    private fun initViews() {
        initTitle()
        addDisConnectListener()
        addConnectListener()
        if (bleDeviceManager.isConnected()) {
            toConnect()
            updateDeviceInfo()
            initListview()
        } else if (bleDeviceManager.isConnecting()) {
            findViewById<View>(R.id.device_battery).visibility = View.GONE
            findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
            findViewById<View>(R.id.device_connecting_layout).visibility = View.VISIBLE
        } else {
            toConnecting()
        }

    }
//
    val onBatteryLevel = fun(battery: NapBattery) {
        runOnUiThread {
            val batteryView = findViewById<BatteryCircle>(R.id.device_battery)
            batteryView.setPercent(battery.percent)
            if (battery.hours == 0){
                batteryView.setDescription(String.format(getString(R.string.device_min_left), battery.minutes))
            }else{
                batteryView.setDescription(String.format(getString(R.string.device_time_left), battery.hours))
            }
        }
    }

    fun updateDeviceInfo() {

        findViewById<TextView>(R.id.device_mac).text = SettingManager.getInstance().bleMac
        val failure = fun(error: String) {
            Logger.d(error)
        }

        Thread.sleep(100)
        bleDeviceManager.readDeviceHardware(fun(msg: String) {
            Logger.d(msg)
            SettingManager.getInstance().bleHardware = msg
            runOnUiThread {
                findViewById<TextView>(R.id.device_hardware).text = SettingManager.getInstance().bleHardware
            }
        }, failure)

        Thread.sleep(200)
        bleDeviceManager.readDeviceFirmware(fun(msg: String) {
            Logger.d(msg)
            SettingManager.getInstance().bleFirmware = msg
            toDeviceUpdate(this@DeviceActivity)
            runOnUiThread {
                findViewById<TextView>(R.id.device_firmware).text = SettingManager.getInstance().bleFirmware
            }
        }, failure)

        bleDeviceManager.readBattery(onBatteryLevel, null)

//        bleDeviceManager.addBatteryListener(onBatteryLevel)
    }

    private fun initListview() {
        val set = SettingManager.getInstance()
        val hardware = findViewById<TextView>(R.id.device_hardware)
        val firmware = findViewById<TextView>(R.id.device_firmware)
        val mac = findViewById<TextView>(R.id.device_mac)
        hardware.text = set.bleHardware
        firmware.text = set.bleFirmware
        mac.text = set.bleMac
    }

    private fun toDisConnect() {
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.device_guide_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.device_guide_layout_line).visibility = View.VISIBLE
        findViewById<View>(R.id.toolbar_layout).setBackgroundColor(getColorInDarkMode(R.color.common_red4_color_light,R.color.common_red4_color_dark))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(Color.TRANSPARENT)
        findViewById<View>(R.id.device_hardware_layout).alpha = 0.5f
        findViewById<View>(R.id.device_firmware_layout).alpha = 0.5f
        findViewById<View>(R.id.device_mac_layout).alpha = 0.5f
    }

    private fun toConnecting() {
        findViewById<View>(R.id.device_battery).visibility = View.GONE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.VISIBLE
        findViewById<View>(R.id.toolbar_layout).setBackgroundColor(resources.getColor(R.color.deviceConnecting))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT)
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
                initListview()
                var messageEvent = MessageEvent()
                messageEvent.message = "device connect"
                messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DEVICE_CONNECT
                EventBus.getDefault().post(messageEvent)
            }
        }, fun(error: String) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_FAILED,error)
            Logger.d("connect failure")
            runOnUiThread {
                toDisConnect()
            }
        })
    }

    private fun toConnect() {
        findViewById<View>(R.id.device_battery).visibility = View.VISIBLE
        findViewById<View>(R.id.device_disconnect_layout).visibility = View.GONE
        findViewById<View>(R.id.device_connecting_layout).visibility = View.GONE
        findViewById<View>(R.id.device_guide_layout).visibility = View.GONE
        findViewById<View>(R.id.device_guide_layout_line).visibility = View.GONE
        findViewById<View>(R.id.device_hardware_layout).alpha = 1f
        findViewById<View>(R.id.device_firmware_layout).alpha = 1f
        findViewById<View>(R.id.device_mac_layout).alpha = 1f
        findViewById<View>(R.id.toolbar_layout).setBackgroundColor(resources.getColor(R.color.deviceConnected))
        findViewById<View>(R.id.rl_title_bg).setBackgroundColor(
            android.graphics.Color.TRANSPARENT)

//        initListview()

//        val onBatteryLevel = fun(battery: NapBattery) {
//            runOnUiThread {
//                val batteryView = findViewById<BatteryCircle>(R.id.device_battery)
//                batteryView.setPercent(battery.percent)
//                batteryView.setDescription(String.format(getString(R.string.device_time_left), battery.hours))
//            }
//        }
//
//        rxBleManager.readBattery(onBatteryLevel, null)
//
//        rxBleManager.notifyBattery(onBatteryLevel,null)
    }

    fun onReConnect(view: View) {
        LogManager.getInstance().logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        toConnecting()
    }

    fun onHardware(view: View) {
//        startActivity(Intent(this,DeviceStatusActivity::class.java))
    }

    fun onFirmware(view: View) {
    }

    fun onMac(view: View) {
    }

    fun onNaptimeState(view: View) {
//        postButtonEvent(this@DeviceActivity!!,"1002","蓝牙连接 头环介绍")
        LogManager.getInstance().logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        var uri = Uri.parse(SettingManager.getInstance().remoteConfigFlowtimeHeadhandIntro)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun onConnectGuide(view: View) {
//        postButtonEvent(this@DeviceActivity!!,"1003","蓝牙连接 连接问题")
        LogManager.getInstance().logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        var uri = Uri.parse(SettingManager.getInstance().remoteConfigDeviceCanNotConnect)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun onUnbind(view: View) {
//        startActivity(Intent(this, DeviceDeleteActivity::class.java))
    }

//    override fun onBackPressed() {
//        if (bleDeviceManager.isConnecting()) {
//            Toast.makeText(this, getString(R.string.nap_conn_connecting), Toast.LENGTH_SHORT).show()
//        } else {
//            super.onBackPressed()
//            startActivity(Intent(this@DeviceActivity, HomeActivity::class.java).putExtra(ExtraKey.ME_SET, true))
//        }
//    }

    override fun onDestroy() {
        removeDisConnectedListener()
        removeConnectedListener()
//        bleDeviceManager.removeBatteryListener(onBatteryLevel)
        super.onDestroy()
    }
}