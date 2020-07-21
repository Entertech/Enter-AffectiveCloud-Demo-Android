package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

import org.greenrobot.eventbus.EventBus
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.IS_SHOW_SKIP
import cn.entertech.flowtimezh.app.Constant.Companion.MEDITATION_TYPE
import cn.entertech.flowtimezh.app.Constant.Companion.MEDITATION_TYPE_UNGUIDE
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.ui.activity.MeditationBaseActivity
import cn.entertech.flowtimezh.ui.activity.SensorContactCheckActivity
import cn.entertech.flowtimezh.ui.view.*
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_INIT
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_INIT_COMPLETE
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_INIT_FAILED
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_RESTORE
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_COMPLETE
import cn.entertech.flowtimezh.utils.LogManager.Companion.LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_FAILED
import cn.entertech.flowtimezh.utils.MeditationStatusPlayer
import cn.entertech.flowtimezh.utils.ScreenUtil
import cn.entertech.flowtimezh.utils.formatNum
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_DEVICE
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_NET
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_data.*
import java.util.*


class MeditationFragment : Fragment() {
    private var isMeditationInterrupt: Boolean = false
    private var lastQuality: Double = 0.0
    private var biomoduleBleManager: BiomoduleBleManager? = null
    var selfView: View? = null
    var smartScrollView: SmartScrollView? = null
    var llContainer: LinearLayout? = null
    var isHeartViewLoading = true
    var isHRVViewLoading = true
    var isBrainViewLoading = true
    var isAttentionLoading = true
    var isRelaxationLoading = true
    var isPressureLoading = true

    var meditationStatusPlayer: MeditationStatusPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selfView = inflater.inflate(R.layout.fragment_data, container, false)

