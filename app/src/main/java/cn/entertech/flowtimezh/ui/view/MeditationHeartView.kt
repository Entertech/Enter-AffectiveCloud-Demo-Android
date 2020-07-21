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
import cn.entertech.uicomponentsdk.realtime.RealtimeHRVView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.view_heart_rate.view.*

class MeditationHeartView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var minHeart: Int = 0
    var maxHeart: Int = 0
    var isFirstLoad = true
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.view_heart_rate, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        initView()
        addView(mSelfView)
    }

    fun initView() {
        mSelfView.findViewById<ImageView>(R.id.iv_heart_real_time_info).setOnClickListener {
//            val intent =
//                Intent(context, WebActivity::class.java).putExtra(ExtraKey.WEB_TITLE, "Heart Rate")
//            intent.putExtra(
//                ExtraKey.EXTRA_URL,
//                SettingManager.getInstance().remoteConfigHRRealtimeInfo
//            )
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigHRRealtimeInfo)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun setHrv(hrv: Double?) {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).appendHrv(hrv)
    }

    fun setHeartValue(heartRate: Int?) {
        if (heartRate == null) {
            return
        }
        if (isFirstLoad) {
            if (heartRate == 0) {
                return
            }
            minHeart = heartRate
            maxHeart = heartRate
            isFirstLoad = false
        } else {
            if (heartRate > maxHeart) {
                maxHeart = heartRate
            }
            if (heartRate != 0 && heartRate < minHeart) {
                minHeart = heartRate
            }
        }
        mSelfView.findViewById<TextView>(R.id.tv_heart_rate).text = "$heartRate"
        mSelfView.findViewById<TextView>(R.id.tv_max_heart).text = "Max: $maxHeart"
        mSelfView.findViewById<TextView>(R.id.tv_min_heart).text = "Min: $minHeart"
    }

    fun showLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.GONE
    }

    fun hindLoadingCover() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.GONE
    }

    fun showSampleData() {
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.VISIBLE
        tv_sample.visibility = View.VISIBLE
        setHeartValue(78)
    }

    fun showErrorMessage(error: String) {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).showErrorMessage(error)
        mSelfView.findViewById<LottieAnimationView>(R.id.icon_loading).visibility = View.GONE
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).visibility = View.VISIBLE
        mSelfView.findViewById<TextView>(R.id.tv_disconnect_text).text = error
        setHeartValue(0)
    }

    fun hideSampleData() {
        mSelfView.findViewById<RelativeLayout>(R.id.rl_loading_cover).visibility = View.GONE
        tv_sample.visibility = View.GONE
    }

    fun showHRVLoadingCover() {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).showLoadingCover()
    }

    fun hideHRVLoadingCover() {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).hindLoadingCover()
    }

    fun showHRVSampleData() {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).showSampleData()
    }

    fun hideHRVSampleData() {
        mSelfView.findViewById<RealtimeHRVView>(R.id.hrv).hideSampleData()
    }
}