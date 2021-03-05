package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.model.UserLessonEntity
import cn.entertech.affectiveclouddemo.ui.activity.*
import cn.entertech.affectiveclouddemo.utils.LogManager
import cn.entertech.affectiveclouddemo.utils.reportfileutils.FileHelper
import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.activity_report_detail_brainwave_spectrrm.*
import kotlinx.android.synthetic.main.activity_report_detail_hr.*
import kotlinx.android.synthetic.main.activity_report_detail_hrv.*
import kotlinx.android.synthetic.main.activity_report_detail_pressure.*
import kotlinx.android.synthetic.main.activity_report_detail_relaxation_and_attention.*
import kotlinx.android.synthetic.main.fragment_journal.*


class JournalLargeFragment : Fragment() {
    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var recordId: Long? = -1L
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_large, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordId = arguments?.getLong(Constant.RECORD_ID)
        if (recordId == null || recordId == 0L || recordId == -1L) {
            return
        }
//        initView()
        setData()
        initSampleTipView()
    }

    fun initView() {
        card_brainwave_new.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailBrainwaveSpectrumActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
        }
        card_hrv.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to hrv report")
            activity?.startActivity(
                Intent(activity, ReportDetailHRVActivity::class.java).putExtra(
                    RECORD_ID,
                    recordId
                )
            )
        }
        card_hr.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to hr report")
            activity?.startActivity(
                Intent(activity, ReportDetailHRActivity::class.java).putExtra(
                    RECORD_ID,
                    recordId
                )
            )
        }
        card_relaxation.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to relaxation report")
            activity?.startActivity(
                Intent(
                    activity,
                    ReportDetailRelaxationAndAttentionActivity::class.java
                ).putExtra(RECORD_ID, recordId)
            )
        }
        card_pressure.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to pressure report")
            activity?.startActivity(
                Intent(
                    activity,
                    ReportDetailPressureActivity::class.java
                ).putExtra(RECORD_ID, recordId)
            )
        }

    }


    fun setData() {
        userLessonRecordDao = UserLessonRecordDao(activity)
        var userLessonRecord: UserLessonEntity? =
            userLessonRecordDao?.findRecordById(0, recordId!!)
                ?: return
        var meditationDao = MeditationDao(activity)

        if (userLessonRecord?.meditation == 0L) {
            return
        }
        var meditation = meditationDao.findMeditationById(userLessonRecord!!.meditation)
        if (meditation == null || meditation.meditationFile == null) {
            return
        }
        var fileUri = meditation.meditationFile
        var uris = fileUri.split("/")
        fileName = uris[uris.size - 1]
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
        setBrainwave()

    }

    private fun initSampleTipView() {
//        if (recordId == -2L) {
//            card_sample_tip.visibility = View.VISIBLE
//        } else {
//            card_sample_tip.visibility = View.GONE
//        }
    }

    fun setBrainwave() {
        var alphaAverage = meditationReportDataAnalyzed!!.alphaCurve.average().toFloat()
        var betaAverage = meditationReportDataAnalyzed!!.betaCurve.average().toFloat()
        var deltaAverage = meditationReportDataAnalyzed!!.deltaCurve.average().toFloat()
        var gammaAverage = meditationReportDataAnalyzed!!.gammaCurve.average().toFloat()
        var thetaAverage = meditationReportDataAnalyzed!!.thetaCurve.average().toFloat()
        var hrAvg = meditationReportDataAnalyzed!!.hrAvg
        var hrvAvg = meditationReportDataAnalyzed!!.hrvAvg
        var pressureAvg = meditationReportDataAnalyzed!!.pressureAvg
        var relaxationAvg = meditationReportDataAnalyzed!!.relaxationAvg
        var attentionAvg = meditationReportDataAnalyzed!!.attentionAvg
        if (alphaAverage + betaAverage + deltaAverage + gammaAverage + thetaAverage != 0f) {
//            report_brainwave.setData(
//                listOf(
//                    gammaAverage,
//                    betaAverage,
//                    alphaAverage,
//                    thetaAverage,
//                    deltaAverage
//                )
//            )
        }
        report_hr.setValue(hrAvg.toInt())
        report_hrv.setValue(hrvAvg)
        report_pressure.setValue(pressureAvg.toInt())
        report_relaxation.setValue(relaxationAvg.toInt(), attentionAvg.toInt())
        initBrainwaveChart()
        initLineChart()
    }

    fun initBrainwaveChart() {
        if (meditationReportDataAnalyzed == null) {
            return
        }
        var alphaAverage = meditationReportDataAnalyzed!!.alphaCurve
        var betaAverage = meditationReportDataAnalyzed!!.betaCurve
        var deltaAverage = meditationReportDataAnalyzed!!.deltaCurve
        var gammaAverage = meditationReportDataAnalyzed!!.gammaCurve
        var thetaAverage = meditationReportDataAnalyzed!!.thetaCurve
        if (alphaAverage.average() == 0.0 && betaAverage.average() == 0.0 && deltaAverage.average() == 0.0) {
            return
        }
        var brainwaveList = ArrayList<ArrayList<Double>>()
        brainwaveList.add(gammaAverage as ArrayList<Double>)
        brainwaveList.add(betaAverage as ArrayList<Double>)
        brainwaveList.add(alphaAverage as ArrayList<Double>)
        brainwaveList.add(thetaAverage as ArrayList<Double>)
        brainwaveList.add(deltaAverage as ArrayList<Double>)
        chart_brainwave.setData(brainwaveList)
    }

    fun initLineChart() {
//        var hrLine = meditationReportDataAnalyzed?.hrRec
//        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.hrAvg != null) {
//            chart_hr.setAverage("${meditationReportDataAnalyzed!!.hrAvg.toInt()}")
//        }
//        chart_hr.setData(hrLine)

        var hrvLine = meditationReportDataAnalyzed?.hrvRec
        if (meditationReportDataAnalyzed != null) {
            chart_hrv.setAverage("${meditationReportDataAnalyzed!!.hrvAvg.toInt()}")
        }
        chart_hrv.setData(hrvLine)

        var pressureLine = meditationReportDataAnalyzed?.pressureRec
        if (meditationReportDataAnalyzed != null) {
            chart_pressure.setAverage("${meditationReportDataAnalyzed!!.pressureAvg.toInt()}")
        }
        chart_pressure.setData(pressureLine)

        var relaxationRec = meditationReportDataAnalyzed?.relaxationRec
        var attentionRec = meditationReportDataAnalyzed?.attentionRec
        if (meditationReportDataAnalyzed != null) {
            chart_relaxation_and_attention.setAttentionAverage(meditationReportDataAnalyzed!!.attentionAvg.toInt())
        }
        if (meditationReportDataAnalyzed != null) {
            chart_relaxation_and_attention.setRelaxationAverage(meditationReportDataAnalyzed!!.relaxationAvg.toInt())
        }
        chart_relaxation_and_attention.setData(relaxationRec, attentionRec)

    }

}
