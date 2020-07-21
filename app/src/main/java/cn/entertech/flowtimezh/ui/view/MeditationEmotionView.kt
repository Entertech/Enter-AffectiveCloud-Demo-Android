package cn.entertech.flowtimezh.ui.view;

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_meditation_emotion.view.*

class MeditationEmotionView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var minHeart: Int = 0
    var maxHeart: Int = 0
    var isFirstLoad = true
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_meditation_emotion, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        initView()
        addView(mSelfView)
    }

    fun initView() {
        var attentionScale = arrayOf(0, 60, 80, 100)
        var relaxationScale = arrayOf(0, 60, 80, 100)
        var stressScale = arrayOf(0, 1, 2, 3, 4, 5)
        var moodScale = arrayOf(-2, -1, 0, 1, 2)
        var attentionIndicatorItems = arrayListOf<EmotionIndicatorAppView.IndicateItem>()
        var relaxationIndicatorItems = arrayListOf<EmotionIndicatorAppView.IndicateItem>()
        var stressIndicatorItems = arrayListOf<EmotionIndicatorAppView.IndicateItem>()
        var moodIndicatorItems = arrayListOf<EmotionIndicatorAppView.IndicateItem>()
        attentionIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.6f, Color.parseColor("#3352a27c")))
        attentionIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.2f, Color.parseColor("#8052a27c")))
        attentionIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.2f, Color.parseColor("#52a27c")))
        relaxationIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.6f, Color.parseColor("#335e75fe")))
        relaxationIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.2f, Color.parseColor("#805e75fe")))
        relaxationIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.2f, Color.parseColor("#5e75fe")))
        stressIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.2f, Color.parseColor("#33cc5268")))
        stressIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.5f, Color.parseColor("#80cc5268")))
        stressIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.3f, Color.parseColor("#cc5268")))
        moodIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.5f, Color.parseColor("#7f725e")))
        moodIndicatorItems.add(EmotionIndicatorAppView.IndicateItem(0.5f, Color.parseColor("#ffc56f")))
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_attention).setScales(attentionScale)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_attention).setIndicatorItems(attentionIndicatorItems)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_relaxation).setScales(relaxationScale)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_relaxation).setIndicatorItems(relaxationIndicatorItems)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_stress).setScales(stressScale)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_stress).setIndicatorItems(stressIndicatorItems)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_mood).setScales(moodScale)
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_mood).setIndicatorItems(moodIndicatorItems)
        mSelfView.findViewById<ImageView>(R.id.iv_attention_real_time_info).setOnClickListener {
//            val intent = Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Attention")
//            intent.putExtra(ExtraKey.EXTRA_URL, SettingManager.getInstance().remoteConfigAttentionRealtimeInfo)
//            context.startActivity(intent)
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigAttentionRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        mSelfView.findViewById<ImageView>(R.id.iv_relaxation_real_time_info).setOnClickListener {
//            val intent = Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Relaxation")
//            intent.putExtra(ExtraKey.EXTRA_URL, SettingManager.getInstance().remoteConfigRelaxationRealtimeInfo)
//            context.startActivity(intent)
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigRelaxationRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        mSelfView.findViewById<ImageView>(R.id.iv_pressure_real_time_info).setOnClickListener {
//            val intent = Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Pressure")
//            intent.putExtra(ExtraKey.EXTRA_URL, SettingManager.getInstance().remoteConfigPressureRealtimeInfo)
//            context.startActivity(intent)
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigPressureRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun setAttention(value: Float?) {
        if (value == null) {
            return
        }
        var valueLevel = if (value >= 0 && value < 60) {
            " (LOW)"
        } else if (value >= 60 && value < 80) {
            " (NORMAL)"
        } else {
            " (HIGH)"
        }
        mSelfView.findViewById<TextView>(R.id.tv_attention_level).text = valueLevel
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_attention).setValue(value)
        mSelfView.findViewById<TextView>(R.id.tv_attention_value).text = "${value.toInt()}"
    }

    fun setRelaxation(value: Float?) {
        if (value == null) {
            return
        }
        var valueLevel = if (value >= 0 && value < 60) {
            " (LOW)"
        } else if (value >= 60 && value < 80) {
            " (NORMAL)"
        } else {
            " (HIGH)"
        }
        mSelfView.findViewById<TextView>(R.id.tv_relaxation_level).text = valueLevel
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_relaxation).setValue(value)
        mSelfView.findViewById<TextView>(R.id.tv_relaxation_value).text = "${value.toInt()}"
    }

    fun setStress(value: Float?) {
        if (value == null) {
            return
        }

        var valueLevel = if (value >= 0 && value < 1) {
            " (LOW)"
        } else if (value >= 1 && value < 3.5) {
            " (NORMAL)"
        } else {
            " (HIGH)"
        }
        mSelfView.findViewById<TextView>(R.id.tv_stress_level).text = valueLevel
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_stress).setValue(value)
        mSelfView.findViewById<TextView>(R.id.tv_stress_value).text = "${value}"
    }

    fun setMood(value: Float?) {
        if (value == null) {
            return
        }
        mSelfView.findViewById<EmotionIndicatorAppView>(R.id.eiv_mood).setValue(value)
        mSelfView.findViewById<TextView>(R.id.tv_mood_value).text = "${value}"
    }

    fun showSampleData() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.VISIBLE

        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_3).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_3).visibility = View.VISIBLE
        tv_sample.visibility = View.VISIBLE
        setAttention(78f)
        setRelaxation(80f)
        setStress(2f)
        setMood(30f)
    }

    fun showErrorMessage(error:String) {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).text = error
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).text = error

        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_3).visibility = View.GONE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_3).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_3).text = error
        setAttention(0f)
        setRelaxation(0f)
        setStress(0f)
        setMood(0f)
    }

    fun hideSampleData() {
        tv_sample.visibility = View.GONE
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.GONE

        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_3).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_3).visibility = View.GONE
    }

    fun showAttentionLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_1).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_1).visibility = View.GONE
    }

    fun showRelaxationLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_2).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_2).visibility = View.GONE
    }

    fun showPressureLoading() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading_3).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text_3).visibility = View.GONE
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.GONE
    }

    fun hideAttentionLoaidng() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_1).visibility = View.GONE
    }

    fun hideRelaxationLoaidng() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_2).visibility = View.GONE
    }

    fun hidePressureLoaidng() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover_3).visibility = View.GONE
    }

}