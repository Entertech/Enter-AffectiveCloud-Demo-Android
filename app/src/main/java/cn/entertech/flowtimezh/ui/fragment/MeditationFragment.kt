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
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.flowtime.utils.*
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_DEVICE
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_NET
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.entity.MessageEvent
import cn.entertech.flowtimezh.ui.activity.MeditationActivity
import cn.entertech.flowtimezh.ui.activity.SensorContactCheckActivity
import cn.entertech.flowtimezh.ui.view.*
import cn.entertech.flowtimezh.utils.MeditationStatusPlayer
import cn.entertech.flowtimezh.utils.ScreenUtil
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.formatNum
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_data.*
import java.util.*


class MeditationFragment : androidx.fragment.app.Fragment() {
    private var isMeditationInterrupt: Boolean = false
    private var lastQuality: Double = 0.0
    private var biomoduleBleManager: MultipleBiomoduleBleManager? = null
    var selfView: View? = null
    var smartScrollView: SmartScrollView? = null
    var llContainer: LinearLayout? = null
    var isHeartViewLoading = true
    var isHRVViewLoading = true
    var isBrainViewLoading = true
    var isAttentionLoading = true
    var isRelaxationLoading = true
    var isPressureLoading = true
    var isArousalLoading = true
    var isCoherenceLoading = true
    var isPleasureLoading = true
    var isSleepLoading = true

    companion object {
        val SHOW_LOADING_TIME_DELAY = 3000L
    }

    var meditationStatusPlayer: MeditationStatusPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selfView = inflater.inflate(R.layout.fragment_data, container, false)

