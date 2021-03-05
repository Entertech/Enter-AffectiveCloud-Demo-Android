package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.ui.activity.MeditationActivity
import cn.entertech.affectiveclouddemo.ui.activity.SensorContactCheckActivity
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationFragment.Companion.SHOW_LOADING_TIME_DELAY
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
    var isBreathCoherenceLoading = true

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

        if ((activity as MeditationActivity).bleManager.isConnected()) {
            handleDeviceConnect()
        } else {
            handleDeviceDisconnect()
        }
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showBreathCoherenceLoadingCover()
    }

    var showBrainLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationBrainwaveView>("Brainwave")
            ?.showLoadingCover()
    }

    var showHRLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
    }

    var showBreathCohLoadingRunnable = Runnable {
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showBreathCoherenceLoadingCover()
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
            if (realtimeEEGDataEntity?.leftwave == null || realtimeEEGDataEntity.leftwave!!.size == 0
                || realtimeEEGDataEntity.rightwave == null || realtimeEEGDataEntity.rightwave!!.size == 0
            ) {
                return@runOnUiThread
            }
            isBrainViewLoading =
                Collections.max(realtimeEEGDataEntity.leftwave) == 0.0 && Collections.max(
                    realtimeEEGDataEntity.rightwave
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

    override fun showHeart(heartRate: Int?) {
        if (heartRate == null) {
            return
        }
        activity?.runOnUiThread {
            isHeartViewLoading = heartRate == 0
            if (heartRate != 0){
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setHeartValue(heartRate)
            }
            if (!isMeditationInterrupt) {
                if (isHeartViewLoading) {
                    mMainHandler.postDelayed(showHRLoadingRunnable, SHOW_LOADING_TIME_DELAY)
                } else {
                    mMainHandler.removeCallbacks(showHRLoadingRunnable)
                    selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideHRLoadingCover()
                }
            }
        }
    }

    override fun showBreathCoherence(hr: Double?){
        if (hr == null){
            return
        }
        isBreathCoherenceLoading = hr == 0.0
        if (hr != 0.0){
            selfView?.findViewWithTag<MeditationHeartView>("Heart")?.setBreathCoherence(listOf(hr))
        }
        if (!isMeditationInterrupt) {
            if (isBreathCoherenceLoading) {
                mMainHandler.postDelayed(showBreathCohLoadingRunnable, SHOW_LOADING_TIME_DELAY)
            } else {
                mMainHandler.removeCallbacks(showBreathCohLoadingRunnable)
                selfView?.findViewWithTag<MeditationHeartView>("Heart")?.hideBreathCoherenceLoadingCover()
            }

        }
    }

    override fun showAttention(attention: Float?) {
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

    override fun showRelaxation(relaxation: Float?) {
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

    override fun showPressure(pressure: Float?) {
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

    override fun showArousal(arousal: Float?) {
        if (arousal == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setStress(arousal)
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

    override fun showPleasure(pleasure: Float?) {
        if (pleasure == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setPleasure(pleasure)
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

    override fun showCoherence(coherence: Float?) {
        if (coherence == null) {
            return
        }
        activity?.runOnUiThread {
            selfView?.findViewWithTag<MeditationEmotionView>("Emotion")
                ?.setCoherence(coherence)
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

    var runnable = Runnable {
        handleInterruptTip()
    }
    var mMainHandler = Handler(Looper.getMainLooper())
    private var lastQuality: Double = 0.0
    override fun dealQuality(quality:Double?){
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


    fun dataReset() {
        showRelaxation(0f)
        showAttention(0f)
        showPressure(0f)
        showArousal(0f)
        showHeart(0)
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
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showCoherenceLoading()
        selfView?.findViewWithTag<MeditationEmotionLargeView>("Emotion")?.showPleasureLoading()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showHRLoadingCover()
        selfView?.findViewWithTag<MeditationHeartView>("Heart")?.showBreathCoherenceLoadingCover()
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