        meditationStatusPlayer = MeditationStatusPlayer(activity!!)
        initView()
        initNetListener()
        initDeviceConnectListener()
        return selfView
    }


    interface IScrollTopListener {
        fun isScrollTop(flag: Boolean)
    }

    var mIScrollTopListener: IScrollTopListener? = null

    fun setScrollTopListener(listener: IScrollTopListener) {
        mIScrollTopListener = listener
    }

    fun initView() {
        selfView?.findViewById<TextView>(R.id.tv_edit)?.setOnClickListener {
            var messageEvent = MessageEvent()
            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DATA_EDIT
            messageEvent.message = "edit"
            EventBus.getDefault().post(messageEvent)
        }
        smartScrollView = selfView?.findViewById<SmartScrollView>(R.id.ssv_scroll_view)
        smartScrollView?.setSmartScrollChangedListener(object :
            SmartScrollView.ISmartScrollChangedListener {
            override fun onScrolledNotTop() {
                mIScrollTopListener?.isScrollTop(false)
            }

            override fun onScrolledToBottom() {
            }

            override fun onScrolledToTop() {
                mIScrollTopListener?.isScrollTop(true)
            }
        })

        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
            ?.addErrorMessageListener {
                if (it != "Poor or No Signal") {
                    (activity as MeditationBaseActivity).pauseMeditation()
                }
                (activity as MeditationBaseActivity).scrollLayout.scrollToOpen()
                isMeditationInterrupt = true
                resetLoading()
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                    ?.showErrorMessage(it)
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showErrorMessage(it)
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showErrorMessage(it)
            }
        refreshMeditationView()
    }

    fun refreshMeditationView() {
        llContainer = selfView?.findViewById<LinearLayout>(R.id.ll_container)
        llContainer?.removeAllViews()
        var viewOrders = SettingManager.getInstance().meditationViewOrder.split(",")
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
            if (!isMeditationInterrupt) {
                if (isBrainViewLoading) {
                    selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                        ?.showLoadingCover()
                } else {
                    selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                        ?.hindLoadingCover()
                }
            }
        }
    }

    fun showHeart(heartRate: Int?, hrv: Double?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHrv(hrv)
            if (heartRate != 0) {
                isHeartViewLoading = false
            }
            if (hrv != 0.0) {
                isHRVViewLoading = false
            }
            if (!isMeditationInterrupt) {
                if (isHeartViewLoading) {
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showLoadingCover()
                } else {
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindLoadingCover()
                }
                if (isHRVViewLoading) {
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVLoadingCover()
                } else {
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRVLoadingCover()
                }

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
            if (!isMeditationInterrupt) {
                if (isAttentionLoading) {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.showAttentionLoading()
                } else {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hideAttentionLoaidng()
                }
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

            if (!isMeditationInterrupt) {
                if (isRelaxationLoading) {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.showRelaxationLoading()
                } else {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hideRelaxationLoaidng()
                }
            }

        }
    }

    fun showPressure(pressure: Float?) {
        if (pressure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setStress(formatNum(pressure / 20f))
            if (pressure != 0f) {
                isPressureLoading = false
            }
            if (!isMeditationInterrupt) {
                if (isPressureLoading) {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.showPressureLoading()
                } else {
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hidePressureLoaidng()
                }
            }
        }
    }

    fun showMood(mood: Float?) {
        if (mood == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setMood(formatNum(mood / 25f - 2))
        }
    }

    var runnable = Runnable {
        if (!isMeditationInterrupt) {
            handleInterruptTip()
        }
    }
    var isTimerScheduling = false
    var mMainHandler = Handler(Looper.getMainLooper())

    var goodContactCount = 0
    var isGoodContact = false
    private fun onBleContactListener(contact: Int) {
//        Log.d("#######", "contact is $contact")
        activity!!.runOnUiThread {
            if (contact != 0) {
                isGoodContact = false
                goodContactCount = 0
                isTimerScheduling = true
                mMainHandler.postDelayed(runnable, 1000)
            } else {
                isTimerScheduling = false
                mMainHandler.removeCallbacks(runnable)
                goodContactCount++
                if (goodContactCount == 5) {
                    if (isMeditationInterrupt) {
                        hideInterruptTip()
                        isGoodContact = true
                        goodContactCount = 0
                    }
                }
            }
        }
    }

    @Synchronized
    fun dealQuality(quality: Double?) {
//        Log.d("#######", "quality is $quality")
        if (quality == null) {
            return
        }
        if (quality >= 2.0) {
            isTimerScheduling = false
            mMainHandler.removeCallbacks(runnable)
//            if (lastQuality < 2 && isMeditationInterrupt) {
//                handleInterruptTip()
//            }
        } else {
            if (isGoodContact && !isTimerScheduling) {
                isTimerScheduling = true
                mMainHandler.postDelayed(runnable, 30000)
            }
        }
        lastQuality = quality
    }


//    fun hideBadSignalTip(){
//        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
//            View.GONE
//    }
//
//    fun showBadSignalTip() {
//        handleInterruptTip()
//    }


    var isFirstIn = true
    var isNeedWebSocketReconnect = true
    var websocketConnectListener = fun() {
        isNeedWebSocketReconnect = true
        restoreDelay = 0L
        LogManager.getInstance().logPost(LogManager.LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECT_COMPLETE)
        activity?.runOnUiThread {
            isFirstIn = false
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_NET)
//            showLoadingCover()
            handleInterruptTip()
        }
    }
    var reconnectRunnable = {
        restoreWebSocket()
    }

    var restoreDelay = 0L
    var websocketDisconnectListener = fun(error: String) {
        LogManager.getInstance()
            .logPost(LogManager.LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECT_DISCONNECT, error)
        activity?.runOnUiThread {
            isMeditationInterrupt = true
            showLoadingCover()
            if (!isNeedWebSocketReconnect) {
                handleInterruptTip()
            }
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_FINISH_EEG)
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_FINISH_HR)
            BiomoduleBleManager.getInstance(Application.getInstance()).stopHeartAndBrainCollection()
            MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_NET)
