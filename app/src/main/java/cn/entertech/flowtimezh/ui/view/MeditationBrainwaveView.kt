package cn.entertech.flowtimezh.ui.view;

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.uicomponentsdk.realtime.BrainWaveSurfaceView
import cn.entertech.uicomponentsdk.realtime.PercentProgressBar
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_meditation_brainwave.view.*

class MeditationBrainwaveView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_brainwave, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        initView()
        addView(mSelfView)
    }

    fun initView() {
        mSelfView.findViewById<ImageView>(R.id.iv_brain_real_time_info).setOnClickListener {
//            val intent = Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Brainwave")
//            intent.putExtra(ExtraKey.EXTRA_URL, SettingManager.getInstance().remoteConfigEEGRealtimeInfo)
//            context.startActivity(intent)
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigEEGRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        mSelfView.findViewById<ImageView>(R.id.iv_spectrum_real_time_info).setOnClickListener {
//                val intent = Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Brainwave Power")
//            intent.putExtra(ExtraKey.EXTRA_URL, SettingManager.getInstance().remoteConfigBrainRealtimeInfo)
//            context.startActivity(intent)
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigBrainRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun setLeftBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_left).setData(data)
    }

    fun setRightBrainwave(data: ArrayList<Double>?) {
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_right).setData(data)
    }

    fun setGammaWavePercent(percent: Float?) {
        mSelfView.findViewById<PercentProgressBar>(R.id.ppb_one).setPercent(percent)
    }

    fun setBetaWavePercent(percent: Float?) {
        mSelfView.findViewById<PercentProgressBar>(R.id.ppb_two).setPercent(percent)
    }

    fun setAlphaWavePercent(percent: Float?) {
        mSelfView.findViewById<PercentProgressBar>(R.id.ppb_three).setPercent(percent)
    }

    fun setThetaWavePercent(percent: Float?) {
        mSelfView.findViewById<PercentProgressBar>(R.id.ppb_four).setPercent(percent)
    }

    fun setDeltaWavePercent(percent: Float?) {
        mSelfView.findViewById<PercentProgressBar>(R.id.ppb_five).setPercent(percent)
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.GONE
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.GONE
    }

    fun showSampleData() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.VISIBLE
        tv_sample.visibility = View.VISIBLE
        var sampleBrainData = ArrayList<Double>()
        for (i in 0..150) {
            sampleBrainData.add(java.util.Random().nextDouble() * 100.0 - 50)
        }
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_left).setSampleData(sampleBrainData)
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_right).setSampleData(sampleBrainData)
        setAlphaWavePercent(0.2f)
        setBetaWavePercent(0.6f)
        setDeltaWavePercent(0.06f)
        setGammaWavePercent(0.04f)
        setThetaWavePercent(0.1f)
    }

    fun showErrorMessage(error:String){
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).text = error
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).text = error
        setAlphaWavePercent(0f)
        setBetaWavePercent(0f)
        setDeltaWavePercent(0f)
        setGammaWavePercent(0f)
        setThetaWavePercent(0f)
    }

    fun hideSampleData() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.GONE
        tv_sample.visibility = View.GONE
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_left).hideSampleData()
        mSelfView.findViewById<BrainWaveSurfaceView>(R.id.bsv_brainwave_right).hideSampleData()
    }

}