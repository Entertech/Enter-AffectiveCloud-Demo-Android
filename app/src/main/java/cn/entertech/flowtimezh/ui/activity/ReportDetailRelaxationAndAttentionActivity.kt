package cn.entertech.flowtimezh.ui.activity

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.FirebaseRemoteConfigShare
import cn.entertech.flowtimezh.utils.ShotShareUtil
import cn.entertech.flowtimezh.utils.getExperienceStartTime
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_report_detail_relaxation_and_attention.*
import kotlinx.android.synthetic.main.layout_common_title.*

class ReportDetailRelaxationAndAttentionActivity : BaseActivity() {

    private var lastAttentionAverage: Double = 0.0
    private var lastRelaxationAverage: Double = 0.0
    private var lastAttentionValue: Int = 0
    private var lastRelaxationValue: Int = 0
    private var popupWindow: PopupWindow? = null
    private var userLessonRecordDao: UserLessonRecordDao? = null
    private var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
    private var fileName: String? = null
    private var mRecordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_relaxation_and_attention)
        initFullScreenDisplay()
        mRecordId = intent.getLongExtra(RECORD_ID, -1L)
        initTitle()
        initData()
        initLineChart()
        initLastAverageChart()
        initShareView()
    }

    fun initShareView() {
        if (isShareCondition()&&!SettingManager.getInstance().isReportShared("${mRecordId}_${Constant.REPORT_SHARE_PAGE_R_A}")) {
            showShareView()
        } else {
            hideShareView()
        }
    }

    fun showShareView() {
        SettingManager.getInstance().setIsReportShared("${mRecordId}_${Constant.REPORT_SHARE_PAGE_R_A}",true)
        iv_menu_icon.visibility = View.GONE
        lottie_view.visibility = View.VISIBLE
        lottie_view.setOnClickListener {
            shareReport()
            hideShareView()
        }
        var popView =
            LayoutInflater.from(this@ReportDetailRelaxationAndAttentionActivity)
                .inflate(R.layout.pop_share_tip, null)
        popupWindow = PopupWindow(
            popView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lottie_view.post {
            popupWindow?.showAsDropDown(lottie_view, 0, 0, Gravity.BOTTOM)
        }

        lottie_view.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                iv_menu_icon.visibility = View.VISIBLE
                lottie_view.visibility = View.GONE
            }

        })

        Handler().postDelayed(object:Runnable{
            override fun run() {
                if (popupWindow != null && popupWindow!!.isShowing){
                    popupWindow!!.dismiss()
                }
            }

        },5000)
    }

    fun hideShareView() {
        iv_menu_icon.visibility = View.VISIBLE
        lottie_view.visibility = View.GONE
        popupWindow?.dismiss()
    }

    fun initTitle() {
        window.statusBarColor = getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark)
        rl_title_bg.setBackgroundColor(getColorInDarkMode(R.color.common_bg_z1_color_light,R.color.common_bg_z1_color_dark))
        iv_back.setOnClickListener {
            finish()
        }

        iv_menu_icon.visibility = View.VISIBLE
        tv_title.text = getString(R.string.r_and_a)
        iv_menu_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.vector_drawable_share
            )
        )
        iv_menu_icon.setOnClickListener {
            shareReport()
        }
    }

    fun shareReport(){
        var shareHeadView =
            LayoutInflater.from(this).inflate(R.layout.layout_share_head_view, null)
        var tvTime = shareHeadView.findViewById<TextView>(R.id.tv_time)
        tvTime.text =
            getExperienceStartTime(this@ReportDetailRelaxationAndAttentionActivity, mRecordId)
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
    fun initData() {
        if (mRecordId == null || mRecordId == 0L || mRecordId == -1L) {
            return
        }
        userLessonRecordDao = UserLessonRecordDao(this)
        var userLessonRecord =
            userLessonRecordDao?.findRecordById(SettingManager.getInstance().userId, mRecordId!!)
        if (userLessonRecord != null) {
            meditationReportDataAnalyzed =
                userLessonRecordDao?.getReportDataFromFile(userLessonRecord)
        }
    }


    fun initLineChart() {
        var relaxationRec = meditationReportDataAnalyzed?.relaxationRec
        var attentionRec = meditationReportDataAnalyzed?.attentionRec
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.attentionAvg != null) {
            chart_relaxation_and_attention.setAttentionAverage(meditationReportDataAnalyzed!!.attentionAvg.toInt())
        }
        if (meditationReportDataAnalyzed != null && meditationReportDataAnalyzed!!.relaxationAvg != null) {
            chart_relaxation_and_attention.setRelaxationAverage(meditationReportDataAnalyzed!!.relaxationAvg.toInt())
        }
        chart_relaxation_and_attention.setAverageLineColor(getColorInDarkMode(R.color.common_line_hard_color_light,R.color.common_line_hard_color_dark))
        chart_relaxation_and_attention.setData(attentionRec,relaxationRec)
    }


    fun initLastAverageChart() {
        relaxation_average_chart.setTipTextColor(getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
        attention_average_chart.setTipTextColor(getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
        relaxation_average_chart.setTipBg(getColorInDarkMode(R.color.common_bg_z2_color_light,R.color.common_bg_z2_color_dark))
        attention_average_chart.setTipBg(getColorInDarkMode(R.color.common_bg_z2_color_light,R.color.common_bg_z2_color_dark))
        if (mRecordId == -2L && meditationReportDataAnalyzed != null) {
            relaxation_average_chart.setValues(listOf(meditationReportDataAnalyzed!!.relaxationAvg.toInt()))
            attention_average_chart.setValues(listOf(meditationReportDataAnalyzed!!.attentionAvg.toInt()))
        } else {
            if (meditationReportDataAnalyzed == null) {
                return
            }
            var lastTimesEffectiveRecord = userLessonRecordDao?.findLastEffectiveRecordById(
                SettingManager.getInstance().userId,
                mRecordId!!,
                7
            )
            var lastTimesEffectiveRelaxationAverage = lastTimesEffectiveRecord?.map {
                userLessonRecordDao?.getReportDataFromFile(it)?.relaxationAvg!!.toInt()
            }
            if (lastTimesEffectiveRelaxationAverage != null) {
                relaxation_average_chart.setValues(lastTimesEffectiveRelaxationAverage!!.asReversed())
                if (lastTimesEffectiveRelaxationAverage.size > 2) {
                    lastRelaxationAverage =
                        lastTimesEffectiveRelaxationAverage.subList(
                            1,
                            lastTimesEffectiveRelaxationAverage.size
                        )
                            .average()
                    lastRelaxationValue = lastTimesEffectiveRelaxationAverage[1]
                }
            }
            var lastTimesEffectiveAttentionAverage = lastTimesEffectiveRecord?.map {
                userLessonRecordDao?.getReportDataFromFile(it)!!.attentionAvg!!.toInt()
            }
            if (lastTimesEffectiveAttentionAverage != null) {
                attention_average_chart.setValues(lastTimesEffectiveAttentionAverage!!.asReversed())
                if (lastTimesEffectiveAttentionAverage.size > 2) {
                    lastAttentionAverage =
                        lastTimesEffectiveAttentionAverage.subList(
                            1,
                            lastTimesEffectiveAttentionAverage.size
                        )
                            .average()
                    lastAttentionValue = lastTimesEffectiveAttentionAverage[1]
                }
            }
        }
    }


    fun isShareCondition(): Boolean {
        if (meditationReportDataAnalyzed == null) {
            return false
        }
        var shareJson = SettingManager.getInstance().remoteConfigShareCondition
        var shareCondition = Gson().fromJson<FirebaseRemoteConfigShare>(
            shareJson,
            FirebaseRemoteConfigShare::class.java
        )
        var isAttentionAverageLarge = false
        var isRelaxationAverageLarge = false
        var isDataEffective = false
        var isAttentionValueLarge = false
        var isRelaxationValueLarge = false
        var isAttentionValueBig = false
        var isRelaxationValueBig = false
        var dataEffectiveCount = 0
        isAttentionAverageLarge = meditationReportDataAnalyzed!!.attentionAvg > lastAttentionAverage
        isRelaxationAverageLarge =
            meditationReportDataAnalyzed!!.relaxationAvg > lastRelaxationAverage
        isAttentionValueLarge = meditationReportDataAnalyzed!!.relaxationAvg > lastAttentionValue
        isRelaxationValueLarge = meditationReportDataAnalyzed!!.relaxationAvg > lastRelaxationValue
        for (data in meditationReportDataAnalyzed!!.attentionRec) {
            if (data > 0) {
                dataEffectiveCount++
            }
        }
        isAttentionValueBig =
            meditationReportDataAnalyzed!!.attentionAvg > shareCondition.r_a_page_share_conditions.min_r_a_value
        isRelaxationValueBig =
            meditationReportDataAnalyzed!!.relaxationAvg > shareCondition.r_a_page_share_conditions.min_r_a_value
        isDataEffective =
            dataEffectiveCount * 1.0 / meditationReportDataAnalyzed!!.attentionRec.size > shareCondition.r_a_page_share_conditions.min_data_valid_ratio
        return isDataEffective && isAttentionAverageLarge &&
                isRelaxationAverageLarge && isAttentionValueLarge &&
                isRelaxationValueLarge && isAttentionValueBig && isRelaxationValueBig
    }

    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }
}
