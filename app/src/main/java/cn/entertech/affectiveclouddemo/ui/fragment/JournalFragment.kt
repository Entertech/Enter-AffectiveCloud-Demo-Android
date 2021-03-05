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
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.model.UserLessonEntity
import cn.entertech.affectiveclouddemo.ui.activity.*
import cn.entertech.affectiveclouddemo.utils.LogManager
import cn.entertech.affectiveclouddemo.utils.TimeUtils
import cn.entertech.affectiveclouddemo.utils.reportfileutils.FileHelper
import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.fragment_journal.*
import java.text.DecimalFormat


class JournalFragment : Fragment() {
    private var brainCurves: java.util.ArrayList<java.util.ArrayList<Double>>? = null
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
        recordId = arguments?.getLong(Constant.RECORD_ID)
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
        card_brainwave_new.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailBrainwaveSpectrumActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
        }
        card_chart_hr.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailHRActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
        }
        card_hrv.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailHRVActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
        }
        card_relaxation.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailRelaxationAndAttentionActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
        }
        card_pressure.setOnClickListener {
            var intent = Intent(
                activity,
                ReportDetailPressureActivity::class.java
            )
            intent.putExtra(RECORD_ID, recordId)
            activity?.startActivity(intent)
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
        setHrChart()
        setBrainwave()
        setReportCardData()
    }

    private fun initSampleTipView() {
    }

    fun setBrainwave() {
        brainCurves = ArrayList<ArrayList<Double>>()
        brainCurves?.add(meditationReportDataAnalyzed!!.gammaCurve as ArrayList<Double>)
        brainCurves?.add(meditationReportDataAnalyzed!!.betaCurve as ArrayList<Double>)
        brainCurves?.add(meditationReportDataAnalyzed!!.alphaCurve as ArrayList<Double>)
        brainCurves?.add(meditationReportDataAnalyzed!!.thetaCurve as ArrayList<Double>)
        brainCurves?.add(meditationReportDataAnalyzed!!.deltaCurve as ArrayList<Double>)
        report_brainwave_new.isChartEnable(false)
        report_brainwave_new.setLegendShowList(getChartShowList())
        report_brainwave_new.setData(brainCurves)
    }

    fun saveChartShowList(){
        var lists = report_brainwave_new.legendIsCheckList.map { if (it){"1"}else{"0"} }
        var listString = ""
        for (i  in lists.indices){
            if (i == lists.size-1){
                listString += "${lists[i]}"
            }else{
                listString += "${lists[i]},"
            }
        }
        SettingManager.getInstance().brainChartLegendShowList = listString
    }

    override fun onPause() {
        saveChartShowList()
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        report_brainwave_new.setLegendShowList(getChartShowList())
        report_brainwave_new.setData(brainCurves)
    }
    fun getChartShowList():List<Boolean>{
        var lists = ArrayList<Boolean>()
        var listString = SettingManager.getInstance().brainChartLegendShowList.split(",")
        for (item in listString){
            if (item == "1"){
                lists.add(true)
            }else{
                lists.add(false)
            }
        }
        return lists
    }
    fun setReportCardData(){
        var hrAvg = meditationReportDataAnalyzed!!.hrAvg
        var hrvAvg = meditationReportDataAnalyzed!!.hrvAvg
        var pressureAvg = meditationReportDataAnalyzed!!.pressureAvg
        var relaxationAvg = meditationReportDataAnalyzed!!.relaxationAvg
        var attentionAvg = meditationReportDataAnalyzed!!.attentionAvg
        report_hr.setValue(hrAvg.toInt())
        var decimalFormat = DecimalFormat(".0")
        var average = decimalFormat.format(hrvAvg)
        report_hrv.setIsShortCard(true)
        report_hrv.setValue(java.lang.Float.parseFloat(average))
        report_pressure.setValue(pressureAvg.toInt())
        report_relaxation.setValue(relaxationAvg.toInt(), attentionAvg.toInt())
    }

    fun setHrChart() {
        chart_hr.isChartEnable(false)
        chart_hr.isShowLegend(false)
        chart_hr.isShowYAxisLabels(false)
        chart_hr.setCohTime(
            TimeUtils.second2FormatString(
                activity!!,
                meditationReportDataAnalyzed?.coherenceDuration?.toInt() ?: 0
            )
        )
        chart_hr.setData(
            meditationReportDataAnalyzed?.hrRec,
            meditationReportDataAnalyzed?.coherenceFlag,
            false
        )
    }
    fun getShareView(): View {
        return scroll_view
    }

    fun getShareViewBg(): View {
        return ll_bg
    }

}
