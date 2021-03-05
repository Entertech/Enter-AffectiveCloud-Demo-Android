package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import android.view.View
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.ui.activity.BaseActivity
import cn.entertech.affectiveclouddemo.utils.TimeUtils
import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.activity_report_detail_hr.*
import kotlinx.android.synthetic.main.activity_report_detail_hr.ll_bg
import kotlinx.android.synthetic.main.activity_report_detail_hr.scroll_view
import kotlinx.android.synthetic.main.layout_common_title.*

class ReportDetailHRActivity : BaseActivity() {

    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null
    private var mRecordId: Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_hr)
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
        tv_title.text = getString(R.string.heart_rate)
        iv_menu_icon.visibility = View.GONE
        about_view.mLearnMoreUrl = SettingManager.getInstance().remoteConfigHRReportInfo
    }

    fun initData() {
        if (mRecordId == 0L || mRecordId == -1L) {
            return
        }
        userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao?.findRecordById(0, mRecordId)
        if (userLessonRecord != null) {
            meditationReportDataAnalyzed = userLessonRecordDao?.getReportDataFromFile(userLessonRecord)
        }
    }

    fun initLineChart() {
        var hrLine = meditationReportDataAnalyzed?.hrRec
        if (meditationReportDataAnalyzed != null) {
            chart_hr.setAverage("${meditationReportDataAnalyzed?.hrAvg!!.toInt()}")
        }
        chart_hr.isShowLegend(true)
        chart_hr.isShowDetail = true
        chart_hr.setCohTime(
            TimeUtils.second2FormatString(
                this,
                meditationReportDataAnalyzed?.coherenceDuration?.toInt() ?: 0
            )
        )
        chart_hr.setData(hrLine, meditationReportDataAnalyzed?.coherenceFlag, true)
    }

//    fun initLastAverageChart() {
//        if (mRecordId ==-2L && meditationReportDataAnalyzed != null){
//            average_chart.setValues(listOf(meditationReportDataAnalyzed!!.hrAvg.toInt()))
//        }pic_arousal_pleasure_emotion_else{
//            if (meditationReportDataAnalyzed == null){
//                return
//            }
//            var lastTimesEffectiveRecord = userLessonRecordDao?.findLastEffectiveRecordById(
//               0,
//                mRecordId!!,
//                7
//            )
//            var lastTimesEffectiveHRAverage = lastTimesEffectiveRecord?.map {
//                userLessonRecordDao?.getReportDataFromFile(it)!!.hrAvg!!.toInt()
//            }
//            if (lastTimesEffectiveHRAverage!= null){
//                average_chart.setValues(lastTimesEffectiveHRAverage!!.asReversed())
//            }
//        }
//    }
    fun getShareView(): View {
        return scroll_view
    }

    fun getShareViewBg(): View {
        return ll_bg
    }
}
