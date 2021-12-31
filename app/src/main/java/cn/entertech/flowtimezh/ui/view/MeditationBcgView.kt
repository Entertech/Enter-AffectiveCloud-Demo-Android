package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import cn.entertech.flowtimezh.R
import kotlinx.android.synthetic.main.card_bcg_wave.view.*
import kotlinx.android.synthetic.main.card_heart_rate.view.*
import kotlinx.android.synthetic.main.card_heart_rate.view.heart_rate

class MeditationBcgView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.card_bcg_wave, null)

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

    fun setRw(rw:List<Double>){
        rw_wave.appendHrv(rw)
    }

    fun setBcg(bcg:List<Double>) {
        bcg_wave.appendHrv(bcg)
    }

    fun showLoadingCover() {
        bcg_wave.showLoadingCover()
        rw_wave.showLoadingCover()
    }

    fun hideLoadingCover() {
        bcg_wave.hindLoadingCover()
        rw_wave.hindLoadingCover()
    }

    fun showSampleData() {
        bcg_wave.showSampleData()
        rw_wave.showSampleData()
    }

    fun showErrorMessage(error: String) {
        bcg_wave.showErrorMessage(error)
        rw_wave.showErrorMessage(error)
    }

    fun hideSampleData() {
        bcg_wave.hideSampleData()
        rw_wave.hideSampleData()
    }
}