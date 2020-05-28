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
        about_view.mLearnMoreUrl = SettingManager.getInstance().remoteConfigBrainReportInfo
    }


    fun initData() {
        if (mRecordId == null || mRecordId == 0L || mRecordId == -1L) {
            return
        }
        var userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao.findRecordById(0, mRecordId!!)
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
    }

    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }
}