        meditationStatusPlayer = MeditationStatusPlayer(activity!!)
        initView()
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
            ?.addErrorMessageListener { errorMsg,errorType->
                if (errorType != MeditationInterruptView.ERROR_TYPE_SIGNAL) {
                    (activity as MeditationActivity).pauseMeditation()
                }
                (activity as MeditationActivity).scrollLayout.scrollToOpen()
                isMeditationInterrupt = true
                resetLoading()
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                    ?.showErrorMessage(errorMsg)
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showErrorMessage(errorMsg)
                selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showErrorMessage(errorMsg)
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
                "2" -> {
                    var meditationEmotionView = MeditationEmotionView(activity!!)
                    meditationEmotionView.tag = "Emotion"
                    meditationEmotionView.layoutParams = lp
                    llContainer?.addView(meditationEmotionView)
                }
                "1" -> {
                    var meditationHeartView = MeditationHeartView(activity!!)
                    meditationHeartView.tag = "Heart"
                    meditationHeartView.layoutParams = lp
                    llContainer?.addView(meditationHeartView)
                }
                "0" -> {
                    var meditationBrainwaveView = MeditationBrainwaveView(activity!!)
                    meditationBrainwaveView.tag = "Brainwave"
                    meditationBrainwaveView.layoutParams = lp
                    llContainer?.addView(meditationBrainwaveView)
                }
            }
        }
    }

    var lastReceiveDataTime: String = ""
    fun showBrain(realtimeEEGDataEntity: RealtimeEEGData?) {
        activity?.runOnUiThread {
            lastReceiveDataTime =
                TimeUtils.getFormatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
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
            isBrainViewLoading =
                Collections.max(realtimeEEGDataEntity?.leftwave) == 0.0 && Collections.max(
                    realtimeEEGDataEntity?.rightwave
                ) == 0.0
            if (!isMeditationInterrupt) {
                if (isBrainViewLoading) {
                    mMainHandler.postDelayed(showBrainLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showBrainLoadingRunnable)
                    selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
                        ?.hindLoadingCover()
                }
            }
        }
    }

    var showBrainLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
            ?.showLoadingCover()
    }

    var showHRLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
    }

    var showHRVLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVLoadingCover()
    }

    var showAttentionLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showAttentionLoading()
    }

    var showRelaxationLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showRelaxationLoading()
    }

    var showPressureLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showPressureLoading()
    }

    var showArousalLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showArousalLoading()
    }

    var showCoherenceLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showCoherenceLoading()
    }
    var showPleasureLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showPleasureLoading()
    }
    var showSleepLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
            ?.showSleepLoading()
    }

    fun showHeart(heartRate: Int?, hrv: Double?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHRV(hrv)
            isHeartViewLoading = heartRate == 0
            isHRVViewLoading = hrv == 0.0
            if (!isMeditationInterrupt) {
                if (isHeartViewLoading) {
                    mMainHandler.postDelayed(showHRLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showHRLoadingRunnable)
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRLoadingCover()
                }
                if (isHRVViewLoading) {
                    mMainHandler.postDelayed(showHRVLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showHRVLoadingRunnable)
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
            isAttentionLoading = attention == 0f
            if (!isMeditationInterrupt) {
                if (isAttentionLoading) {
                    mMainHandler.postDelayed(showAttentionLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showAttentionLoadingRunnable)
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
            isRelaxationLoading = relaxation == 0f

            if (!isMeditationInterrupt) {
                if (isRelaxationLoading) {
                    mMainHandler.postDelayed(showRelaxationLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showRelaxationLoadingRunnable)
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
                ?.setStress(pressure)
            isPressureLoading = pressure == 0f
            if (!isMeditationInterrupt) {
                if (isPressureLoading) {
                    mMainHandler.postDelayed(showPressureLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showPressureLoadingRunnable)
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
                ?.setPleasure(formatNum(mood / 25f - 2))
        }
    }

    fun showArousal(arousal: Float?) {
        if (arousal == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setArousal(arousal)
            isArousalLoading = arousal == 0f

            if (!isMeditationInterrupt) {
                if (isArousalLoading) {
                    mMainHandler.postDelayed(showArousalLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showArousalLoadingRunnable)
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hideArousalLoaidng()
                }
            }
        }
    }

    fun showCoherence(coherence: Float?) {
        if (coherence == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setCoherence(coherence)
            isCoherenceLoading = coherence == 0f
            if (!isMeditationInterrupt) {
                if (isCoherenceLoading) {
                    mMainHandler.postDelayed(showCoherenceLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showCoherenceLoadingRunnable)
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hideCoherenceLoaidng()
                }
            }
        }
    }

    fun showPleasure(pleasure: Float?) {
        if (pleasure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setPleasure(pleasure)
            isPleasureLoading = pleasure == 0f
            if (!isMeditationInterrupt) {
                if (isPleasureLoading) {
                    mMainHandler.postDelayed(showPleasureLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showPleasureLoadingRunnable)
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hidePleasureLoaidng()
                }
            }
        }
    }

    fun showSleep(sleep: Float?) {
        if (sleep == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setSleepState(sleep)
            isSleepLoading = sleep == 0f
            if (!isMeditationInterrupt) {
                if (isSleepLoading) {
                    mMainHandler.postDelayed(showSleepLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showSleepLoadingRunnable)
                    selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                        ?.hideSleepLoaidng()
                }
            }
        }
    }

    var runnable = Runnable {
        if (!isMeditationInterrupt) {
            handleInterruptTip()
        }
    }
    var isTimerScheduling = false
    var mMainHandler = Handler(Looper.getMainLooper())
    var mDeviceHandler = Handler(Looper.getMainLooper())
    var mNetHandler = Handler(Looper.getMainLooper())

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
        activity?.runOnUiThread {
//            Log.d("#####", "websocketconnectListener")
            isFirstIn = false
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_NET)
//            showLoadingCover()
            handleInterruptTip()
        }
    }
    var restoreDelay = 0L
    var reconnectRunnable = {
        restoreWebSocket()
    }
    var websocketDisconnectListener = fun(error: String) {
        (activity as MeditationActivity).affectiveCloudService?.setInit(false)
        activity?.runOnUiThread {
            isMeditationInterrupt = true
            showLoadingCover()
            if (!isNeedWebSocketReconnect) {
                handleInterruptTip()
            }
            DeviceUIConfig.getInstance(Application.getInstance()).managers[0].stopHeartAndBrainCollection()
            MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_NET)
//            Log.d("#####", "websocketDisconnectListener")
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
                    mNetHandler.removeCallbacks(reconnectRunnable)
                    isNeedWebSocketReconnect = false
                    handleInterruptTip()
                }
            }
            if (isNeedWebSocketReconnect) {
                mNetHandler.postDelayed(reconnectRunnable, restoreDelay)
            }
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

    fun initNetListener() {
        (activity as MeditationActivity).affectiveCloudService?.addWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationActivity).affectiveCloudService?.addWebSocketDisconnectListener(
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
        isNeedDeviceReconnect = true
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
        if (activity is MeditationActivity) {
            var intent = Intent(activity!!, SensorContactCheckActivity::class.java)
            activity!!.startActivity(intent)
        }
    }

    fun showMiniBar() {
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility =
            View.VISIBLE
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.GONE
        if (!BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text =
                "Connect the headhand to show data"
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationActivity).scrollLayout.scrollToOpen()
            }
        } else {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text = getString(R.string.meditation_tap_to_show_data)
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationActivity).scrollLayout.scrollToOpen()
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
            meditationStatusPlayer?.playRecordStartAudio()
        }
    }

    fun playDisconnectAudio() {
        if (!isFirstIn && selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.visibility ==
            View.GONE
        ) {
            meditationStatusPlayer?.playRecordEndAudio()
        }
    }

    fun hideInterruptTip() {
        if (!(activity as MeditationActivity).needToCheckSensor) {
            (activity as MeditationActivity).resumeMeditation()
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
            if ((activity as MeditationActivity).affectiveCloudService!!.isConnected()) {
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
            } else if (!(activity as MeditationActivity).affectiveCloudService!!.isConnected() && (activity as MeditationActivity).affectiveCloudService!!.getSessionId() != null) {
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

    var isNeedDeviceReconnect = true
    var reconnectDeviceDelay = 0L
    var reconnectDeviceRunnable = object : Runnable {
        override fun run() {
            biomoduleBleManager?.scanNearDeviceAndConnect(fun() {
                activity?.runOnUiThread {
                    Logger.d("scan succ")
                }
            }, fun(e: Exception) {
                activity?.runOnUiThread {
                    handleReconnectDevice()
                }
            }, fun(mac: String) {
                activity?.runOnUiThread {
                    Logger.d("connect succ shake succ" + mac)
//                    SettingManager.getInstance().mac = mac
//                    SettingManager.getInstance().isConnectBefore = true
                    var messageEvent = MessageEvent()
                    messageEvent.message = "device connect"
                    messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DEVICE_CONNECT
                    EventBus.getDefault().post(messageEvent)
                }
            }, fun(error: String) {
                activity?.runOnUiThread {
//                    handleReconnectDevice()
                    Logger.d("connect failure")
                }
            })
        }

    }
    var onDeviceConnectListener = fun(str: String) {
        reconnectDeviceDelay = 0L
        isNeedDeviceReconnect = true
        isBleConnected = true
        activity?.runOnUiThread {
            handleInterruptTip()
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_DEVICE)
            if ((activity as MeditationActivity).affectiveCloudService!!.isInited()) {
                (activity as MeditationActivity).affectiveCloudService!!.restoreCloud(object :
                    Callback {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess() {
                        biomoduleBleManager?.startHeartAndBrainCollection()
                        biomoduleBleManager?.startContact()
                    }

                })
            } else {
                (activity as MeditationActivity).affectiveCloudService!!.connectCloud(object :
                    Callback {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess() {
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
//        Log.d("####","is ble connected :${biomoduleBleManager!!.isConnected()}")
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

    fun handleReconnectDevice() {
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
                mDeviceHandler.removeCallbacks(reconnectDeviceRunnable)
                isBleConnected = false
                isNeedDeviceReconnect = false
                handleInterruptTip()
            }
        }
        if (isNeedDeviceReconnect) {
            mDeviceHandler.postDelayed(reconnectDeviceRunnable, reconnectDeviceDelay)
        }
    }

    private fun initDeviceConnectListener() {
        biomoduleBleManager = DeviceUIConfig.getInstance(Application.getInstance()).managers[0]
        if (biomoduleBleManager!!.isConnected()) {
            isBleConnected = true
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility =
                View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toSignalBad(toSignalCheckListener)
        } else {
            isBleConnected = false
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceListener)
//            showSampleData()
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
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showSampleData()
    }

    fun hideSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRVSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hideSampleData()
        showLoadingCover()
    }

    fun showLoadingCover() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showAttentionLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showRelaxationLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showPressureLoading()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
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
        mNetHandler.removeCallbacks(reconnectRunnable)
        mDeviceHandler.removeCallbacks(reconnectDeviceRunnable)
        biomoduleBleManager?.removeConnectListener(onDeviceConnectListener)
        biomoduleBleManager?.removeDisConnectListener(onDeviceDisconnectListener)
        biomoduleBleManager?.removeContactListener(::onBleContactListener)
        (activity as MeditationActivity).affectiveCloudService?.removeWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationActivity).affectiveCloudService?.removeWebSocketDisconnectListener(
            websocketDisconnectListener
        )
        meditationStatusPlayer?.release()
        super.onDestroy()
    }
}
