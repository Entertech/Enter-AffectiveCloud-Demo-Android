package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.ui.activity.*
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.reportfileutils.FileHelper
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.fragment_journal.*


class JournalFragment : Fragment() {
    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var recordId: Long? = -1L
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordId = arguments?.getLong(RECORD_ID)
        if (recordId == null || recordId == 0L || recordId == -1L) {
            return
        }
        initView()
        setData()
        initSampleTipView()
    }

    fun refresh(recordId: Long) {
        this.recordId = recordId
        initView()
        setData()
        initSampleTipView()
    }

    fun initView() {
        card_brainwave.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to brainwave report")
            activity?.startActivity(
                Intent(
                    activity,
                    ReportDetailBrainwaveSpectrumActivity::class.java
                ).putExtra(RECORD_ID, recordId)
            )
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
        card_coherence.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to coherence report")
            activity?.startActivity(
                Intent(
                    activity,
                    ReportDetailCoherenceActivity::class.java
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
        if (recordId == -2L) {
            card_sample_tip.visibility = View.VISIBLE
        } else {
            card_sample_tip.visibility = View.GONE
        }
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
        var coherenceAvg = meditationReportDataAnalyzed!!.coherenceAvg
        if (alphaAverage + betaAverage + deltaAverage + gammaAverage + thetaAverage != 0f) {
            report_brainwave.setData(
                listOf(
                    gammaAverage,
                    betaAverage,
                    alphaAverage,
                    thetaAverage,
                    deltaAverage
                )
            )
        }
        report_hr.setValue(hrAvg.toInt())
        report_coherence.setValue(coherenceAvg.toInt())
        report_hrv.setValue(hrvAvg.toInt())
        report_pressure.setValue(pressureAvg.toInt())
        report_relaxation.setValue(relaxationAvg.toInt(), attentionAvg.toInt())
    }

    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }

}
