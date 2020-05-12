package cn.entertech.affectiveclouddemo.ui.fragment

import androidx.fragment.app.Fragment
import cn.entertech.affectivecloudsdk.entity.RealtimeEEGData

abstract class MeditationBaseFragment : Fragment() {
    abstract fun showHeart(hr: Int?, hrv: Double?)
    abstract fun showBrain(data: RealtimeEEGData?)
    abstract fun showAttention(attention: Float?)
    abstract fun showRelaxation(relaxation: Float?)
    abstract fun showPressure(pressure: Float?)
    abstract fun showArousal(arousal: Float?)
    abstract fun showPleasure(pleasureL: Float?)
    abstract fun showCoherence(coherence: Float?)
    abstract fun handleDeviceDisconnect()
    abstract fun handleDeviceConnect()
    abstract fun handleWebSocketDisconnect()
    abstract fun handleWebSocketConnect()
    abstract fun handleInterruptTip()
    abstract fun dealQuality(quality:Double?)
}