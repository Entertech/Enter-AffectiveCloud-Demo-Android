package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import cn.entertech.flowtimezh.ui.activity.MeditationActivity
import cn.entertech.flowtimezh.ui.activity.MeditationDimListActivity
import cn.entertech.flowtimezh.ui.activity.MeditationLabelsCommitActivity
import cn.entertech.flowtimezh.ui.activity.MeditationTimeRecordActivity
import cn.entertech.flowtimezh.ui.adapter.MeditationLabelsListAdapter
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.TimeUtils.*
import cn.entertech.flowtimezh.utils.reportfileutils.FileHelper
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed
import cn.entertech.uicomponentsdk.report.StackedAreaChart
import cn.entertech.uicomponentsdk.utils.removeZeroData
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_meditation_labels_commit.*

import kotlinx.android.synthetic.main.fragment_statistics_data.*
import kotlin.collections.ArrayList

class StatisticsDataFragment : androidx.fragment.app.Fragment() {
    private var meditaitonId: Long? = null
    private var startTime: String? = null
    private lateinit var fileName: String
    var self: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        self = inflater.inflate(R.layout.fragment_statistics_data, container, false)
//        EventBus.getDefault().register(this)
        return self
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    open fun getScrollView(): NestedScrollView {
        return scroll_view
    }

    open fun getLinearLayoutBg(): LinearLayout {
        return ll_bg
    }


    var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    fun initView() {
//        refreshStatisticsView()
        var recordId = arguments?.getLong(RECORD_ID)
//        var recordId = -1573788852936
        if (recordId == null || recordId == 0L || recordId == -1L) {
            return
        }
        var userLessonRecordDao = UserLessonRecordDao(activity)
        var userLessonRecord = userLessonRecordDao.findRecordById(0, recordId)
        var meditationDao = MeditationDao(activity)
        var lessonName = userLessonRecord.lessonName
        var courseName = userLessonRecord.courseName

        tv_lesson_name.text = lessonName
        tv_course_name.text = courseName
        var formatStartTIme = userLessonRecord.startTime.replace("T", " ").replace("Z", "")
        tv_start_time.text =
            getFormatTime(
                getStringToDate(formatStartTIme, "yyyy-MM-dd HH:mm:ss"),
                "hh:mmaa"
            ).toLowerCase()
        tv_duration.text =
            timeStampToMin(
                getStringToDate(
                    userLessonRecord.finishTime,
                    "yyyy-MM-dd HH:mm:ss"
                ) - getStringToDate(
                    userLessonRecord.startTime,
                    "yyyy-MM-dd HH:mm:ss"
                )
            )

        if (userLessonRecord.meditation == 0L) {
            return
        }
        var meditation = meditationDao.findMeditationById(userLessonRecord.meditation)
        if (meditation == null || meditation.meditationFile == null) {
            return
        }

        startTime = meditation.startTime
        fileName = meditation.meditationFile!!
//        Logger.d("file name is " + fileName)

        if (fileName == null) {
            return
        }
        var fileProtocol = FileHelper.getMeditationReport(activity!!, fileName)

//        Logger.d("fileProtocol size is " + fileProtocol.list.size)
        if (fileProtocol.list.size <= 0) {
            return
        }
        meditationReportDataAnalyzed = fileProtocol.list[0] as MeditationReportDataAnalyzed?
        if (meditationReportDataAnalyzed == null) {
            return
        }

        meditaitonId = userLessonRecord.meditation
//        report_hr_view.isDataNull(false)
//        report_hrv_view.isDataNull(false)
//        report_pressure_view.isDataNull(false)
//        Logger.d("user record is " + userLessonRecord.toString() + "meditation record is " + meditation.toString())
        sleep_chart.setSourceData(fileProtocol)
        setViewData()
        initLabelsView()
        hideChartView()
    }

    fun hideChartView(){
        chart_brainwave.visibility = View.GONE
        chart_hr.visibility = View.GONE
        chart_hrv.visibility = View.GONE
        chart_pressure.visibility = View.GONE
        chart_relaxation_and_attention.visibility = View.GONE
        sleep_chart.visibility = View.GONE
    }

