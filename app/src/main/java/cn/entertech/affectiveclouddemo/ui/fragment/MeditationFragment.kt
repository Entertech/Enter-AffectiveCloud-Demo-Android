package cn.entertech.affectiveclouddemo.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

import android.util.Log
import android.widget.ImageView
import cn.entertech.affectivecloudsdk.AffectiveSubscribeParams
import cn.entertech.affectivecloudsdk.BiodataSubscribeParams
import cn.entertech.affectivecloudsdk.EnterAffectiveCloudConfig
import cn.entertech.affectivecloudsdk.EnterAffectiveCloudManager
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.affectivecloudsdk.entity.Service
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.affectivecloudsdk.interfaces.Callback2
import cn.entertech.affectivecloudsdk.utils.ConvertUtil
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import cn.entertech.flowtime.mvp.model.meditation.*
import cn.entertech.flowtime.utils.reportfileutils.FragmentBuffer
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_DEVICE
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_NET
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.model.MeditationEntity
import cn.entertech.affectiveclouddemo.model.MessageEvent
import cn.entertech.affectiveclouddemo.model.UserLessonEntity
import cn.entertech.affectiveclouddemo.ui.activity.DataActivity
import cn.entertech.affectiveclouddemo.ui.view.MeditationBrainwaveView
import cn.entertech.affectiveclouddemo.ui.view.MeditationEmotionView
import cn.entertech.affectiveclouddemo.ui.view.MeditationHeartView
import cn.entertech.affectiveclouddemo.ui.view.MeditationInterruptView
import cn.entertech.affectiveclouddemo.utils.MeditationStatusPlayer
import cn.entertech.affectiveclouddemo.utils.getCurrentTimeFormat
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_data.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class MeditationFragment : androidx.fragment.app.Fragment() {
    private var bleManager: MultipleBiomoduleBleManager? = null
    var selfView: View? = null
    var llContainer: LinearLayout? = null
    var isHeartViewLoading = true
    var isHRVLoading = true
    var isBrainViewLoading = true
    var isAttentionLoading = true
    var isRelaxationLoading = true
    var isPressureLoading = true
    var handler: Handler = Handler()
    var meditationStartTime: Long? = null
    var websocketAddress = "wss://server-test.affectivecloud.cn/ws/algorithm/v1/"

    var meditationEndTime: String? = null
//    val APP_KEY: String = "93e3cf84-dea1-11e9-ae15-0242ac120002"
//    val APP_SECRET: String = "c28e78f98f154962c52fcd3444d8116f"

    val APP_KEY: String = "015b7118-b81e-11e9-9ea1-8c8590cb54f9"
    val APP_SECRET: String = "cd9c757ae9a7b7e1cff01ee1bb4d4f98"
    var fragmentBuffer = FragmentBuffer()
    var enterAffectiveCloudManager: EnterAffectiveCloudManager? = null
    var meditationStatusPlayer: MeditationStatusPlayer? = null

    lateinit var reportMeditationData: ReportMeditationDataEntity
    private var userLessonEntity: UserLessonEntity? = null
    private var meditaiton: MeditationEntity? = null

    var userLessonStartTime: String? = null
    private var meditationId: Long = -1
    private var mRecordId: Long = -1
    var isFirstReceiveData = true

    private lateinit var rawListener: (ByteArray) -> Unit
    private lateinit var heartRateListener: (Int) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selfView = inflater.inflate(R.layout.fragment_data, container, false)
        meditationStatusPlayer = MeditationStatusPlayer(activity!!)
        userLessonStartTime = getCurrentTimeFormat()
        initView()
        initFlowtimeManager()
        initAffectiveCloudManager()
        return selfView
    }


    fun initView() {
        selfView?.findViewById<ImageView>(R.id.iv_close)?.setOnClickListener {
            showDialog()
        }
        selfView?.findViewById<TextView>(R.id.tv_edit)?.setOnClickListener {
            var messageEvent = MessageEvent()
            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DATA_EDIT
            messageEvent.message = "edit"
            EventBus.getDefault().post(messageEvent)
        }

        refreshMeditationView()

        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVLoadingCover()
    }

    var onDeviceConnectListener = fun(str: String) {
        if (enterAffectiveCloudManager!!.isWebSocketOpen()) {
            meditationStatusPlayer?.playConnectAudio()
        }
        MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_DEVICE)
        activity?.runOnUiThread {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.GONE
            if (enterAffectiveCloudManager!!.isInited()) {
                enterAffectiveCloudManager!!.restore(object : Callback {
                    override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {

                    }

                    override fun onSuccess() {
                        bleManager?.startHeartAndBrainCollection()
                        bleManager?.startContact()
                    }
                })
            } else {
                enterAffectiveCloudManager!!.init(object :
                    Callback {
                    override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
                    }

                    override fun onSuccess() {
                        bleManager?.startHeartAndBrainCollection()
                        bleManager?.startContact()
                    }

                })
            }