//            Log.d("####", "is auto restore:$isNeedWebSocketReconnect")
            when (restoreDelay) {
                0L -> {
                    restoreDelay = 5000L
                }
                5000L -> {
                    restoreDelay = 15000L
                }
                15000L -> {
                    restoreDelay = 30000L
                }
                30000L -> {
                    restoreDelay = 60000L
                }
                60000L -> {
                    mMainHandler.removeCallbacks(reconnectRunnable)
                    isNeedWebSocketReconnect = false
                    handleInterruptTip()
                }
            }
            if (isNeedWebSocketReconnect) {
                mMainHandler.postDelayed(reconnectRunnable, restoreDelay)
            }
        }
    }

    var isNeedDeviceReconnect = true
    var reconnectDeviceDelay = 0L
    var reconnectDeviceRunnable = object:Runnable{
        override fun run() {
            biomoduleBleManager?.scanNearDeviceAndConnect(fun() {
                activity?.runOnUiThread {
                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_SCAN_COMPLETE)
                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECTING)
                    Logger.d("scan succ")
                }
            }, fun(e: Exception) {
                activity?.runOnUiThread {
                    handleReconnectDevice()
                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_SCAN_FAILED)
                }
            }, fun(mac: String) {
                activity?.runOnUiThread {
                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_COMPLETE)
                    Logger.d("connect succ shake succ" + mac)
                    SettingManager.getInstance().bleMac = mac
                    SettingManager.getInstance().isConnectBefore = true
                    var messageEvent = MessageEvent()
                    messageEvent.message = "device connect"
                    messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DEVICE_CONNECT
                    EventBus.getDefault().post(messageEvent)
                }
            }, fun(error: String) {
                activity?.runOnUiThread {
//                    handleReconnectDevice()
                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_FAILED,error)
                    Logger.d("connect failure")
                }
            })
        }

    }
    fun handleReconnectDevice(){
        if (!isNeedDeviceReconnect) {
            isBleConnected = false
            handleInterruptTip()
        }
        when (reconnectDeviceDelay) {
            0L -> {
                reconnectDeviceDelay = 5000L
            }
            5000L -> {
                reconnectDeviceDelay = 15000L
            }
            15000L -> {
                reconnectDeviceDelay = 30000L
            }
            30000L -> {
                reconnectDeviceDelay = 60000L
            }
            60000L -> {
                mMainHandler.removeCallbacks(reconnectDeviceRunnable)
                isBleConnected = false
                isNeedDeviceReconnect = false
                handleInterruptTip()
            }
        }
        if (isNeedDeviceReconnect) {
            mMainHandler.postDelayed(reconnectDeviceRunnable, reconnectDeviceDelay)
        }
    }
    fun dataReset() {
        showRelaxation(0f)
        showAttention(0f)
        showPressure(0f)
        showMood(0f)
        showHeart(0, 0.0)
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    private fun initNetListener() {
        (activity as MeditationBaseActivity).enterAffectiveCloudManager?.addWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationBaseActivity).enterAffectiveCloudManager?.addWebSocketDisconnectListener(
            websocketDisconnectListener
        )
    }

    override fun onResume() {
//        if ((activity as MeditationBaseActivity).isToConnectDevice) {
//            mMainHandler.postDelayed(Runnable {
//                toSensorCheckActivity()
//                (activity as MeditationBaseActivity).isToConnectDevice = false
//            }, 1000)
//        }
        super.onResume()
    }

    var toConnectDeviceListener = fun() {
        var messageEvent = MessageEvent()
        messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_DEVICE_CONNECT
        messageEvent.message = "connectDevice"
        EventBus.getDefault().post(messageEvent)
    }

    var toNetRestoreLinstener = fun() {
        restoreWebSocket()
        isNeedWebSocketReconnect = true
    }

    fun restoreWebSocket() {
        var messageEvent = MessageEvent()
        messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_NET_RESTORE
        messageEvent.message = "netRestore"
        EventBus.getDefault().post(messageEvent)
    }

    var toSignalCheckListener = fun() {
        toSensorCheckActivity()
    }

    fun toSensorCheckActivity() {

        var intent = Intent(activity!!, SensorContactCheckActivity::class.java)
        intent.putExtra(
            MEDITATION_TYPE,
            MEDITATION_TYPE_UNGUIDE
        )
        intent.putExtra(IS_SHOW_SKIP, false)
        activity!!.startActivity(intent)

    }

    fun showMiniBar() {
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility =
            View.VISIBLE
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.GONE
        if (!BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text =
                "Connect the headhand to show data"
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationBaseActivity).scrollLayout.scrollToOpen()
            }
        } else {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text = "Tap to show biodata"
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationBaseActivity).scrollLayout.scrollToOpen()
            }
        }
    }

    fun hideMiniBar() {
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility = View.GONE
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.VISIBLE
    }

    fun playConnectAudio() {
        if (!isFirstIn && selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.visibility ==
            View.VISIBLE
        ) {
            meditationStatusPlayer?.playConnectAudio()
        }
    }

    fun playDisconnectAudio() {
        if (!isFirstIn && selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.visibility ==
            View.GONE
        ) {
            meditationStatusPlayer?.playDisconnectAudio()
        }
    }

    fun hideInterruptTip() {
        if (!(activity as MeditationBaseActivity).needToCheckSensor) {
            (activity as MeditationBaseActivity).resumeMeditation()
        }
        isTimerScheduling = false
        playConnectAudio()
        isMeditationInterrupt = false
        showLoadingCover()
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
            ?.visibility =
            View.GONE
    }

    fun handleInterruptTip() {
        if (activity == null) {
            return
        }
        if (isBleConnected) {
            if ((activity as MeditationBaseActivity).enterAffectiveCloudManager!!.isWebSocketOpen()) {
                if (isTimerScheduling) {
                    playDisconnectAudio()
                    selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                        ?.visibility =
                        View.VISIBLE
                    selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                        ?.toSignalBad(toSignalCheckListener)
                } else {
                    hideInterruptTip()
                }
            } else {
                playDisconnectAudio()
                selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                    ?.visibility =
                    View.VISIBLE
                selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                    ?.toNetDisconnect(toNetRestoreLinstener)
            }
        } else {
            playDisconnectAudio()
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceListener)
        }
    }

    var onDeviceConnectListener = fun(str: String) {
        reconnectDeviceDelay = 0L
        isNeedDeviceReconnect = true
        isBleConnected = true
        activity?.runOnUiThread {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_COMPLETE)
            handleInterruptTip()
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_DEVICE)
            if ((activity as MeditationBaseActivity).enterAffectiveCloudManager!!.isInited()) {
                LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_RESTORE)
                (activity as MeditationBaseActivity).enterAffectiveCloudManager!!.restore(object :
                    Callback {
                    override fun onError(error: Error?) {
                        LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_FAILED)
                    }

                    override fun onSuccess() {
                        LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_RESTORE_COMPLETE)
                        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_START_EEG)
                        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_START_HR)
                        biomoduleBleManager?.startHeartAndBrainCollection()
                        biomoduleBleManager?.startContact()
                    }

                })
            } else {
                LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_INIT)
                (activity as MeditationBaseActivity).enterAffectiveCloudManager!!.init(object :
                    Callback {
                    override fun onError(error: Error?) {
                        LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_INIT_FAILED)
                    }

                    override fun onSuccess() {
                        LogManager.getInstance().logPost(LOG_EVENT_AFFECTIVE_CLOUD_INIT_COMPLETE)
                        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_START_EEG)
                        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_START_HR)
                        biomoduleBleManager?.startHeartAndBrainCollection()
                        biomoduleBleManager?.startContact()
                    }

                })
            }
            rl_minibar_disconnect.visibility = View.GONE
            rl_minibar_connect.visibility = View.VISIBLE
            hideSampleData()
        }
    }
    var isBleConnected: Boolean = false
    var onDeviceDisconnectListener = fun(str: String) {
        isBleConnected = false
//        Log.d("####","is ble connected :${biomoduleBleManager!!.isConnected()}")
        LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_CONNECT_FAILED)
        MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_DEVICE)
        activity?.runOnUiThread {
            isMeditationInterrupt = true
            showLoadingCover()
            rl_minibar_disconnect.visibility = View.VISIBLE
            rl_minibar_connect.visibility = View.GONE
            handleReconnectDevice()
        }
