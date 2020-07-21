package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.IS_SHOW_SKIP
import cn.entertech.flowtimezh.app.Constant.Companion.MEDITATION_TYPE
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.ble.single.BiomoduleBleManager
import kotlinx.android.synthetic.main.activity_sensor_contact_check.*

class SensorContactCheckActivity : BaseActivity() {

    private var isShowSkip: Boolean = true
    private var bleManager: BiomoduleBleManager? = null
    private var meditationType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_contact_check)
        initFullScreenDisplay()
        setStatusBarLight()
        meditationType = intent.getStringExtra(MEDITATION_TYPE)
        isShowSkip = intent.getBooleanExtra(IS_SHOW_SKIP, true)
        bleManager = BiomoduleBleManager.getInstance(Application.getInstance())
        if (bleManager!!.isConnected()) {
            bleManager!!.startHeartAndBrainCollection()
        } else {
            if (meditationType != null) {
                toMeditaitionActivity()
            }
        }
        initView()
    }

    fun toMeditaitionActivity() {
        if (meditationType != null) {
            var intent = Intent(
                this@SensorContactCheckActivity,
                MeditationWithoutGuideActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    var runnable = Runnable {
        toMeditaitionActivity()
    }
    var mMainHandler = Handler(Looper.getMainLooper())
    var isShowWellDone = false
    fun initView() {
        if (!isShowSkip) {
            tv_skip.visibility = View.GONE
        } else {
            tv_skip.visibility = View.VISIBLE
        }
        tv_skip.setOnClickListener {
            LogManager.getInstance().logPost("Button ${currentActivity} SKIP")
            toMeditaitionActivity()
        }
        device_contact_indicator.addContactListener(fun() {
            if (!isShowWellDone) {
                isShowWellDone = true
//                showWellDoneTip()
                toMeditationPageWithDelay()
            }
        }, fun() {
            if (isShowWellDone) {
                isShowWellDone = false
                cancelToMeditationPage()
//                showContactIndicator()
            }
        })
    }

    fun toMeditationPageWithDelay() {
        mMainHandler.postDelayed(runnable, 1500)
    }

    fun cancelToMeditationPage() {
        mMainHandler.removeCallbacks(runnable)
    }

    fun showWellDoneTip() {
        ll_well_done_tip.visibility = View.VISIBLE
        device_contact_indicator.visibility = View.GONE
    }

    fun showContactIndicator() {
        ll_well_done_tip.visibility = View.GONE
        device_contact_indicator.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        cancelToMeditationPage()
        device_contact_indicator.release()
        super.onDestroy()
    }
}
