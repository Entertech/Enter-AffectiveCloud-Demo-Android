package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.database.ExperimentDao
import kotlinx.android.synthetic.main.fragment_me.*
import android.net.Uri
import android.os.Build
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.ui.activity.*
import cn.entertech.flowtimezh.utils.ConnectedDeviceHelper
import cn.entertech.flowtimezh.utils.ToastUtil
import cn.entertech.flowtimezh.utils.getAppVersionCode
import cn.entertech.flowtimezh.utils.getAppVersionName

class MeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        me_experiment_setting.setOnClickListener {
            activity!!.startActivity(Intent(activity!!, ExperimentChooseActivity::class.java))
        }

        me_help_center.setOnClickListener {
            val uri = Uri.parse("https://docs.myflowtime.cn/")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        btn_logout.setOnClickListener {
            SettingManager.getInstance().token = ""
            activity!!.startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
        }
        me_device_select.setOnClickListener {
            ConnectedDeviceHelper.disconnectAllDevice()
            val intent = Intent(activity, DeviceSelectActivity::class.java)
            intent.putExtra("fromMe", true)
            activity!!.startActivity(intent)
        }
        me_disconnect_ble.setOnClickListener {
            ConnectedDeviceHelper.disconnectAllDevice()
            ToastUtil.toastShort(requireActivity(),"Device Disconnected!")
        }
        setDeviceName()
        tv_version.text = "${getAppVersionName(activity!!)}(${getAppVersionCode(activity!!)})"
        switch_save_data.isChecked = SettingManager.getInstance().isSaveData
        switch_save_data.setOnCheckedChangeListener { buttonView, isChecked ->
            SettingManager.getInstance().isSaveData = isChecked
        }
    }

    fun setDeviceName() {
        when (SettingManager.getInstance().deviceType) {
            DEVICE_TYPE_CUSHION -> {
                me_device_select.mSubText = "Flowtime坐垫"
            }
            DEVICE_TYPE_HEADBAND -> {
                me_device_select.mSubText = "Flowtime头环"
            }
            DEVICE_TYPE_ENTERTECH_VR -> {
                me_device_select.mSubText = "FlowtimeVR"
            }

        }
    }

    fun updateSelectedExperiment() {
        var experimentDao = ExperimentDao(Application.getInstance())
        var selectedExperiment = experimentDao.findExperimentBySelected()
        if (selectedExperiment != null) {
            me_experiment_setting.mSubText = selectedExperiment.nameCn
        } else {
            var experiments = experimentDao.listAll()
            if (experiments != null && experiments.isNotEmpty()) {
                me_experiment_setting.mSubText = experiments[0].nameCn
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateSelectedExperiment()
        setDeviceName()
    }

}
