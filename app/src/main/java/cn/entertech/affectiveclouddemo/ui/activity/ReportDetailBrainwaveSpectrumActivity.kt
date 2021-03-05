package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import android.view.View
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.utils.reportfileutils.FileHelper
import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
import kotlinx.android.synthetic.main.activity_report_detail_brainwave_spectrrm.*
import kotlinx.android.synthetic.main.activity_report_detail_brainwave_spectrrm.ll_bg
import kotlinx.android.synthetic.main.activity_report_detail_brainwave_spectrrm.scroll_view
import kotlinx.android.synthetic.main.layout_common_title.*
import kotlin.math.pow

class ReportDetailBrainwaveSpectrumActivity : BaseActivity() {

    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null
    private var mRecordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_brainwave_spectrrm)
        initFullScreenDisplay()
        setStatusBarLight()
        mRecordId = intent.getLongExtra(RECORD_ID, -1L)
        initTitle()
        initData()
        initBrainwaveChart()
    }


    fun initTitle() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.brainwave_spectrum)
        iv_menu_icon.visibility = View.GONE
    }


    fun initData() {
        if (mRecordId == 0L || mRecordId == -1L) {
            return
        }
        var userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao.findRecordById(0, mRecordId)
        var meditationDao = MeditationDao(this)

        if (userLessonRecord.meditation == 0L) {
            return
        }
        var meditation = meditationDao.findMeditationById(userLessonRecord.meditation)
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
        var fileProtocol = FileHelper.getMeditationReport(this, fileName)

//        Logger.d("fileProtocol size is " + fileProtocol.list.size)
        if (fileProtocol.list.size <= 0) {
            return
        }
        meditationReportDataAnalyzed = fileProtocol.list[0] as MeditationReportDataAnalyzed?
        if (meditationReportDataAnalyzed == null) {
            return
        }
    }

    fun initBrainwaveChart() {
        if (meditationReportDataAnalyzed == null){
            return
        }
        var alphaCurve = meditationReportDataAnalyzed!!.alphaCurve
        var betaCurve = meditationReportDataAnalyzed!!.betaCurve
        var deltaCurve = meditationReportDataAnalyzed!!.deltaCurve
        var gammaCurve = meditationReportDataAnalyzed!!.gammaCurve
        var thetaCurve = meditationReportDataAnalyzed!!.thetaCurve
        if (alphaCurve.average() == 0.0 && betaCurve.average() == 0.0 && deltaCurve.average() == 0.0){
            return
        }
        var brainwaveList = ArrayList<ArrayList<Double>>()
        brainwaveList.add(gammaCurve as ArrayList<Double>)
        brainwaveList.add(betaCurve as ArrayList<Double>)
        brainwaveList.add(alphaCurve as ArrayList<Double>)
        brainwaveList.add(thetaCurve as ArrayList<Double>)
        brainwaveList.add(deltaCurve as ArrayList<Double>)
        chart_brainwave.setLegendShowList(getChartShowList())
        chart_brainwave.setData(brainwaveList)

        var alphaCurvePower = 10.0.pow(meditationReportDataAnalyzed!!.alphaCurve.average()/20)
        var betaCurvePower = 10.0.pow( meditationReportDataAnalyzed!!.betaCurve.average()/20)
        var deltaCurvePower =  10.0.pow(meditationReportDataAnalyzed!!.deltaCurve.average()/20)
        var gammaCurvePower =  10.0.pow(meditationReportDataAnalyzed!!.gammaCurve.average()/20)
        var thetaCurvePower =  10.0.pow(meditationReportDataAnalyzed!!.thetaCurve.average()/20)

        var totalPower = alphaCurvePower+betaCurvePower+deltaCurvePower+gammaCurvePower+thetaCurvePower

        var alphaRate = (alphaCurvePower/totalPower).toFloat()
        var betaRate = (betaCurvePower/totalPower).toFloat()
        var deltaRate =  (deltaCurvePower/totalPower).toFloat()
        var gammaRate =  (gammaCurvePower/totalPower).toFloat()
        var thetaRate =  (thetaCurvePower/totalPower).toFloat()
        report_brainwave.setData(listOf(gammaRate,betaRate,alphaRate,thetaRate,deltaRate))
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

    fun saveChartShowList(){
        var lists = chart_brainwave.legendIsCheckList.map { if (it){"1"}else{"0"} }
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
    fun getShareView(): View {
        return scroll_view
    }

    fun getShareViewBg(): View {
        return ll_bg
    }
}
