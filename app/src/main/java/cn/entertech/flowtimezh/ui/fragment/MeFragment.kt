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
import cn.entertech.flowtimezh.ui.activity.ExperimentChooseActivity
import kotlinx.android.synthetic.main.fragment_me.*
import android.net.Uri
import android.os.Build
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

        tv_version.text = "${getAppVersionName(activity!!)}(${getAppVersionCode(activity!!)})"
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
    }

}
