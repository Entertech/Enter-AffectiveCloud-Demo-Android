package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_ID
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.ui.adapter.MeditationLabelsListAdapter
import kotlinx.android.synthetic.main.activity_meditation_labels_commit.*

class MeditationLabelsCommitActivity : BaseActivity() {
    private var meditationId: Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_labels_commit)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    fun initView() {
        meditationId = intent.getLongExtra(EXTRA_MEDITATION_ID, -1L)
        var meditationLabelsDao = MeditationLabelsDao(this)
        var meditationLabels = meditationLabelsDao.findByMeditationId(meditationId)
        var adapter = MeditationLabelsListAdapter(meditationLabels)
        rv_list.adapter = adapter
        rv_list.layoutManager = LinearLayoutManager(this)
        btn_commit.setOnClickListener {
            startActivity(Intent(this, MeditationActivity::class.java))
            finish()
        }
    }
}
