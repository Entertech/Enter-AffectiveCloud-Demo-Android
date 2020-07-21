package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.MEDITATION_TYPE
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.ui.activity.*
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.ble.single.BiomoduleBleManager
import kotlinx.android.synthetic.main.fragment_hone.*

class HomeFragment : Fragment() {

    private var bleManager: BiomoduleBleManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    fun initView(){
        initDeviceIcon()
        iv_device.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to device")
            if (SettingManager.getInstance().isConnectBefore) {
                startActivity(Intent(activity, DeviceStatusActivity::class.java))
            } else {
                startActivity(Intent(activity, DevicePermissionActivity::class.java))
            }
        }
        btn_start_meditation.setOnClickListener {
            if (bleManager!!.isConnected()) {
                LogManager.getInstance().logPost("Button $currentActivity to meditation")
                var intent = Intent(activity, UnguidePreSettingsActivity::class.java)
                intent.putExtra(MEDITATION_TYPE, "meditation")
                startActivity(intent)
            } else {
                Toast.makeText(activity!!, "请先连接设备！", Toast.LENGTH_SHORT).show()
            }
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
        bleManager = BiomoduleBleManager.getInstance(Application.getInstance())
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
