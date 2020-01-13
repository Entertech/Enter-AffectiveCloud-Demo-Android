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
        tv_title.text = "Pressure"
        iv_menu_icon.visibility = View.GONE
    }

    fun initData() {
        if (mRecordId == null || mRecordId == 0L || mRecordId == -1L) {
            return
        }
        userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao?.findRecordById(0, mRecordId!!)
        if (userLessonRecord != null) {
            meditationReportDataAnalyzed =
                userLessonRecordDao?.getReportDataFromFile(userLessonRecord)
        }
    }


    fun initLineChart() {
        var relaxationRec = meditationReportDataAnalyzed?.relaxationRec
        var attentionRec = meditationReportDataAnalyzed?.attentionRec
        chart_relaxation_and_attention.setData(relaxationRec, attentionRec)
    }

//
//    fun initLastAverageChart() {
//        if (mRecordId == -2L && meditationReportDataAnalyzed != null) {
//            relaxation_average_chart.setValues(listOf(meditationReportDataAnalyzed!!.relaxationAvg.toInt()))
//            attention_average_chart.setValues(listOf(meditationReportDataAnalyzed!!.attentionAvg.toInt()))
//        }else{
//            if (meditationReportDataAnalyzed==null){
//                return
//            }
//            var lastTimesEffectiveRecord = userLessonRecordDao?.findLastEffectiveRecordById(
//                0,
//                mRecordId!!,
//                7
//            )
//            var lastTimesEffectiveRelaxationAverage = lastTimesEffectiveRecord?.map {
//                userLessonRecordDao?.getReportDataFromFile(it)?.relaxationAvg!!.toInt()
//            }
//            if (lastTimesEffectiveRelaxationAverage != null) {
//                relaxation_average_chart.setValues(lastTimesEffectiveRelaxationAverage!!.asReversed())
//            }
//            var lastTimesEffectiveAttentionAverage = lastTimesEffectiveRecord?.map {
//                userLessonRecordDao?.getReportDataFromFile(it)!!.attentionAvg!!.toInt()
//            }
//            if (lastTimesEffectiveAttentionAverage != null) {
//                attention_average_chart.setValues(lastTimesEffectiveAttentionAverage!!.asReversed())
//            }
//        }
//    }


    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }
}
