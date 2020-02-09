//package cn.entertech.affectiveclouddemo.ui.fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.core.widget.NestedScrollView
//import cn.entertech.affectiveclouddemo.R
//import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
//import cn.entertech.affectiveclouddemo.database.MeditationDao
//import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
//import cn.entertech.affectiveclouddemo.utils.TimeUtils.*
//import cn.entertech.affectiveclouddemo.utils.reportfileutils.FileHelper
//import cn.entertech.affectiveclouddemo.utils.reportfileutils.MeditationReportDataAnalyzed
//import cn.entertech.uicomponentsdk.report.StackedAreaChart
//import cn.entertech.uicomponentsdk.utils.removeZeroData
//
//import kotlinx.android.synthetic.main.fragment_statistics_data.*
//import kotlin.collections.ArrayList
//
//class StatisticsDataFragment : androidx.fragment.app.Fragment() {
//    private var startTime: String? = null
//    private lateinit var fileName: String
//    var self: View? = null
//    val TAG_OF_BRAIN_VIEW = "Brainwave Sepctrum"
//    val TAG_OF_HR_VIEW = "Heart Rate"
//    val TAG_OF_HRV_VIEW = "Heart Rate Variability"
//    val TAG_OF_ATTENTION_VIEW = "Attention"
//    val TAG_OF_RELAXATION_VIEW = "Relaxation"
//    val TAG_OF_PRESSURE_VIEW = "Pressure"
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        self = inflater.inflate(R.layout.fragment_statistics_data, container, false)
////        EventBus.getDefault().register(this)
//        return self
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//    }
//
//    open fun getScrollView(): NestedScrollView {
//        return scroll_view
//    }
//
//    open fun getLinearLayoutBg(): LinearLayout {
//        return ll_bg
//    }
//
//
//    var meditationReportDataAnalyzed: MeditationReportDataAnalyzed? = null
//    fun initView() {
////        refreshStatisticsView()
//        var recordId = arguments?.getLong(RECORD_ID)
////        var recordId = -1573788852936
//        if (recordId == null || recordId == 0L || recordId == -1L) {
//            return
//        }
//        var userLessonRecordDao = UserLessonRecordDao(activity)
//        var userLessonRecord = userLessonRecordDao.findRecordById(0, recordId)
//        var meditationDao = MeditationDao(activity)
//        var lessonName = userLessonRecord.lessonName
//        var courseName = userLessonRecord.courseName
//
//        tv_lesson_name.text = lessonName
//        tv_course_name.text = courseName
//        var formatStartTIme = userLessonRecord.startTime.replace("T", " ").replace("Z", "")
//        tv_start_time.text =
//            getFormatTime(
//                getStringToDate(formatStartTIme, "yyyy-MM-dd HH:mm:ss"),
//                "hh:mmaa"
//            ).toLowerCase()
//        tv_duration.text =
//            timeStampToMin(
//                getStringToDate(
//                    userLessonRecord.finishTime,
//                    "yyyy-MM-dd HH:mm:ss"
//                ) - getStringToDate(
//                    userLessonRecord.startTime,
//                    "yyyy-MM-dd HH:mm:ss"
//                )
//            )
//
//        report_brainwave_view.isDataNull(true)
//        report_attention_view.isDataNull(true)
//        report_relaxation_view.isDataNull(true)
//        report_hr_view.isDataNull(true)
//        report_hrv_view.isDataNull(true)
//        report_pressure_view.isDataNull(true)
//        if (userLessonRecord.meditation == 0L) {
//            return
//        }
//        var meditation = meditationDao.findMeditationById(userLessonRecord.meditation)
//        if (meditation == null || meditation.meditationFile == null) {
//            return
//        }
//
//        startTime = meditation.startTime
//        fileName = meditation.meditationFile!!
////        Logger.d("file name is " + fileName)
//
//        if (fileName == null) {
//            return
//        }
//        var fileProtocol = FileHelper.getMeditationReport(activity!!, fileName)
//
////        Logger.d("fileProtocol size is " + fileProtocol.list.size)
//        if (fileProtocol.list.size <= 0) {
//            return
//        }
//        meditationReportDataAnalyzed = fileProtocol.list[0] as MeditationReportDataAnalyzed?
//        if (meditationReportDataAnalyzed == null) {
//            return
//        }
//
//        report_brainwave_view.isDataNull(false)
//        report_attention_view.isDataNull(false)
//        report_relaxation_view.isDataNull(false)
//        report_hr_view.isDataNull(false)
//        report_hrv_view.isDataNull(false)
//        report_pressure_view.isDataNull(false)
////        Logger.d("user record is " + userLessonRecord.toString() + "meditation record is " + meditation.toString())
//        setViewData()
//    }
//
//    fun setViewData() {
//        var stackItems = ArrayList<StackedAreaChart.StackItem>()
//        var alphaItems = StackedAreaChart.StackItem()
//        var betaItems = StackedAreaChart.StackItem()
//        var thetaItems = StackedAreaChart.StackItem()
//        var deltaItems = StackedAreaChart.StackItem()
//        var gammaItems = StackedAreaChart.StackItem()
//        gammaItems.stackColor = ContextCompat.getColor(activity!!,R.color.colorStatisticsGammaWave)
//        deltaItems.stackColor = ContextCompat.getColor(activity!!,R.color.colorStatisticsDeltaWave)
//        thetaItems.stackColor = ContextCompat.getColor(activity!!,R.color.colorStatisticsThetaWave)
//        betaItems.stackColor = ContextCompat.getColor(activity!!,R.color.colorStatisticsBetaWave)
//        alphaItems.stackColor = ContextCompat.getColor(activity!!,R.color.colorStatisticsAlphaWave)
//        gammaItems.stackData = meditationReportDataAnalyzed!!.gammaCurve
//        deltaItems.stackData = meditationReportDataAnalyzed!!.deltaCurve
//        thetaItems.stackData = meditationReportDataAnalyzed!!.thetaCurve
//        betaItems.stackData = meditationReportDataAnalyzed!!.betaCurve
//        alphaItems.stackData = meditationReportDataAnalyzed!!.alphaCurve
//        stackItems.add(gammaItems)
//        stackItems.add(betaItems)
//        stackItems.add(alphaItems)
//        stackItems.add(thetaItems)
//        stackItems.add(deltaItems)
//
//        var meditationStart = getStringToDate(startTime!!.replace("T", " ").replace("Z", ""), "yyyy-MM-dd HH:mm:ss")
//        report_brainwave_view.setData(meditationStart, stackItems)
//        report_hr_view.setData(
//            meditationStart,
//            meditationReportDataAnalyzed!!.hrRec,
//            meditationReportDataAnalyzed!!.hrRec.max(),
//            meditationReportDataAnalyzed!!.hrRec.min(),
//            meditationReportDataAnalyzed!!.hrRec.average()
//        )
//        report_hrv_view.setData(
//            meditationStart,
//            meditationReportDataAnalyzed!!.hrvRec,
//            meditationReportDataAnalyzed!!.hrvRec.average()
//        )
//
//        report_pressure_view.setData(meditationStart, meditationReportDataAnalyzed!!.pressureRec)
//        var removeZeroAttentionRec = removeZeroData(meditationReportDataAnalyzed!!.attentionRec)
//        var removeZeroRelaxationRec = removeZeroData(meditationReportDataAnalyzed!!.relaxationRec)
//
//        if (!removeZeroAttentionRec.isNullOrEmpty()){
//            report_attention_view.setData(meditationStart, removeZeroAttentionRec)
//        }
//        if (!removeZeroRelaxationRec.isNullOrEmpty()){
//            report_relaxation_view.setData(meditationStart, removeZeroRelaxationRec)
//        }
//    }
//
//    private var llContainer: LinearLayout? = null
//
////    fun refreshStatisticsView() {
////        llContainer = self?.findViewById<LinearLayout>(R.id.ll_card_container)
////        llContainer?.removeAllViews()
////        var viewOrders = convertJsonString2List(SettingManager.getInstance().statisticsViewOrder)
////        var lp = LinearLayout.LayoutParams(
////            ViewGroup.LayoutParams.MATCH_PARENT,
////            ViewGroup.LayoutParams.WRAP_CONTENT
////        )
////        lp.leftMargin = ScreenUtil.dip2px(activity!!, 16f)
////        lp.rightMargin = ScreenUtil.dip2px(activity!!, 16f)
////        lp.topMargin = ScreenUtil.dip2px(activity!!, 16f)
////        lp.bottomMargin = 0
////        for (i in 0 until viewOrders.size) {
////            when (viewOrders[i].name) {
////                TAG_OF_BRAIN_VIEW -> {
////                    var statistiBrainwaveView = StatisticsBrainwaveView(activity!!)
////                    statistiBrainwaveView.cardElevation = 0f
////                    statistiBrainwaveView.radius = ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statistiBrainwaveView.tag = viewOrders[i].name
////                    statistiBrainwaveView.layoutParams = lp
////                    llContainer?.addView(statistiBrainwaveView)
////                    if (viewOrders[i].isShow) {
////                        statistiBrainwaveView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statistiBrainwaveView.visibility = View.GONE
////                    }
////                }
////                TAG_OF_HR_VIEW -> {
////                    var statisticsHeartRateView = StatisticsHeartRateView(activity!!)
////                    statisticsHeartRateView.cardElevation = 0f
////                    statisticsHeartRateView.radius = ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statisticsHeartRateView.tag = viewOrders[i].name
////                    statisticsHeartRateView.layoutParams = lp
////                    llContainer?.addView(statisticsHeartRateView)
////                    if (viewOrders[i].isShow) {
////                        statisticsHeartRateView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statisticsHeartRateView.visibility = View.GONE
////                    }
////                }
////                TAG_OF_HRV_VIEW -> {
////                    var statisticsHeartRateVariabilityView =
////                        StatisticsHeartRateVariabilityView(activity!!)
////                    statisticsHeartRateVariabilityView.cardElevation = 0f
////                    statisticsHeartRateVariabilityView.radius =
////                        ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statisticsHeartRateVariabilityView.tag = viewOrders[i].name
////                    statisticsHeartRateVariabilityView.layoutParams = lp
////                    llContainer?.addView(statisticsHeartRateVariabilityView)
////                    if (viewOrders[i].isShow) {
////                        statisticsHeartRateVariabilityView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statisticsHeartRateVariabilityView.visibility = View.GONE
////                    }
////                }
////                TAG_OF_ATTENTION_VIEW -> {
////                    var statisticsAttentionView = StatisticsAttentionView(activity!!)
////                    statisticsAttentionView.cardElevation = 0f
////                    statisticsAttentionView.radius = ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statisticsAttentionView.tag = viewOrders[i].name
////                    statisticsAttentionView.layoutParams = lp
////                    llContainer?.addView(statisticsAttentionView)
////                    if (viewOrders[i].isShow) {
////                        statisticsAttentionView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statisticsAttentionView.visibility = View.GONE
////                    }
////                }
////                TAG_OF_RELAXATION_VIEW -> {
////                    var statisticsRelaxationView = StatisticsRelaxationView(activity!!)
////                    statisticsRelaxationView.cardElevation = 0f
////                    statisticsRelaxationView.radius = ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statisticsRelaxationView.tag = viewOrders[i].name
////                    statisticsRelaxationView.layoutParams = lp
////                    llContainer?.addView(statisticsRelaxationView)
////                    if (viewOrders[i].isShow) {
////                        statisticsRelaxationView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statisticsRelaxationView.visibility = View.GONE
////                    }
////                }
////                TAG_OF_PRESSURE_VIEW -> {
////                    var statisticsPressureView = StatisticsPressureView(activity!!)
////                    statisticsPressureView.cardElevation = 0f
////                    statisticsPressureView.radius = ScreenUtil.dip2px(activity, 8f).toFloat()
////                    statisticsPressureView.tag = viewOrders[i].name
////                    statisticsPressureView.layoutParams = lp
////                    llContainer?.addView(statisticsPressureView)
////                    if (viewOrders[i].isShow) {
////                        statisticsPressureView.visibility = View.VISIBLE
////                    } pic_arousal_pleasure_emotion_else {
////                        statisticsPressureView.visibility = View.GONE
////                    }
////                }
////            }
////        }
////
////    }
//
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    fun onMessageEvent(event: MessageEvent) {
////        if (event.messageCode == MessageEvent.MESSAGE_CODE_STATISTICS_EDIT_DONE) {
//////            initView()
////            refreshStatisticsView()
////            if (fileName != null) {
////                self?.findViewWithTag<StatisticsBrainwaveView>(TAG_OF_BRAIN_VIEW)?.isDataNull(false)
////                self?.findViewWithTag<StatisticsRelaxationView>(TAG_OF_RELAXATION_VIEW)
////                    ?.isDataNull(false)
////                self?.findViewWithTag<StatisticsAttentionView>(TAG_OF_ATTENTION_VIEW)
////                    ?.isDataNull(false)
////                self?.findViewWithTag<StatisticsPressureView>(TAG_OF_PRESSURE_VIEW)
////                    ?.isDataNull(false)
////                self?.findViewWithTag<StatisticsHeartRateVariabilityView>(TAG_OF_HRV_VIEW)
////                    ?.isDataNull(false)
////                self?.findViewWithTag<StatisticsHeartRateView>(TAG_OF_HR_VIEW)?.isDataNull(false)
////            }
////            setViewData()
////        }
////    }
//
//    override fun onDestroy() {
////        EventBus.getDefault().unregister(this)
//        super.onDestroy()
//    }
//
//}
