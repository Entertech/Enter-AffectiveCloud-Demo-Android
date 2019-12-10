package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.ui.activity.MeditationActivity
import cn.entertech.flowtimezh.ui.activity.PersonInfoActivity
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
            startActivity(Intent(activity, DeviceManagerActivity::class.java))
        }
        btn_start_meditation.setOnClickListener {
            startActivity(Intent(activity, PersonInfoActivity::class.java))
        }
    }
    var connectListener = fun(str: String) {
        activity?.runOnUiThread {
            iv_device.setImageResource(R.mipmap.ic_battery)
        }
    }
    var disconnectListener = fun(str: String) {
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
