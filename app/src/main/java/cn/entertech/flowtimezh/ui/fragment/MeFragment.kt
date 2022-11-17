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
        rl_experiment_setting.setOnClickListener {
            activity!!.startActivity(Intent(activity!!, ExperimentChooseActivity::class.java))
        }

        rl_help_center.setOnClickListener {
            val uri = Uri.parse("https://docs.myflowtime.cn/")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        rl_login_out.setOnClickListener {
            SettingManager.getInstance().token = ""
            activity!!.startActivity(Intent(activity,LoginActivity::class.java))
            activity!!.finish()
        }
        rl_time_count.setOnClickListener {
            activity!!.startActivity(Intent(activity,MeditationTimeCountSelectActivity::class.java))
        }

        rl_device_select.setOnClickListener {
            val intent = Intent(activity,DeviceSelectActivity::class.java)
            intent.putExtra("fromMe",true)
            activity!!.startActivity(intent)
        }
        setDeviceName()
        tv_version.text = "${getAppVersionName(activity!!)}(${getAppVersionCode(activity!!)})"

    }

    fun setDeviceName(){
        when(SettingManager.getInstance().deviceType){
            DEVICE_TYPE_CUSHION -> {
                tv_device_type.text = "Flowtime坐垫"
            }
            DEVICE_TYPE_HEADBAND -> {
                tv_device_type.text = "Flowtime头环"
            }
            DEVICE_TYPE_ENTERTECH_VR -> {
                tv_device_type.text = "FlowtimeVR"
            }

        }
    }

    fun updateSelectedExperiment() {
        var experimentDao = ExperimentDao(Application.getInstance())
        var selectedExperiment = experimentDao.findExperimentBySelected()
        if (selectedExperiment != null) {
            tv_experiment_name.text = selectedExperiment.nameCn
        } else {
            var experiments = experimentDao.listAll()
            if (experiments != null && experiments.isNotEmpty()) {
                tv_experiment_name.text = experiments[0].nameCn
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateSelectedExperiment()
        setDeviceName()
    }

}
