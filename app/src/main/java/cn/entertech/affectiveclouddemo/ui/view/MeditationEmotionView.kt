package cn.entertech.affectiveclouddemo.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.SettingManager
import kotlinx.android.synthetic.main.card_meditation_emotion.view.*

class MeditationEmotionView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var minHeart: Int = 0
    var maxHeart: Int = 0
    var isFirstLoad = true
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.card_meditation_emotion, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()
    }

    fun initView() {
        realtime_attention_view.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigAttentionRealtimeInfo
        )
        realtime_relaxation_view.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigRelaxationRealtimeInfo
        )
        realtime_pressure_view.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigPressureRealtimeInfo
        )
//        realtime_arousal_view.setIsShowInfoIcon(
//            true,
//            url = SettingManager.getInstance().remoteConfig
//        )
//        realtime_pleasure_view.setIsShowInfoIcon(
//            true,
//            url = SettingManager.getInstance().remoteConfigAttentionRealtimeInfo
//        )
        realtime_coherence_view.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigCoherenceRealtimeInfo
        )
    }

    fun setAttention(value: Float?) {
        if (value == null) {
            return
        }
        realtime_attention_view.setValue(value)
    }

    fun setRelaxation(value: Float?) {
        if (value == null) {
            return
        }
        realtime_relaxation_view.setValue(value)
    }

    fun setStress(value: Float?) {
        if (value == null) {
            return
        }
        realtime_pressure_view.setValue(value)
    }

    fun setArousal(value: Float?) {
        realtime_arousal_view.setValue(value)
    }

    fun setPleasure(value: Float?) {
        realtime_pleasure_view.setValue(value)
    }

    fun setCoherence(value: Float?) {
        realtime_coherence_view.setValue(value)
    }

    fun showSampleData() {
        realtime_pressure_view.showDisconnectTip()
        realtime_relaxation_view.showDisconnectTip()
        realtime_attention_view.showDisconnectTip()
        realtime_arousal_view.showDisconnectTip()
        realtime_pleasure_view.showDisconnectTip()
        realtime_coherence_view.showDisconnectTip()
    }

    fun hideSampleData() {
        tv_sample.visibility = View.GONE
    }

    fun showLoadingCover() {
        realtime_attention_view.showLoading()
        realtime_relaxation_view.showLoading()
        realtime_pressure_view.showLoading()
        realtime_arousal_view.showLoading()
    }

    fun showAttentionLoading() {
        realtime_attention_view.showLoading()
    }

    fun showRelaxationLoading() {
        realtime_relaxation_view.showLoading()
    }

    fun showPressureLoading() {
        realtime_pressure_view.showLoading()
    }

    fun showArousalLoading() {
        realtime_arousal_view.showLoading()
    }

    fun showPleasureLoading() {
        realtime_pleasure_view.showLoading()
    }

    fun showCoherenceLoading() {
        realtime_coherence_view.showLoading()
    }

    fun hideLoading() {
        realtime_arousal_view.hideLoading()
        realtime_attention_view.hideLoading()
        realtime_relaxation_view.hideLoading()
        realtime_pressure_view.hideLoading()
    }

    fun hideAttentionLoaidng() {
        realtime_attention_view.hideLoading()
    }

    fun hideRelaxationLoaidng() {
        realtime_relaxation_view.hideLoading()
    }

    fun hidePressureLoaidng() {
        realtime_pressure_view.hideLoading()
    }

    fun hideArousalLoaidng() {
        realtime_arousal_view.hideLoading()
    }

    fun hidePleasureLoaidng() {
        realtime_pleasure_view.hideLoading()
    }

    fun hideCoherenceLoaidng() {
        realtime_coherence_view.hideLoading()
    }

    fun showErrorMessage(error: String) {
        realtime_attention_view.showErrorMessage(error)
        realtime_relaxation_view.showErrorMessage(error)
        realtime_pressure_view.showErrorMessage(error)
        realtime_arousal_view.showErrorMessage(error)
        realtime_pleasure_view.showErrorMessage(error)
        realtime_coherence_view.showErrorMessage(error)
    }


}