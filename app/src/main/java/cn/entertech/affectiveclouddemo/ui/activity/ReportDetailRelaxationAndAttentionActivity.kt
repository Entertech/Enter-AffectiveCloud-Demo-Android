package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.ui.activity.BaseActivity
import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.activity_report_detail_relaxation_and_attention.*
import kotlinx.android.synthetic.main.layout_common_title.*

class ReportDetailRelaxationAndAttentionActivity : BaseActivity() {

    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null
    private var mRecordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_relaxation_and_attention)
        initFullScreenDisplay()
        setStatusBarLight()
        mRecordId = intent.getLongExtra(RECORD_ID, -1L)
        initTitle()
        initData()
        initLineChart()
//        initLastAverageChart()
    }


    fun initTitle() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.realxation_and_attention)
        iv_menu_icon.visibility = View.GONE
        about_view.mLearnMoreUrl = SettingManager.getInstance().remoteConfigAttentionRelaxationReportInfo
    }

    fun initData() {
        if (mRecordId == 0L || mRecordId == -1L) {
            return
        }
        userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao?.findRecordById(0, mRecordId)
        if (userLessonRecord != null) {
            meditationReportDataAnalyzed =
                userLessonRecordDao?.getReportDataFromFile(userLessonRecord)
        }
    }


    fun initLineChart() {
        var relaxationRec = meditationReportDataAnalyzed?.relaxationRec
        var attentionRec = meditationReportDataAnalyzed?.attentionRec
        if (meditationReportDataAnalyzed != null){
            chart_relaxation_and_attention.setAttentionAverage(meditationReportDataAnalyzed!!.attentionAvg.toInt())
        }
        if (meditationReportDataAnalyzed != null){
            chart_relaxation_and_attention.setRelaxationAverage(meditationReportDataAnalyzed!!.relaxationAvg.toInt())
        }
        chart_relaxation_and_attention.setData(relaxationRec, attentionRec)
    }

}
