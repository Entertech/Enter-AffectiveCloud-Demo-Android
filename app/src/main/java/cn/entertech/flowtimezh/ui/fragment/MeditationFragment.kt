package cn.entertech.flowtimezh.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

import org.greenrobot.eventbus.EventBus
import android.util.Log
import android.widget.RelativeLayout
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.flowtimezh.ui.activity.MeditationActivity
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_DEVICE
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager.Companion.INTERRUPT_TYPE_NET
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.entity.MessageEvent
import cn.entertech.flowtimezh.ui.view.*
import cn.entertech.flowtimezh.utils.MeditationStatusPlayer
import cn.entertech.flowtimezh.utils.ScreenUtil
import cn.entertech.flowtimezh.utils.formatNum
import kotlinx.android.synthetic.main.fragment_data.*
import java.util.*


class MeditationFragment : androidx.fragment.app.Fragment() {
    private var biomoduleBleManager: MultipleBiomoduleBleManager? = null
    var selfView: View? = null
    var smartScrollView: SmartScrollView? = null
    var llContainer: LinearLayout? = null
    var isHeartViewLoading = true
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
        smartScrollView?.setSmartScrollChangedListener(object : SmartScrollView.ISmartScrollChangedListener {
            override fun onScrolledNotTop() {
                mIScrollTopListener?.isScrollTop(false)
            }

            override fun onScrolledToBottom() {
            }

            override fun onScrolledToTop() {
                mIScrollTopListener?.isScrollTop(true)
            }
        })

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
            if (Collections.max(realtimeEEGDataEntity?.leftwave) != 0.0 || Collections.max(realtimeEEGDataEntity?.rightwave) != 0.0) {
                isBrainViewLoading = false
            }
            if (isBrainViewLoading) {
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
            } else {
                selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hindLoadingCover()
            }
        }
    }

    fun showHeart(heartRate: Int?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            isHeartViewLoading = heartRate == 0
            Log.d("###", "isHeartViewLoading:" + isHeartViewLoading + ":" + heartRate)
            if (isHeartViewLoading) {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showLoadingCover()
            } else {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindLoadingCover()
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
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setStress(formatNum(pressure / 20f))
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
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.setArousal(formatNum(mood / 25f - 2))
        }
    }

    var isFirstIn = true
    var websocketConnectListener = fun() {
        activity?.runOnUiThread {
            if (biomoduleBleManager!!.isConnected() && !isFirstIn) {
                meditationStatusPlayer?.playConnectAudio()
            }
            isFirstIn = false
            MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_NET)
            showLoadingCover()
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility = View.GONE
        }
    }
    var websocketDisconnectListener = fun() {
        activity?.runOnUiThread {
            if (biomoduleBleManager!!.isConnected()) {
                meditationStatusPlayer?.playDisconnectAudio()
            }
            resetLoading()
            dataReset()
            BiomoduleBleManager.getInstance(Application.getInstance()).stopHeartAndBrainCollection()
            MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_NET)
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)
                ?.toNetDisconnect(toNetRestoreLinstener)
        }
    }

    fun dataReset() {
        showRelaxation(0f)
        showAttention(0f)
        showPressure(0f)
        showMood(0f)
        showHeart(0)
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    private fun initNetListener() {
        (activity as MeditationActivity).enterAffectiveCloudManager?.addWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationActivity).enterAffectiveCloudManager?.addWebSocketDisconnectListener(
            websocketDisconnectListener
        )
    }

    var toConnectDeviceLinstener = fun() {
        var messageEvent = MessageEvent()
        messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_DEVICE_CONNECT
        messageEvent.message = "connectDevice"
        EventBus.getDefault().post(messageEvent)
    }

    var toNetRestoreLinstener = fun() {
        var messageEvent = MessageEvent()
        messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_NET_RESTORE
        messageEvent.message = "netRestore"
        EventBus.getDefault().post(messageEvent)
    }

    fun showMiniBar() {
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility = View.VISIBLE
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.GONE
        if (!BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text = "Connect the headhand to show data"
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationActivity).scrollLayout.scrollToOpen()
            }
        } else {
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.text = "Tap to show biodata"
            selfView?.findViewById<TextView>(R.id.tv_minibar_text)?.setOnClickListener {
                (activity as MeditationActivity).scrollLayout.scrollToOpen()
            }
        }
    }

    fun hideMiniBar() {
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility = View.GONE
        selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.VISIBLE
    }

    var onDeviceConnectListener = fun(str: String) {
        if ((activity as MeditationActivity).enterAffectiveCloudManager!!.isWebSocketOpen()) {
            meditationStatusPlayer?.playConnectAudio()
        }
        MeditaionInterruptManager.getInstance().popInterrupt(INTERRUPT_TYPE_DEVICE)
        activity?.runOnUiThread {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility = View.GONE
            if ((activity as MeditationActivity).enterAffectiveCloudManager!!.isInited()) {
                (activity as MeditationActivity).enterAffectiveCloudManager!!.restore(object : Callback {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess() {
                        biomoduleBleManager?.startHeartAndBrainCollection()
                        biomoduleBleManager?.startContact()
                    }

                })
            } else {
                (activity as MeditationActivity).enterAffectiveCloudManager!!.init(object : Callback {
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

    var onDeviceDisconnectListener = fun(str: String) {
        if ((activity as MeditationActivity).enterAffectiveCloudManager!!.isWebSocketOpen()) {
            meditationStatusPlayer?.playDisconnectAudio()
        }
        MeditaionInterruptManager.getInstance().pushInterrupt(INTERRUPT_TYPE_DEVICE)
        activity?.runOnUiThread {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceLinstener)
            rl_minibar_disconnect.visibility = View.VISIBLE
            rl_minibar_connect.visibility = View.GONE
        }
        Thread.sleep(1000)
        activity?.runOnUiThread {
            showSampleData()
        }
    }

    private fun initDeviceConnectListener() {
        biomoduleBleManager = DeviceUIConfig.getInstance(activity!!).managers[0]
        if (biomoduleBleManager!!.isConnected()) {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility = View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility = View.GONE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.VISIBLE
        } else {
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
                View.VISIBLE
            selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
                ?.toDeviceDisconnect(toConnectDeviceLinstener)
            showSampleData()
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_disconnect)?.visibility = View.VISIBLE
            selfView?.findViewById<RelativeLayout>(R.id.rl_minibar_connect)?.visibility = View.GONE
        }
        biomoduleBleManager?.addConnectListener(onDeviceConnectListener)
        biomoduleBleManager?.addDisConnectListener(onDeviceDisconnectListener)
    }

    fun showSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showSampleData()
    }

    fun hideSampleData() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hideSampleData()
        showLoadingCover()
    }

    fun showLoadingCover() {
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showAttentionLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showRelaxationLoading()
        selfView?.findViewWithTag<MeditationEmotionView>("Emotion")?.showPressureLoading()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showLoadingCover()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    fun resetLoading() {
        isHeartViewLoading = true
        isBrainViewLoading = true
        isAttentionLoading = true
        isRelaxationLoading = true
        isPressureLoading = true
    }

    override fun onDestroy() {
        biomoduleBleManager?.removeConnectListener(onDeviceConnectListener)
        biomoduleBleManager?.removeDisConnectListener(onDeviceDisconnectListener)
        (activity as MeditationActivity).enterAffectiveCloudManager?.removeWebSocketConnectListener(
            websocketConnectListener
        )
        (activity as MeditationActivity).enterAffectiveCloudManager?.removeWebSocketDisconnectListener(
            websocketDisconnectListener
        )
        meditationStatusPlayer?.release()
        super.onDestroy()
    }
}
