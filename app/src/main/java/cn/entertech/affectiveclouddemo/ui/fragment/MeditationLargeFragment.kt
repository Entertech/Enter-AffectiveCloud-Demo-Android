package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.ui.activity.MeditationActivity
import cn.entertech.affectiveclouddemo.ui.activity.SensorContactCheckActivity
import cn.entertech.affectiveclouddemo.ui.view.*
import cn.entertech.affectiveclouddemo.utils.MeditationStatusPlayer
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import com.bumptech.glide.Glide
import java.util.*


class MeditationLargeFragment : MeditationBaseFragment() {
    private var mArousal: Float = 0f
    private var mPleasure: Float = 0f
    private var mAttention: Float = 0f
    private var mRelaxation: Float = 0f
    private var mPressure: Float = 0f
    var selfView: View? = null
    var isHeartViewLoading = true
    var isBrainViewLoading = true
    var isAttentionLoading = true
    var isRelaxationLoading = true
    var isPressureLoading = true
    var isArousalLoading = true
    var isPleasureLoading = true
    var isCoherenceLoading = true

    private var isMeditationInterrupt: Boolean = false
    var isTimerScheduling = false
    var isBleConnected: Boolean = false
    var meditationStatusPlayer: MeditationStatusPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        selfView = inflater.inflate(R.layout.fragment_data_large, container, false)
        meditationStatusPlayer = MeditationStatusPlayer(activity!!)
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
        this.mAttention = attention
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

        this.mRelaxation = relaxation
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

        this.mPressure = pressure
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

    override fun showArousal(arousal: Float?) {
        if (arousal == null) {
            return
        }
        this.mArousal = arousal
        activity?.runOnUiThread {
            selfView?.findViewById<RealtimePleasureAndArousalView>(R.id.realtime_arousal_pleasure)
                ?.setArousal(arousal.toDouble())
            selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")
                ?.setArousal(arousal)
            if (arousal != 0f) {
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
        this.mPleasure = pleasure
        activity?.runOnUiThread {
            selfView?.findViewById<RealtimePleasureAndArousalView>(R.id.realtime_arousal_pleasure)
                ?.setPleasure(pleasure.toDouble())
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
            showArousalAndPleasureEmotion(mArousal, mPleasure)
            showAffectiveLineChart(mAttention,mRelaxation,mPressure,mArousal,mPleasure)
        }
    }

    private fun showAffectiveLineChart(attention:Float,relaxation: Float,pressure: Float,arousal: Float,pleasure: Float){
        selfView?.findViewById<AffectiveSurfaceView>(R.id.realtime_affective_line_chart)?.setData(attention,relaxation,pressure,pleasure,arousal)
    }

    private fun showArousalAndPleasureEmotion(arousal: Float, pleasure: Float) {
        var targetView = selfView?.findViewById<ImageView>(R.id.iv_emotion)
        if (pleasure in 0.0..22.0 && arousal in 75.0..100.0) {
            Glide.with(activity!!).load(R.mipmap.p0_22a75_100).into(targetView!!)
        } else if (pleasure in 0.0..40.0 && arousal in 0.0..25.0) {
            Glide.with(activity!!).load(R.mipmap.p0_40a0_25).into(targetView!!)
        } else if (pleasure in 0.0..40.0 && arousal in 25.0..50.0) {
            Glide.with(activity!!).load(R.mipmap.p0_40a25_50).into(targetView!!)
        } else if (pleasure in 0.0..45.0 && arousal in 50.0..75.0) {
            Glide.with(activity!!).load(R.mipmap.p0_45a50_75).into(targetView!!)
        } else if (pleasure in 22.0..45.0 && arousal in 75.0..100.0) {
            Glide.with(activity!!).load(R.mipmap.p22_45a75_100).into(targetView!!)
        } else if (pleasure in 40.0..60.0 && arousal in 0.0..25.0) {
            Glide.with(activity!!).load(R.mipmap.p40_60a0_25).into(targetView!!)
        } else if (pleasure in 45.0..55.0 && arousal in 75.0..100.0) {
            Glide.with(activity!!).load(R.mipmap.p45_55a75_100).into(targetView!!)
        } else if (pleasure in 55.0..78.0 && arousal in 75.0..100.0) {
            Glide.with(activity!!).load(R.mipmap.p55_78a75_100).into(targetView!!)
        } else if (pleasure in 60.0..100.0 && arousal in 0.0..25.0) {
            Glide.with(activity!!).load(R.mipmap.p60_100a0_25).into(targetView!!)
        } else if (pleasure in 60.0..100.0 && arousal in 25.0..50.0) {
            Glide.with(activity!!).load(R.mipmap.p60_100a25_50).into(targetView!!)
        } else if (pleasure in 75.0..100.0 && arousal in 50.0..75.0) {
            Glide.with(activity!!).load(R.mipmap.p75_100a50_75).into(targetView!!)
        } else if (pleasure in 78.0..100.0 && arousal in 75.0..100.0) {
            Glide.with(activity!!).load(R.mipmap.p78_100a75_100).into(targetView!!)
        } else {
            Glide.with(activity!!).load(R.mipmap.pic_arousal_pleasure_emotion_else).into(targetView!!)
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

    var toConnectDeviceListener = fun() {
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
        handleInterruptTip()
    }

    override fun handleDeviceConnect() {
        handleInterruptTip()
    }

    override fun handleWebSocketDisconnect() {
        handleInterruptTip()
    }

    override fun handleWebSocketConnect() {
        handleInterruptTip()
    }

    var isFirstIn = true
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


    var toSignalCheckListener = fun() {
        if (activity is MeditationActivity) {
            activity!!.startActivity(
                Intent(activity!!, SensorContactCheckActivity::class.java).putExtra(
                    Constant.MEDITATION_TYPE,
                    "meditation"
                )
            )
        } else {
            activity!!.startActivity(
                Intent(activity!!, SensorContactCheckActivity::class.java).putExtra(
                    Constant.MEDITATION_TYPE,
                    "meditation"
                )
            )
        }
    }

    fun hideInterruptTip() {
        isTimerScheduling = false
        playConnectAudio()
        isMeditationInterrupt = false
        showLoadingCover()
        selfView?.findViewById<MeditationInterruptView>(R.id.miv_interrupt_device)
            ?.visibility =
            View.GONE
    }

    override fun handleInterruptTip() {
        if (isBleConnected) {
            if ((activity as MeditationActivity).enterAffectiveCloudManager!!.isWebSocketOpen()) {
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
}
