package cn.entertech.flowtimezh.ui.fragment

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

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.ui.activity.MeditationActivity
import cn.entertech.flowtimezh.ui.activity.PersonInfoActivity
import cn.entertech.flowtimezh.utils.ConnectedDevice
import cn.entertech.flowtimezh.utils.ConnectedDeviceHelper
import cn.entertech.flowtimezh.utils.ToastUtil
import cn.entertech.flowtimezh.utils.isPermissionGranted
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
//        initDeviceIcon()
        iv_device.setOnClickListener {
            if (ConnectedDeviceHelper.currentConnectedDeviceType() != ConnectedDevice.NONE){
                ToastUtil.toastShort(requireActivity(),"设备已连接")
            }else{
                if (isPermissionGranted(requireActivity())){
                    connectDevice()
                }else{
                    connectDevice()
                }
            }
        }
        btn_start_meditation.setOnClickListener {
            var experimentDao = ExperimentDao(activity)
            var experiment = experimentDao.findExperimentBySelected()
            if (experiment == null){
                Toast.makeText(activity,"请先在设置中选择要进行的实验!",Toast.LENGTH_LONG).show()
            }else{
                startActivity(Intent(activity, PersonInfoActivity::class.java))
            }
        }
    }
    fun toDisconnected(error:String){
        requireActivity().runOnUiThread {
            (requireActivity() as BaseActivity).showTipError(error)
        }
    }

    fun toConnected(){
        requireActivity().runOnUiThread {
            (requireActivity() as BaseActivity).showTipSuccess("设备已连接")
        }
    }

    fun connectDevice(){
        (requireActivity() as BaseActivity).showLoading("Connecting")
        ConnectedDeviceHelper.scanNearDeviceAndConnect(
            SettingManager.getInstance().deviceType,
            fun(deviceType) {

            }, fun(e, deviceType) {
                toDisconnected(e.toString())
            }, fun(mac, deviceType) {
                toConnected()
            }, fun(error, deviceType) {
                toDisconnected(error)
            })
    }
//    var connectListener = fun(str: String) {
//        activity?.runOnUiThread {
//            iv_device.setImageResource(R.mipmap.ic_battery)
//        }
//    }
//    var disconnectListener = fun(str: String) {
//        activity?.runOnUiThread {
//            iv_device.setImageResource(R.mipmap.ic_device_disconnect_color)
//        }
//    }
//    private fun initDeviceIcon() {
//        bleManager = DeviceUIConfig.getInstance(activity!!).managers[0]
//
//        bleManager?.addConnectListener(connectListener)
//        bleManager?.addDisConnectListener(disconnectListener)
//        if (bleManager!!.isConnected()) {
//            iv_device.setImageResource(R.mipmap.ic_battery)
//        } else {
//            iv_device.setImageResource(R.mipmap.ic_device_disconnect_color)
//        }
//    }

    override fun onDestroy() {
//        bleManager?.removeConnectListener(connectListener)
//        bleManager?.removeDisConnectListener(disconnectListener)
        super.onDestroy()

    }
}
