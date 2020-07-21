package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.utils.ShotShareUtil
import cn.entertech.flowtimezh.utils.getExperienceStartTime
import cn.entertech.flowtimezh.utils.reportfileutils.FileHelper
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed
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
        mRecordId = intent.getLongExtra(RECORD_ID, -1L)
        initTitle()
        initData()
        initBrainwaveChart()
    }


    override fun onResume() {
        super.onResume()
    }
    fun initTitle() {
        window.statusBarColor = getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark)
        rl_title_bg.setBackgroundColor(getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark))
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.brainwave_spectrum)
        iv_menu_icon.visibility = View.VISIBLE
        iv_menu_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.vector_drawable_share
            )
        )
        iv_menu_icon.setOnClickListener {
            var shareHeadView =
                LayoutInflater.from(this).inflate(R.layout.layout_share_head_view, null)
            var tvTime = shareHeadView.findViewById<TextView>(R.id.tv_time)
            tvTime.text =  getExperienceStartTime(this@ReportDetailBrainwaveSpectrumActivity,mRecordId)
            var tvUserName = shareHeadView.findViewById<TextView>(R.id.tv_user_name)
            tvUserName.text = "${SettingManager.getInstance().socialUserName}'s"
            var shareFootView =
                LayoutInflater.from(this).inflate(R.layout.layout_product_share_view, null)
            var scrollView = getShareView() as NestedScrollView
            var llBg = getShareViewBg() as LinearLayout
            llBg.setBackgroundColor(getColorInDarkMode(R.color.common_share_bg_color_light,R.color.common_share_bg_color_dark))
            ShotShareUtil.shotScrollView(this, scrollView, shareHeadView, shareFootView)
            llBg.setBackgroundColor(getColorInDarkMode(R.color.common_bg2_color_light,R.color.common_bg2_color_dark))

        }
    }


    fun initData() {
        if (mRecordId == null || mRecordId == 0L || mRecordId == -1L) {
            return
        }
        var userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao.findRecordById(SettingManager.getInstance().userId, mRecordId!!)
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
