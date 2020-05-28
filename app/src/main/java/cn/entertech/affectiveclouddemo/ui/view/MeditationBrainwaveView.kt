package cn.entertech.affectiveclouddemo.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.uicomponentsdk.realtime.RealtimeBrainwaveSpectrumView
import cn.entertech.uicomponentsdk.realtime.RealtimeBrainwaveView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.card_meditation_brainwave.view.*

class MeditationBrainwaveView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View =
        LayoutInflater.from(context).inflate(R.layout.card_meditation_brainwave, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()
    }

    fun initView() {
        brainwave.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigEEGRealtimeInfo
        )
        brainwave_spectrum.setIsShowInfoIcon(
            true,
            url = SettingManager.getInstance().remoteConfigBrainRealtimeInfo
        )
    }

    fun setLeftBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).setLeftBrainwave(data)
    }

    fun setRightBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).setRightBrainwave(data)
    }

    fun setGammaWavePercent(percent: Float?) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .setGammaWavePercent(percent)
    }

    fun setBetaWavePercent(percent: Float?) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .setBetaWavePercent(percent)
    }

    fun setAlphaWavePercent(percent: Float?) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .setAlphaWavePercent(percent)
    }

    fun setThetaWavePercent(percent: Float?) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .setThetaWavePercent(percent)
    }

    fun setDeltaWavePercent(percent: Float?) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .setDeltaWavePercent(percent)
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum).showLoading()
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).showLoading()
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum).hideLoading()
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).hideLoading()
    }

    fun showSampleData() {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .showDisconnectTip()
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).showDisconnectTip()
    }

    fun hideSampleData() {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .hideLoading()
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).hideDisconnectTip()
    }


    fun showErrorMessage(error: String) {
        mSelfView.findViewById<RealtimeBrainwaveSpectrumView>(R.id.brainwave_spectrum)
            .showErrorMessage(error)
        mSelfView.findViewById<RealtimeBrainwaveView>(R.id.brainwave).showErrorMessage(error)
    }

}