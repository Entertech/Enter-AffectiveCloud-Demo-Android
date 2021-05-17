package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.view.View
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import kotlinx.android.synthetic.main.activity_meditation_time_count_select.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationTimeCountSelectActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setStatusBarLight()
        setContentView(R.layout.activity_meditation_time_count_select)
        initView()
    }

    fun initView() {
        initTitle()
        initTimeCountView()
    }

    fun initTimeCountView() {
        var isTimeCountEEG = SettingManager.getInstance().timeCountIsEEG()
        if (isTimeCountEEG) {
            eegSelect()
        } else {
            hrSelect()
        }

        rl_time_count_eeg.setOnClickListener {
            eegSelect()
            SettingManager.getInstance().setTimeCountIsEEG(true)
        }
        rl_time_count_hr.setOnClickListener {
            hrSelect()
            SettingManager.getInstance().setTimeCountIsEEG(false)
        }
    }

    fun eegSelect() {
        iv_icon_eeg.visibility = View.VISIBLE
        iv_icon_hr.visibility = View.INVISIBLE
    }

    fun hrSelect() {
        iv_icon_eeg.visibility = View.INVISIBLE
        iv_icon_hr.visibility = View.VISIBLE
    }

    fun initTitle() {
        iv_back.visibility = View.VISIBLE
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.visibility = View.VISIBLE
        tv_title.text = "计时方式"
    }
}