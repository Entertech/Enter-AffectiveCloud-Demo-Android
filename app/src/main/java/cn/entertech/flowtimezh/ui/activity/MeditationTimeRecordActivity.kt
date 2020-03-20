package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_LABEL_END_TIME
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_LABEL_START_TIME
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_ID
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_START_TIME
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.utils.MeditationTimeManager
import kotlinx.android.synthetic.main.activity_meditation_time_record.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationTimeRecordActivity : BaseActivity() {

    private var endTime: Long? = null
    private var startTime: Long? = null
    var canExit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_time_record)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    fun initView() {
        tv_back.visibility = View.VISIBLE
        ll_back.visibility = View.VISIBLE
        ll_back.setOnClickListener { finish() }
        var meditationId = intent.getLongExtra(EXTRA_MEDITATION_ID, -1)
        var meditationStartTime = intent.getLongExtra(EXTRA_MEDITATION_START_TIME, -1)
        if (meditationId == -1L || meditationStartTime == -1L) {
            finish()
            Toast.makeText(this, "请先开始有效的体验", Toast.LENGTH_LONG).show()
        }
        tv_record_btn.setOnClickListener {
            if (tv_record_btn.text == "开始记录") {
                canExit = false
                ll_back.visibility = View.GONE
                chronometer.visibility = View.VISIBLE
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                startTime = MeditationTimeManager.getInstance().currentTimeMs()
                tv_record_btn.setBackgroundResource(R.drawable.shape_time_record_end_bg)
                tv_record_btn.text = "结束记录"
            } else {
                endTime = MeditationTimeManager.getInstance().currentTimeMs()
                chronometer.stop()
                var intent = Intent(this, MeditationLabelsRecordActivity::class.java)
                intent.putExtra(EXTRA_LABEL_START_TIME, startTime)
                intent.putExtra(EXTRA_LABEL_END_TIME, endTime!!)
                intent.putExtra(
                    EXTRA_MEDITATION_ID,
                    meditationId
                )
                intent.putExtra(
                    EXTRA_MEDITATION_START_TIME,
                    meditationStartTime
                )

                startActivity(intent)
            }
        }

        var experimentDao = ExperimentDao(this)
        var experimentName = experimentDao.findExperimentBySelected().nameCn
        tv_experiment_name.text = experimentName
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        resetPage()
    }

    private fun resetPage() {
        canExit = true
        ll_back.visibility = View.VISIBLE
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
//        chronometer.visibility = View.INVISIBLE
        tv_record_btn.setBackgroundResource(R.drawable.shape_time_record_start_bg)
        tv_record_btn.text = "开始记录"
    }

    override fun onBackPressed() {
        if (canExit) {
            finish()
        }
    }

}
