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
import kotlinx.android.synthetic.main.activity_report_detail_hrv.*
import kotlinx.android.synthetic.main.activity_report_detail_hrv.ll_bg
import kotlinx.android.synthetic.main.activity_report_detail_hrv.scroll_view
import kotlinx.android.synthetic.main.layout_common_title.*

class ReportDetailHRVActivity : BaseActivity() {

    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null
    private var mRecordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_hrv)
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
        tv_title.text = getString(R.string.hrv)
        iv_menu_icon.visibility = View.GONE
        about_view.mLearnMoreUrl = SettingManager.getInstance().remoteConfigHRVReportInfo
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
        var hrvLine = meditationReportDataAnalyzed?.hrvRec
        if (meditationReportDataAnalyzed != null){
            chart_hrv.setAverage("${meditationReportDataAnalyzed!!.hrvAvg.toInt()}")
        }
        chart_hrv.setData(hrvLine)
    }


//    fun initLastAverageChart() {
//        if (mRecordId == -2L && meditationReportDataAnalyzed != null) {
//            hrv_average_chart.setValues(listOf(meditationReportDataAnalyzed!!.hrvAvg.toInt()))
//        } pic_arousal_pleasure_emotion_else {
//            if (meditationReportDataAnalyzed == null){
//                return
//            }
//            var lastTimesEffectiveRecord = userLessonRecordDao?.findLastEffectiveRecordById(
//                0,
//                mRecordId!!,
//                7
//            )
//            var lastTimesEffectiveHRAverage = lastTimesEffectiveRecord?.map {
//                userLessonRecordDao?.getReportDataFromFile(it)!!.hrvAvg!!.toInt()
//            }
//            if (lastTimesEffectiveHRAverage != null) {
//                hrv_average_chart.setValues(lastTimesEffectiveHRAverage!!.asReversed())
//            }
//        }
//
//    }

    fun getShareView(): View {
        return scroll_view
    }

    fun getShareViewBg(): View {
        return ll_bg
    }
}
