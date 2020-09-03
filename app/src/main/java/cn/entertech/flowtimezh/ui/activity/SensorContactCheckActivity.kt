package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import kotlinx.android.synthetic.main.activity_sensor_contact_check.*

class SensorContactCheckActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_contact_check)
        initFullScreenDisplay()
        if (BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
            BiomoduleBleManager.getInstance(Application.getInstance())
                .startHeartAndBrainCollection()
        }
        initView()
    }
//
//    fun toMeditaitionActivity() {
//        when (meditationType) {
//            MEDITATION_TYPE_GUIDE -> {
//                var intent = Intent(
//                    this@SensorContactCheckActivity,
//                    MeditationActivity::class.java
//                )
//                intent.putExtra(LESSON_FILE_URL, getIntent().getStringExtra(LESSON_FILE_URL))
//                intent.putExtra(LESSON_ID, getIntent().getIntExtra(LESSON_ID, -1))
//                intent.putExtra(LESSON_NAME, getIntent().getStringExtra(LESSON_NAME))
//                intent.putExtra(COURSE_ID, getIntent().getIntExtra(COURSE_ID, -1))
//                intent.putExtra(COURSE_NAME, getIntent().getStringExtra(COURSE_NAME))
//                startActivity(intent)
//                finish()
//            }
//            MEDITATION_TYPE_UNGUIDE -> {
//                var intent = Intent(
//                    this@SensorContactCheckActivity,
//                    MeditationWithoutGuideActivity::class.java
//                )
//                intent.putExtra(LESSON_FILE_URL, getIntent().getStringExtra(LESSON_FILE_URL))
//                intent.putExtra(LESSON_ID, getIntent().getIntExtra(LESSON_ID, -1))
//                intent.putExtra(LESSON_NAME, getIntent().getStringExtra(LESSON_NAME))
//                intent.putExtra(COURSE_ID, getIntent().getIntExtra(COURSE_ID, -1))
//                intent.putExtra(COURSE_NAME, getIntent().getStringExtra(COURSE_NAME))
//                startActivity(intent)
//                finish()
//            }
//        }
//    }
    var runnable = Runnable {
//        toMeditaitionActivity()
    }
    var mMainHandler = Handler(Looper.getMainLooper())
    var isShowWellDone = false
    fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_skip.visibility = View.GONE
//        tv_skip.setOnClickListener {
//            toMeditaitionActivity()
//        }
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