//            rl_minibar_disconnect.visibility = View.GONE
            rl_minibar_connect.visibility = View.VISIBLE
            hideSampleData()
        }
    }

    var onDeviceDisconnectListener = fun(str: String) {
        if (enterAffectiveCloudManager!!.isWebSocketOpen()) {
            meditationStatusPlayer?.playDisconnectAudio()
        }
        MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_DEVICE)
        activity?.runOnUiThread {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceLinstener)
//            rl_minibar_disconnect.visibility = View.VISIBLE
            rl_minibar_connect.visibility = View.GONE
            showSampleData()
        }
//        Thread.sleep(1000)
//        activity?.runOnUiThread {
//            showSampleData()
//        }
    }

    fun initFlowtimeManager() {
        bleManager = DeviceUIConfig.getInstance(activity!!).managers[0]
        rawListener = fun(bytes: ByteArray) {
            enterAffectiveCloudManager?.appendEEGData(bytes)
        }
        heartRateListener = fun(heartRate: Int) {
            enterAffectiveCloudManager?.appendHeartRateData(heartRate)
        }
        bleManager?.addRawDataListener(rawListener)
        bleManager?.addHeartRateListener(heartRateListener)


        if (bleManager!!.isConnected()) {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.GONE
        } else {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceLinstener)
            showSampleData()
        }
        bleManager?.addConnectListener(onDeviceConnectListener)
        bleManager?.addDisConnectListener(onDeviceDisconnectListener)
    }

    var isFirstIn = true
    var websocketConnectListener = fun() {
        activity?.runOnUiThread {
            if (bleManager!!.isConnected() && !isFirstIn) {
                meditationStatusPlayer?.playConnectAudio()
            }
            isFirstIn = false
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_NET)
            showLoadingCover()
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
                View.GONE
        }
    }
    var websocketDisconnectListener = fun() {
        activity?.runOnUiThread {
            if (bleManager!!.isConnected()) {
                meditationStatusPlayer?.playDisconnectAudio()
            }
            resetLoading()
            dataReset()
            DeviceUIConfig.getInstance(activity!!).managers[0].stopHeartAndBrainCollection()
            MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_NET)
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)
                ?.toNetDisconnect(toNetRestoreLinstener)
        }
    }

    fun initAffectiveCloudManager() {

        var availableAffectiveServices =
            listOf(
                Service.ATTENTION,
                Service.PRESSURE,
                Service.RELAXATION,
                Service.AROUSAL,
                Service.PLEASURE
            )
        var availableBioServices = listOf(Service.EEG, Service.HR)
        var biodataSubscribeParams = BiodataSubscribeParams.Builder()
            .requestAllEEGData()
            .requestAllHrData()
            .build()

        var affectiveSubscribeParams = AffectiveSubscribeParams.Builder()
            .requestAllSleepData()
            .requestAttention()
            .requestRelaxation()
            .requestPressure()
            .requestArousal()
            .build()
        var enterAffectiveCloudConfig =
            EnterAffectiveCloudConfig.Builder(APP_KEY, APP_SECRET, "0")
                .url(websocketAddress)
                .timeout(10000)
                .availableBiodataServices(availableBioServices)
                .availableAffectiveServices(availableAffectiveServices)
                .biodataSubscribeParams(biodataSubscribeParams!!)
                .affectiveSubscribeParams(affectiveSubscribeParams!!)
                .build()
        enterAffectiveCloudManager = EnterAffectiveCloudManager(enterAffectiveCloudConfig)

        enterAffectiveCloudManager!!.addBiodataRealtimeListener {
            activity?.runOnUiThread {
                //            Logger.d("bio realtime data is " + it.toString())
                if (it != null && it!!.realtimeEEGData != null) {
                    if (isFirstReceiveData) {
                        meditationStartTime = System.currentTimeMillis()
                        fragmentBuffer.fileName = getCurrentTimeFormat(meditationStartTime!!)
                        isFirstReceiveData = false
                    }
                }
                showHeart(it?.realtimeHrData?.hr?.toInt(),it?.realtimeHrData?.hrv)
                showBrain(it?.realtimeEEGData)
            }
        }
        enterAffectiveCloudManager!!.addAffectiveDataRealtimeListener {
            //            Logger.d("affective realtime data is " + it.toString())
            activity?.runOnUiThread {
                showAttention(it?.realtimeAttentionData?.attention?.toFloat())
                showRelaxation(it?.realtimeRelaxationData?.relaxation?.toFloat())
                showPressure(it?.realtimePressureData?.pressure?.toFloat())
                showMood(it?.realtimePleasureData?.pressure?.toFloat())
            }
        }
        if (bleManager!!.isConnected()) {
            enterAffectiveCloudManager?.init(object : Callback {
                override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
//                    Logger.d("affectivecloudmanager init failed:" + error.toString())
                }

                override fun onSuccess() {
//                    Logger.d("affectivecloudmanager init success:")
                    bleManager?.startHeartAndBrainCollection()
                }
            })
        }


        enterAffectiveCloudManager?.addWebSocketConnectListener(
            websocketConnectListener
        )
        enterAffectiveCloudManager?.addWebSocketDisconnectListener(
            websocketDisconnectListener
        )
    }

    fun refreshMeditationView() {
        llContainer = selfView?.findViewById<LinearLayout>(R.id.ll_container)
        llContainer?.removeAllViews()
        var viewOrders = "Heart,Brainwave,Emotion".split(",")
        var lp = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lp.leftMargin = ScreenUtil.dip2px(activity!!, 16f)
        lp.rightMargin = ScreenUtil.dip2px(activity!!, 16f)
        lp.topMargin = ScreenUtil.dip2px(activity!!, 16f)
        for (i in 0 until viewOrders.size) {
            when (viewOrders[i]) {
                "Emotion" -> {
                    var meditationEmotionView = MeditationEmotionView(activity!!)
                    meditationEmotionView.tag = viewOrders[i]
                    meditationEmotionView.layoutParams = lp
                    llContainer?.addView(meditationEmotionView)
                }
                "Heart" -> {
                    var meditationHeartView = MeditationHeartView(activity!!)
                    meditationHeartView.tag = viewOrders[i]
                    meditationHeartView.layoutParams = lp
                    llContainer?.addView(meditationHeartView)
                }
                "Brainwave" -> {
                    var meditationBrainwaveView = MeditationBrainwaveView(activity!!)
                    meditationBrainwaveView.tag = viewOrders[i]
                    meditationBrainwaveView.layoutParams = lp
                    llContainer?.addView(meditationBrainwaveView)
                }
            }
        }
    }

    fun showBrain(realtimeEEGDataEntity: RealtimeEEGData?) {
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setLeftBrainwave(realtimeEEGDataEntity?.leftwave)
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setRightBrainwave(realtimeEEGDataEntity?.rightwave)
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setAlphaWavePercent(realtimeEEGDataEntity?.alphaPower?.toFloat())
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setBetaWavePercent(realtimeEEGDataEntity?.betaPower?.toFloat())
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setDeltaWavePercent(realtimeEEGDataEntity?.deltaPower?.toFloat())
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setGammaWavePercent(realtimeEEGDataEntity?.gammaPower?.toFloat())
            selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                ?.setThetaWavePercent(realtimeEEGDataEntity?.thetaPower?.toFloat())
            if (realtimeEEGDataEntity?.leftwave == null || realtimeEEGDataEntity?.leftwave!!.size == 0
                || realtimeEEGDataEntity?.rightwave == null || realtimeEEGDataEntity?.rightwave!!.size == 0
            ) {
                return@runOnUiThread
            }
            if (Collections.max(realtimeEEGDataEntity?.leftwave) != 0.0 || Collections.max(
                    realtimeEEGDataEntity?.rightwave
                ) != 0.0
            ) {
                isBrainViewLoading = false
            }
            if (isBrainViewLoading) {
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
            } else {
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hindLoadingCover()
            }
        }
    }

    fun showHeart(heartRate: Int?,hrv:Double?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHRV(hrv)
            isHeartViewLoading = heartRate == 0
            isHRVLoading = hrv == 0.0
            Log.d("###", "isHeartViewLoading:" + isHeartViewLoading + ":" + heartRate)
            if (isHeartViewLoading) {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
            } else {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindHRLoadingCover()
            }
            if (hrv != 0.0){
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindHRVLoadingCover()
            }
        }
    }

    fun showAttention(attention: Float?) {
        if (attention == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setAttention(attention)
            if (attention != 0f) {
                isAttentionLoading = false
            }
            if (isAttentionLoading) {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showAttentionLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideAttentionLoaidng()
            }
        }
    }

    fun showRelaxation(relaxation: Float?) {
        if (relaxation == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setRelaxation(relaxation)
            if (relaxation != 0f) {
                isRelaxationLoading = false
            }
            if (isRelaxationLoading) {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showRelaxationLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideRelaxationLoaidng()
            }
        }
    }

    fun showPressure(pressure: Float?) {
        if (pressure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setStress(pressure)
            if (pressure != 0f) {
                isPressureLoading = false
            }
            if (isPressureLoading) {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showPressureLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hidePressureLoaidng()
            }
        }
    }

    fun showMood(mood: Float?) {
        if (mood == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setArousal(mood)
            if (mood != 0f) {
                isPressureLoading = false
            }
            if (isPressureLoading) {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showArousalLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideArousalLoaidng()
            }
        }
    }

    fun dataReset() {
        showRelaxation(0f)
        showAttention(0f)
        showPressure(0f)
        showMood(0f)
        showHeart(0,0.0)
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    var toConnectDeviceLinstener = fun() {
        startActivity(Intent(activity, DeviceManagerActivity::class.java))
    }

    var toNetRestoreLinstener = fun() {
        if (enterAffectiveCloudManager!!.isInited()) {
            enterAffectiveCloudManager?.restore(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    bleManager?.startHeartAndBrainCollection()
                }

            })
        } else {
            enterAffectiveCloudManager?.init(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    bleManager?.startHeartAndBrainCollection()
                }

            })
        }
    }

    fun showSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showSampleData()
    }

    fun hideSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hideSampleData()
        showLoadingCover()
    }

    fun showLoadingCover() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showAttentionLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showRelaxationLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showPressureLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showArousalLoading()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    fun resetLoading() {
        isHeartViewLoading = true
        isBrainViewLoading = true
        isAttentionLoading = true
        isRelaxationLoading = true
        isPressureLoading = true
    }

    var finishRunnable = {
        exitWithoutMeditation()
    }

    fun exitWithMeditation(reportMeditationData: ReportMeditationDataEntity) {
        if (reportMeditationData.isDataSetCompletly()) {
            handler.removeCallbacks(finishRunnable)
            saveReportFile(reportMeditationData)
            saveMeditationInDB(reportMeditationData)
            saveUserLessonInDB()
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    toDataActivity()
                }

            })
        }
    }

    fun exitWithoutMeditation() {
//        saveUserLessonInDB()
//        Toast.makeText(activity!!,"体验时间过短！",Toast.LENGTH_SHORT).show()
        if (enterAffectiveCloudManager!!.isWebSocketOpen()) {
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    activity?.finish()
                }

            })
        } else {
            activity?.finish()
        }
    }


    fun saveReportFile(reportMeditationData: ReportMeditationDataEntity) {
        fragmentBuffer.appendMeditationReport(
            reportMeditationData,
            meditationStartTime!!,
            MeditaionInterruptManager.getInstance().interruptTimestampList
        )
    }

    fun saveMeditationInDB(report: ReportMeditationDataEntity) {
        var meditationDao = MeditationDao(activity)
        meditaiton = MeditationEntity()
        meditationId = -System.currentTimeMillis()
        meditaiton!!.id = meditationId
        meditaiton!!.startTime = fragmentBuffer.fileName
        meditaiton!!.finishTime = meditationEndTime
        meditaiton!!.attentionAvg = report.reportAttentionEnitty?.attentionAvg!!.toFloat()
        meditaiton!!.attentionMax =
            java.util.Collections.max(report.reportAttentionEnitty?.attentionRec).toFloat()
        meditaiton!!.attentionMin =
            java.util.Collections.min(report.reportAttentionEnitty?.attentionRec).toFloat()
        meditaiton!!.heartRateAvg = report.reportHRDataEntity?.hrAvg!!.toFloat()
        meditaiton!!.heartRateMax = report.reportHRDataEntity?.hrMax!!.toFloat()
        meditaiton!!.heartRateMin = report.reportHRDataEntity?.hrMin!!.toFloat()
        meditaiton!!.heartRateVariabilityAvg = report.reportHRDataEntity?.hrvAvg!!.toFloat()
        meditaiton!!.relaxationAvg = report.reportRelaxationEnitty?.relaxationAvg!!.toFloat()
        meditaiton!!.relaxationMax =
            java.util.Collections.max(report.reportRelaxationEnitty?.relaxationRec).toFloat()
        meditaiton!!.relaxationMin =
            java.util.Collections.min(report.reportRelaxationEnitty?.relaxationRec).toFloat()
        meditaiton!!.user = 0
//        var reportFileUri =
//            "${SettingManager.getInstance().userId}/${courseId}/${lessonId}/${fragmentBuffer.fileName}"
        meditaiton!!.meditationFile = fragmentBuffer.fileName
        meditationDao.create(meditaiton)
    }

    fun saveUserLessonInDB() {
        var userLessonRecordDao = UserLessonRecordDao(activity)
        userLessonEntity = UserLessonEntity()
        mRecordId = -System.currentTimeMillis()
        userLessonEntity!!.id = mRecordId
        userLessonEntity!!.lessonName = ""
        userLessonEntity!!.courseName = ""
        userLessonEntity!!.lessonId = 0
        userLessonEntity!!.courseId = 0
        userLessonEntity!!.startTime = userLessonStartTime
        userLessonEntity!!.finishTime = meditationEndTime
        userLessonEntity!!.user = 0
        userLessonEntity!!.meditation = meditationId
        userLessonRecordDao.create(userLessonEntity)
    }

    fun toDataActivity() {
        var intent = Intent(activity, DataActivity::class.java)
        intent.putExtra(Constant.RECORD_ID, mRecordId)
        startActivity(intent)
        activity?.finish()
    }

    fun getReportAndExit() {
        enterAffectiveCloudManager?.getBiodataReport(object : Callback2<HashMap<Any, Any?>> {
            override fun onError(error: Error?) {
            }

            override fun onSuccess(t: HashMap<Any, Any?>?) {
//                Logger.d("report bio is " + t.toString())
                if (t == null) {
                    return
                }
                var reportHRDataEntity = ReportHRDataEntity()
                var hrMap = t!!["hr"] as Map<Any, Any?>
                if (hrMap!!.containsKey("hr_avg")) {
                    reportHRDataEntity.hrAvg = hrMap["hr_avg"] as Double
                }
                if (hrMap!!.containsKey("hr_max")) {
                    reportHRDataEntity.hrMax = hrMap["hr_max"] as Double
                }
                if (hrMap!!.containsKey("hr_min")) {
                    reportHRDataEntity.hrMin = hrMap["hr_min"] as Double
                }
                if (hrMap!!.containsKey("hr_rec")) {
                    reportHRDataEntity.hrRec = hrMap["hr_rec"] as ArrayList<Double>
                }
                if (hrMap!!.containsKey("hrv_rec")) {
                    reportHRDataEntity.hrvRec = hrMap["hrv_rec"] as ArrayList<Double>
                }
                if (hrMap!!.containsKey("hrv_avg")) {
                    reportHRDataEntity.hrvAvg = hrMap["hrv_avg"] as Double
                }
                reportMeditationData.reportHRDataEntity = reportHRDataEntity
                var reportEEGDataEntity = ReportEEGDataEntity()
                var eegMap = t!!["eeg"] as Map<Any, Any?>
                if (eegMap!!.containsKey("eeg_alpha_curve")) {
                    reportEEGDataEntity.alphaCurve = eegMap["eeg_alpha_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_beta_curve")) {
                    reportEEGDataEntity.betaCurve = eegMap["eeg_beta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_theta_curve")) {
                    reportEEGDataEntity.thetaCurve = eegMap["eeg_theta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_delta_curve")) {
                    reportEEGDataEntity.deltaCurve = eegMap["eeg_delta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_gamma_curve")) {
                    reportEEGDataEntity.gammaCurve = eegMap["eeg_gamma_curve"] as ArrayList<Double>
                }
                reportMeditationData.reportEEGDataEntity = reportEEGDataEntity

                enterAffectiveCloudManager?.getAffectiveDataReport(object :
                    Callback2<HashMap<Any, Any?>> {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess(t: HashMap<Any, Any?>?) {
//                        Logger.d("report affectve is " + t.toString())
                        if (t == null) {
                            return
                        }
                        var reportAttentionEnitty = ReportAttentionEnitty()
                        var attentionMap = t["attention"] as Map<Any, Any?>
                        if (attentionMap!!.containsKey("attention_avg")) {
                            Logger.d("attention avg is " + attentionMap["attention_avg"] as Double)
                            reportAttentionEnitty.attentionAvg =
                                attentionMap["attention_avg"] as Double
                        }
                        if (attentionMap!!.containsKey("attention_rec")) {
                            reportAttentionEnitty.attentionRec =
                                attentionMap["attention_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportAttentionEnitty = reportAttentionEnitty
                        var reportRelxationEntity = ReportRelaxationEnitty()
                        var relaxationMap = t["relaxation"] as Map<Any, Any?>
                        if (relaxationMap!!.containsKey("relaxation_avg")) {
                            reportRelxationEntity.relaxationAvg =
                                relaxationMap["relaxation_avg"] as Double
                        }
                        if (relaxationMap!!.containsKey("relaxation_rec")) {
                            reportRelxationEntity.relaxationRec =
                                relaxationMap["relaxation_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportRelaxationEnitty = reportRelxationEntity

                        var reportPressureEnitty = ReportPressureEnitty()

                        var pressureMap = t["pressure"] as Map<Any, Any?>
                        if (pressureMap!!.containsKey("pressure_avg")) {
                            reportPressureEnitty.pressureAvg = pressureMap["pressure_avg"] as Double
                        }
                        if (pressureMap!!.containsKey("pressure_rec")) {
                            reportPressureEnitty.pressureRec =
                                pressureMap["pressure_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportPressureEnitty = reportPressureEnitty

                        var reportPleasureEnitty = ReportPleasureEnitty()
                        var pleasureMap = t["pleasure"] as Map<Any, Any?>
                        if (pleasureMap!!.containsKey("pleasure_avg")) {
                            reportPleasureEnitty.pleasureAvg = pleasureMap["pleasure_avg"] as Double
                        }
                        if (pleasureMap!!.containsKey("pleasure_rec")) {
                            reportPleasureEnitty.pleasureRec =
                                pleasureMap["pleasure_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportPleasureEnitty = reportPleasureEnitty
                        exitWithMeditation(reportMeditationData)
                    }

                })
            }

        })
    }

    fun finishMeditation() {
//        if (mediaPlayer == null || mediaPlayer!!.currentPosition * 1f / mediaPlayer!!.duration < 0.75) {
//            ToastUtil.toastShort(this, "Meditation didn't finish, no report generate.")
//            finish()
//        } else {
        startFinishTimer()
        reportMeditationData = ReportMeditationDataEntity()
        meditationEndTime = getCurrentTimeFormat()
        bleManager?.stopHeartAndBrainCollection()
        if (meditationTimeError() || !enterAffectiveCloudManager!!.isWebSocketOpen()) {
            exitWithoutMeditation()
        } else {
            getReportAndExit()
        }
    }

    fun meditationTimeError(): Boolean {
        if (meditationStartTime == null) {
            return true
        } else {
            var meditationEndTime = System.currentTimeMillis()
            var duration = (meditationEndTime - meditationStartTime!!) / 1000 / 60
            if (duration < 1) {
                return true
            }
        }
        return false
    }

    fun showDialog() {
        var dialog = AlertDialog.Builder(activity)
            .setTitle(Html.fromHtml("<font color='${R.color.colorDialogTitle}'>结束体验</font>"))
            .setMessage(Html.fromHtml("<font color='${R.color.colorDialogContent}'>结束体验并获取分析报告</font>"))
            .setPositiveButton(
                Html.fromHtml("<font color='${R.color.colorDialogExit}'>确定</font>")
            ) { dialog, which ->
                dialog.dismiss()
                finishMeditation()
            }
            .setNegativeButton(
                Html.fromHtml("<font color='${R.color.colorDialogCancel}'>取消</font>")
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    fun startFinishTimer() {
        handler.postDelayed({
            finishRunnable
        }, 10000)
    }


    override fun onDestroy() {
        bleManager?.removeConnectListener(onDeviceConnectListener)
        bleManager?.removeDisConnectListener(onDeviceDisconnectListener)
        enterAffectiveCloudManager?.removeWebSocketConnectListener(
            websocketConnectListener
        )
        enterAffectiveCloudManager?.removeWebSocketDisconnectListener(
            websocketDisconnectListener
        )
        meditationStatusPlayer?.release()
        super.onDestroy()
    }


}
