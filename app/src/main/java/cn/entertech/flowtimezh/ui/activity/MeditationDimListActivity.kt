package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.ui.adapter.MeditationDimListAdapter
import kotlinx.android.synthetic.main.activity_meditation_dim_list.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationDimListActivity : BaseActivity() {
    var mData = ArrayList<ExperimentDimModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_dim_list)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    fun initTitle() {
        tv_back.visibility = View.VISIBLE
        tv_title.visibility = View.VISIBLE
        var duration = intent.getStringExtra("duration")
        tv_title.text = duration
        ll_back.visibility = View.VISIBLE
        ll_back.setOnClickListener {
            finish()
        }
    }

    fun initView() {
        initTitle()
        var experimentDimDao = ExperimentDimDao(this)
        var dimIds = intent.getStringExtra("dimIds")
        var dimIdsString = dimIds.split(",")
        for (dimId in dimIdsString) {
            var dimId = Integer.parseInt(dimId)
            var dim = experimentDimDao.findByDimId(dimId)
            mData.add(dim)
        }

        var adapter = MeditationDimListAdapter(mData)
        rv_list.adapter = adapter
        rv_list.layoutManager = LinearLayoutManager(this)
    }
}
