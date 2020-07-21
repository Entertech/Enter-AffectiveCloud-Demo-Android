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
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.utils.ShotShareUtil
import cn.entertech.flowtimezh.utils.getExperienceStartTime
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed
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
        mRecordId = intent.getLongExtra(RECORD_ID, -1L)
        initTitle()
        initData()
        initLineChart()
        initLastAverageChart()
    }


    fun initTitle() {
        window.statusBarColor = getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark)
        rl_title_bg.setBackgroundColor(getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark))
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = getString(R.string.heart_rate)
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
            tvTime.text =  getExperienceStartTime(this@ReportDetailHRActivity,mRecordId)
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
        userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao?.findRecordById(SettingManager.getInstance().userId, mRecordId!!)
        if (userLessonRecord != null) {
            meditationReportDataAnalyzed = userLessonRecordDao?.getReportDataFromFile(userLessonRecord)
        }
    }

    fun initLineChart() {
        var hrLine = meditationReportDataAnalyzed?.hrRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.hrAvg != null){
            chart_hr.setAverage(meditationReportDataAnalyzed!!.hrAvg.toInt())
        }
        chart_hr.setAverageLineColor(getColorInDarkMode(R.color.common_line_hard_color_light,R.color.common_line_hard_color_dark))
        chart_hr.setData(hrLine)
    }

    fun initLastAverageChart() {
        average_chart.setTipTextColor(getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
        average_chart.setTipBg(getColorInDarkMode(R.color.common_bg_z2_color_light,R.color.common_bg_z2_color_dark))
        if (mRecordId ==-2L && meditationReportDataAnalyzed != null){
            average_chart.setValues(listOf(meditationReportDataAnalyzed!!.hrAvg.toInt()))
        }else{
            if (meditationReportDataAnalyzed == null){
                return
            }
            var lastTimesEffectiveRecord = userLessonRecordDao?.findLastEffectiveRecordById(
                SettingManager.getInstance().userId,
                mRecordId!!,
                7
            )
            var lastTimesEffectiveHRAverage = lastTimesEffectiveRecord?.map {
                userLessonRecordDao?.getReportDataFromFile(it)!!.hrAvg!!.toInt()
            }
            if (lastTimesEffectiveHRAverage!= null){
                average_chart.setValues(lastTimesEffectiveHRAverage!!.asReversed())
            }
        }
    }
    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }
}