//        Thread.sleep(1000)
//        activity?.runOnUiThread {
//            showSampleData()
//        }
    }

    private fun initDeviceConnectListener() {
        biomoduleBleManager = BiomoduleBleManager.getInstance(Application.getInstance())
        if (biomoduleBleManager!!.isConnected()) {
            isBleConnected = true
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility =
                View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility =
                View.VISIBLE
        } else {
            isBleConnected = false
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceListener)
            showSampleData()
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility =
                View.VISIBLE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.GONE
        }
        biomoduleBleManager?.addConnectListener(onDeviceConnectListener)
        biomoduleBleManager?.addDisConnectListener(onDeviceDisconnectListener)
        biomoduleBleManager?.addContactListener(::onBleContactListener)
    }

    fun showSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showSampleData()
    }

    fun hideSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRVSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hideSampleData()
        showLoadingCover()
    }

    fun showLoadingCover() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showAttentionLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showRelaxationLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showPressureLoading()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showLoadingCover()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVLoadingCover()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    fun resetLoading() {
        isHeartViewLoading = true
        isHRVViewLoading = true
        isBrainViewLoading = true
        isAttentionLoading = true
        isRelaxationLoading = true
        isPressureLoading = true
    }

    override fun onDestroy() {
        mMainHandler.removeCallbacks(runnable)
        mMainHandler.removeCallbacks(reconnectRunnable)
        biomoduleBleManager?.removeConnectListener(onDeviceConnectListener)
        biomoduleBleManager?.removeDisConnectListener(onDeviceDisconnectListener)
        biomoduleBleManager?.removeContactListener(::onBleContactListener)
        (activity as MeditationBaseActivity).enterAffectiveCloudManager?.removeWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationBaseActivity).enterAffectiveCloudManager?.removeWebSocketDisconnectListener(
            websocketDisconnectListener
        )
        meditationStatusPlayer?.release()
        super.onDestroy()
    }
}
