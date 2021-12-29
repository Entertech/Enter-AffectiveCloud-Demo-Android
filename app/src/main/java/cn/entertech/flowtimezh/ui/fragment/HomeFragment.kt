package cn.entertech.flowtimezh.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.entertech.ble.cushion.CushionBleManager
import cn.entertech.ble.single.BiomoduleBleManager

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.ui.activity.PersonInfoActivity
import kotlinx.android.synthetic.main.fragment_hone.*

class HomeFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null
    private var bleManager: CushionBleManager? = null
    private var flowtimeBle: BiomoduleBleManager? = null

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
        initProgressLoading()
        iv_device.setOnClickListener {
            if (bleManager?.isConnected() == true) {
                Toast.makeText(activity!!, "设备已连接", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progressDialog?.show()
//            bleManager?.scanNearDeviceAndConnect(fun() {
//            }, fun(e) {
//                activity!!.runOnUiThread {
//                    progressDialog?.dismiss()
//                    Toast.makeText(activity!!, "设备扫描失败：$e", Toast.LENGTH_SHORT).show()
//                }
//            }, fun(mac) {
//                activity!!.runOnUiThread {
//                    progressDialog?.dismiss()
//                    Toast.makeText(activity!!, "设备连接成功", Toast.LENGTH_SHORT).show()
//                }
//            }, fun(error) {
//                activity!!.runOnUiThread {
//                    progressDialog?.dismiss()
//                    Toast.makeText(activity!!, "设备连接失败：$error", Toast.LENGTH_SHORT).show()
//                }
//            })
            flowtimeBle?.scanNearDeviceAndConnect(fun() {
            }, fun(e) {
                activity!!.runOnUiThread {
                    progressDialog?.dismiss()
                    Toast.makeText(activity!!, "设备扫描失败：$e", Toast.LENGTH_SHORT).show()
                }
            }, fun(mac) {
                activity!!.runOnUiThread {
                    progressDialog?.dismiss()
                    Toast.makeText(activity!!, "设备连接成功", Toast.LENGTH_SHORT).show()
                }
            }, fun(error) {
                activity!!.runOnUiThread {
                    progressDialog?.dismiss()
                    Toast.makeText(activity!!, "设备连接失败：$error", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btn_start_meditation.setOnClickListener {
            if (flowtimeBle?.isConnected() == true) {
                var experimentDao = ExperimentDao(activity)
                var experiment = experimentDao.findExperimentBySelected()
                if (experiment == null) {
                    Toast.makeText(activity, "请先在设置中选择要进行的实验!", Toast.LENGTH_LONG).show()
                } else {
                    startActivity(Intent(activity, PersonInfoActivity::class.java))
                }
            } else {
                Toast.makeText(activity!!, "请先连接设备！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initProgressLoading() {
        progressDialog = ProgressDialog(activity)
        progressDialog?.setMessage("正在连接设备...")
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
        bleManager = CushionBleManager.getInstance(activity!!)

        bleManager?.addConnectListener(connectListener)
        bleManager?.addDisConnectListener(disconnectListener)
        if (bleManager!!.isConnected()) {
            iv_device.setImageResource(R.mipmap.ic_battery)
        } else {
            iv_device.setImageResource(R.mipmap.ic_device_disconnect_color)
        }

        flowtimeBle = BiomoduleBleManager.getInstance(activity!!)
    }

    override fun onDestroy() {
        bleManager?.removeConnectListener(connectListener)
        bleManager?.removeDisConnectListener(disconnectListener)
        super.onDestroy()

    }
}
