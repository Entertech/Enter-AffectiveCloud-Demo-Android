package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity

import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.MEDITATION_TYPE
import cn.entertech.affectiveclouddemo.ui.activity.DeviceStatusActivity
import cn.entertech.affectiveclouddemo.ui.activity.MeditationActivity
import cn.entertech.affectiveclouddemo.ui.activity.SensorContactCheckActivity
import cn.entertech.affectiveclouddemo.ui.activity.currentActivity
import cn.entertech.affectiveclouddemo.utils.LogManager
import kotlinx.android.synthetic.main.fragment_hone.*

class HomeFragment : Fragment() {

    private var bleManager: MultipleBiomoduleBleManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDeviceIcon()
        iv_device.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to device")
            startActivity(Intent(activity, DeviceManagerActivity::class.java))
        }
        btn_start_meditation.setOnClickListener {
            if (DeviceUIConfig.getInstance(activity!!).managers[0].isConnected()){
                LogManager.getInstance().logPost("Button $currentActivity to meditation")
                var intent = Intent(activity, SensorContactCheckActivity::class.java)
                intent.putExtra(MEDITATION_TYPE,"meditation")
                startActivity(intent)
            }else{
                Toast.makeText(activity!!,"请先连接设备！",Toast.LENGTH_SHORT).show()
            }
        }
    }
    var connectListener = fun(_: String) {
        activity?.runOnUiThread {
            iv_device.setImageResource(R.mipmap.ic_battery)
        }
    }
    var disconnectListener = fun(_: String) {
        activity?.runOnUiThread {
            iv_device.setImageResource(R.mipmap.ic_device_disconnect_color)
        }
    }
    private fun initDeviceIcon() {
        bleManager = DeviceUIConfig.getInstance(activity!!).managers[0]

        bleManager?.addConnectListener(connectListener)
        bleManager?.addDisConnectListener(disconnectListener)
        if (bleManager!!.isConnected()) {
            iv_device.setImageResource(R.mipmap.ic_battery)
        } else {
            iv_device.setImageResource(R.mipmap.ic_device_disconnect_color)
        }
    }

    override fun onDestroy() {
        bleManager?.removeConnectListener(connectListener)
        bleManager?.removeDisConnectListener(disconnectListener)
        super.onDestroy()

    }
}