    fun initLabelsView() {
        var meditationLabelsDao = MeditationLabelsDao(Application.getInstance())
        var meditationLabels = meditationLabelsDao.findByMeditationId(meditaitonId!!)
        if (meditationLabels == null || meditationLabels.isEmpty()) {
            ll_labels_container.visibility = View.GONE
            return
        } else {
            ll_labels_container.visibility = View.VISIBLE
        }
        var data: List<MeditationLabelsModel>
        if (meditationLabels.size <= 4) {
            data = meditationLabels
            tv_more_labels.visibility = View.GONE
        } else {
            data = meditationLabels.subList(0, 4)
            tv_more_labels.visibility = View.VISIBLE
            tv_more_labels.setOnClickListener {
                var intent = Intent(activity!!, MeditationLabelsCommitActivity::class.java)
                intent.putExtra(Constant.EXTRA_MEDITATION_ID, meditaitonId!!)
                intent.putExtra(Constant.EXTRA_IS_FROM_REPORT, true)
                startActivity(intent)
            }
        }
        var adapter = MeditationLabelsListAdapter(data)
        adapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                var duration =
                    if (meditationLabels[position].startTime > meditationLabels[position].meditationStartTime) {
                        "${TimeUtils.getFormatTime(
                            meditationLabels[position].startTime - meditationLabels[position].meditationStartTime,
                            "mm:ss"
                        )}" +
                                "-${TimeUtils.getFormatTime(
                                    meditationLabels[position].endTime - meditationLabels[position].meditationStartTime,
                                    "mm:ss"
                                )}"
                    } else {
                        "${TimeUtils.getFormatTime(
                            meditationLabels[position].startTime,
                            "mm:ss"
                        )}" +
                                "-${TimeUtils.getFormatTime(
                                    meditationLabels[position].endTime,
                                    "mm:ss"
                                )}"
                    }
                var intent = Intent(
                    activity!!,
                    MeditationDimListActivity::class.java
                )
                intent.putExtra("dimIds", meditationLabels[position].dimIds)
                intent.putExtra("duration", duration)
                intent.putExtra(Constant.EXTRA_IS_FROM_REPORT, true)
                startActivity(intent)
            }
        rv_meditation_labels.adapter = adapter
        rv_meditation_labels.layoutManager = LinearLayoutManager(activity!!)
    }

    fun setViewData() {
        Log.d("########","meditationReportDataAnalyzed is ${meditationReportDataAnalyzed.toString()}")
        var alphaAverage = meditationReportDataAnalyzed!!.alphaCurve
        var betaAverage = meditationReportDataAnalyzed!!.betaCurve
        var deltaAverage = meditationReportDataAnalyzed!!.deltaCurve
        var gammaAverage = meditationReportDataAnalyzed!!.gammaCurve
        var thetaAverage = meditationReportDataAnalyzed!!.thetaCurve
        if (alphaAverage.average() == 0.0 && betaAverage.average() == 0.0 && deltaAverage.average() == 0.0){
            return
        }
        var brainwaveList = ArrayList<ArrayList<Double>>()
        brainwaveList.add(gammaAverage as ArrayList<Double>)
        brainwaveList.add(betaAverage as ArrayList<Double>)
        brainwaveList.add(alphaAverage as ArrayList<Double>)
        brainwaveList.add(thetaAverage as ArrayList<Double>)
        brainwaveList.add(deltaAverage as ArrayList<Double>)
        chart_brainwave.setData(brainwaveList)

        var hrLine = meditationReportDataAnalyzed?.hrRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.hrAvg != null){
            chart_hr.setAverage("${meditationReportDataAnalyzed!!.hrAvg.toInt()}")
        }
        chart_hr.setAverageLineColor(R.color.common_line_hard_color_light)
        chart_hr.setData(hrLine)
        var hrvLine = meditationReportDataAnalyzed?.hrvRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.hrvAvg != null) {
            chart_hrv.setAverage("${meditationReportDataAnalyzed!!.hrvAvg.toInt()}")
        }
        chart_hrv.setAverageLineColor(R.color.common_line_hard_color_light)
        chart_hrv.setData(hrvLine)


        var relaxationRec = meditationReportDataAnalyzed?.relaxationRec
        var attentionRec = meditationReportDataAnalyzed?.attentionRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.attentionAvg != null) {
            chart_relaxation_and_attention.setAttentionAverage(meditationReportDataAnalyzed!!.attentionAvg.toInt())
        }
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.relaxationAvg != null) {
            chart_relaxation_and_attention.setRelaxationAverage(meditationReportDataAnalyzed!!.relaxationAvg.toInt())
        }
        chart_relaxation_and_attention.setAverageLineColor(R.color.common_line_hard_color_light)
        chart_relaxation_and_attention.setData(attentionRec,relaxationRec)

        var pressureLine = meditationReportDataAnalyzed?.pressureRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.pressureAvg != null){
            chart_pressure.setAverage("${meditationReportDataAnalyzed!!.pressureAvg.toInt()}")
        }
        chart_pressure.setAverageLineColor(R.color.common_line_hard_color_light)
        chart_pressure.setData(pressureLine)
    }

    private var llContainer: LinearLayout? = null


    override fun onDestroy() {
//        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}
