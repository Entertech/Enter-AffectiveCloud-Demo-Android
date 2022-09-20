package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.flowtimezh.R
import kotlinx.android.synthetic.main.card_heart_rate.view.*

class MeditationHeartView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var minHeart: Int = 0
    var maxHeart: Int = 0
    var isFirstLoad = true
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.card_heart_rate, null)

    init {
        var layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()
    }

    fun initView() {
//        heart_rate.setIsShowInfoIcon(true,url = SettingManager.getInstance().remoteConfigHRRealtimeInfo)
//        realtime_hrv.setIsShowInfoIcon(true,url= SettingManager.getInstance().remoteConfigHRVRealtimeInfo )
    }
    var preSmoothValue = 0f
    var beta = 0.8f
    fun smoothValue(newData:Int):Float{
        val curValue =  (1f-beta) * newData + beta*preSmoothValue
        return curValue
    }
    fun setHeartValue(heartRate: Int?) {
        if (heartRate != null) {
            if (heartRate != 0 && preSmoothValue != 0F){
                var smoothValue = smoothValue(heartRate)
                heart_rate.setHeartValue(smoothValue.toInt())
            }else{
                heart_rate.setHeartValue(heartRate)
            }
            preSmoothValue = heartRate.toFloat()
        }
    }

    fun setRealtimeHr(hr: Double?) {
        if (hr != null){
            heart_rate_line.appendHrv(listOf(hr.toDouble()))
        }
//        realtime_hrv.appendHrv(hrv)
    }

    fun showHRLoadingCover() {
        heart_rate.showLoading()
        heart_rate_line.showLoadingCover()
    }

    fun hideHRLoadingCover() {
        heart_rate.hideLoading()
        heart_rate_line.hindLoadingCover()
    }

    fun showHRSampleData() {
        heart_rate.showDisconnectTip()
        heart_rate_line.showSampleData()
    }

    fun showErrorMessage(error: String) {
        heart_rate.showErrorMessage(error)
        heart_rate_line.showErrorMessage(error)
//        realtime_hrv.showErrorMessage(error)
    }

    fun hideHRSampleData() {
        heart_rate.hideDisconnectTip()
        heart_rate_line.hideSampleData()
    }

    fun showHRVLoadingCover() {
//        realtime_hrv.showLoadingCover()
    }

    fun hideHRVLoadingCover() {
//        realtime_hrv.hindLoadingCover()
    }

    fun showHRVSampleData() {
//        realtime_hrv.showSampleData()
    }

    fun hideHRVSampleData() {
//        realtime_hrv.hideSampleData()
    }
}