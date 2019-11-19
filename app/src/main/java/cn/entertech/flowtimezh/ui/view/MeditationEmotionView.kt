package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.flowtimezh.R
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
        initView()
        addView(mSelfView)
    }

    fun initView() {
//        var attentionScale = arrayOf(0, 60, 80, 100)
//        var relaxationScale = arrayOf(0, 60, 80, 100)
//        var stressScale = arrayOf(0, 1, 2, 3, 4, 5)
//        var moodScale = arrayOf(-2, -1, 0, 1, 2)
//        var attentionIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
//        var relaxationIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
//        var stressIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
//        var moodIndicatorItems = arrayListOf<EmotionIndicatorView.IndicateItem>()
//        attentionIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.6f, Color.parseColor("#3352a27c")))
//        attentionIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#8052a27c")))
//        attentionIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#52a27c")))
//        relaxationIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.6f, Color.parseColor("#335e75fe")))
//        relaxationIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#805e75fe")))
//        relaxationIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#5e75fe")))
//        stressIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.2f, Color.parseColor("#33cc5268")))
//        stressIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.5f, Color.parseColor("#80cc5268")))
//        stressIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.3f, Color.parseColor("#cc5268")))
//        moodIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.5f, Color.parseColor("#7f725e")))
//        moodIndicatorItems.add(EmotionIndicatorView.IndicateItem(0.5f, Color.parseColor("#ffc56f")))
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_attention).setScales(attentionScale)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_attention).setIndicatorItems(attentionIndicatorItems)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_relaxation).setScales(relaxationScale)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_relaxation).setIndicatorItems(relaxationIndicatorItems)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_stress).setScales(stressScale)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_stress).setIndicatorItems(stressIndicatorItems)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_mood).setScales(moodScale)
//        mSelfView.findViewById<EmotionIndicatorView>(R.id.eiv_mood).setIndicatorItems(moodIndicatorItems)

    }

    fun setAttention(value: Float?) {
        if (value == null) {
            return
        }
        realtime_attention_view.setAttention(value)
    }

    fun setRelaxation(value: Float?) {
        if (value == null) {
            return
        }
        realtime_relaxation_view.setRelaxation(value)
    }

    fun setStress(value: Float?) {
        if (value == null) {
            return
        }
        realtime_pressure_view.setPressure(value)
    }


    fun setArousal(value: Float?) {
        realtime_arousal_view.setArousal(value)
    }

    fun showSampleData() {
        realtime_pressure_view.showDisconnectTip()
        realtime_relaxation_view.showDisconnectTip()
        realtime_attention_view.showDisconnectTip()
        realtime_arousal_view.showDisconnectTip()
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

    fun hindLoadingCover() {
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
}