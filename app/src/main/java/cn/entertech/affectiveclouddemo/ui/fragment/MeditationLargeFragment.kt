package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.ui.activity.MeditationActivity
import cn.entertech.affectiveclouddemo.ui.view.MeditationBrainwaveView
import cn.entertech.affectiveclouddemo.ui.view.MeditationEmotionLargeView
import cn.entertech.affectiveclouddemo.ui.view.MeditationHeartView
import cn.entertech.affectiveclouddemo.ui.view.MeditationInterruptView
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import java.util.*


class MeditationLargeFragment : MeditationBaseFragment() {
    var selfView: View? = null
    var isHeartViewLoading = true
    var isBrainViewLoading = true
    var isAttentionLoading = true
    var isRelaxationLoading = true
    var isPressureLoading = true
    var isArousalLoading = true
    var isPleasureLoading = true
    var isCoherenceLoading = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selfView = inflater.inflate(R.layout.fragment_data_large, container, false)
        initView()
        return selfView
    }


    fun initView() {
        selfView?.findViewById<ImageView>(R.id.iv_close)?.setOnClickListener {
            (activity as MeditationActivity).showDialog()
        }

        if ((activity as MeditationActivity).bleManager!!.isConnected()) {
            handleDeviceConnect()
        } else {
            handleDeviceDisconnect()
        }
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRVLoadingCover()
    }

    override fun showBrain(realtimeEEGDataEntity: RealtimeEEGData?) {
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

    override fun showHeart(heartRate: Int?, hrv: Double?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHRV(hrv)
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            isHeartViewLoading = heartRate == 0
            Log.d("###", "isHeartViewLoading:" + isHeartViewLoading + ":" + heartRate)
            if (isHeartViewLoading) {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
            } else {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindHRLoadingCover()
            }
            if (hrv != 0.0) {
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hindHRVLoadingCover()
            }
        }
    }

    override fun showAttention(attention: Float?) {
        if (attention == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setAttention(attention)
            if (attention != 0f) {
                isAttentionLoading = false
            }
            if (isAttentionLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showAttentionLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hideAttentionLoaidng()
            }
        }
    }

    override fun showRelaxation(relaxation: Float?) {
        if (relaxation == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setRelaxation(relaxation)
            if (relaxation != 0f) {
                isRelaxationLoading = false
            }
            if (isRelaxationLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showRelaxationLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hideRelaxationLoaidng()
            }
        }
    }

    override fun showPressure(pressure: Float?) {
        if (pressure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setStress(pressure)
            if (pressure != 0f) {
                isPressureLoading = false
            }
            if (isPressureLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showPressureLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hidePressureLoaidng()
            }
        }
    }

    override fun showArousal(mood: Float?) {
        if (mood == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setArousal(mood)
            if (mood != 0f) {
                isArousalLoading = false
            }
            if (isArousalLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showArousalLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hideArousalLoaidng()
            }
        }
    }

    override fun showPleasure(pleasure: Float?) {
        if (pleasure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setPleasure(pleasure)
            if (pleasure != 0f) {
                isPleasureLoading = false
            }
            if (isPleasureLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showPleasureLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hidePleasureLoaidng()
            }
        }
    }

    override fun showCoherence(coherence: Float?) {
        if (coherence == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setCoherence(coherence)
            if (coherence != 0f) {
                isCoherenceLoading = false
            }
            if (isCoherenceLoading) {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.showCoherenceLoading()
            } else {
                selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                    ?.hideCoherenceLoaidng()
            }
        }
    }

    fun dataReset() {
        showRelaxation(0f)
        showAttention(0f)
        showPressure(0f)
        showArousal(0f)
        showHeart(0, 0.0)
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showLoadingCover()
    }

    var toConnectDeviceLinstener = fun() {
        startActivity(Intent(activity, DeviceManagerActivity::class.java))
    }

    var toNetRestoreLinstener = fun() {
        (activity as MeditationActivity).restore()
    }

    fun showSampleData() {
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.showSampleData()
    }

    fun hideSampleData() {
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.hideSampleData()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRSampleData()
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")?.hideSampleData()
        showLoadingCover()
    }

    fun showLoadingCover() {
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showAttentionLoading()
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showRelaxationLoading()
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showPressureLoading()
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showArousalLoading()
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

    override fun handleDeviceDisconnect() {
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
            View.VISIBLE
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
            ?.toDeviceDisconnect(toConnectDeviceLinstener)
//            rl_minibar_disconnect.visibility = View.VISIBLE
//        rl_minibar_connect.visibility = View.GONE
        showSampleData()
    }

    override fun handleDeviceConnect() {
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)?.visibility =
            View.GONE
//        rl_minibar_connect.visibility = View.VISIBLE
        hideSampleData()
    }

    override fun handleWebSocketDisconnect() {
        resetLoading()
        dataReset()
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
            View.VISIBLE
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)
            ?.toNetDisconnect(toNetRestoreLinstener)
    }

    override fun handleWebSocketConnect() {
        showLoadingCover()
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_net)?.visibility =
            View.GONE
    }
}
