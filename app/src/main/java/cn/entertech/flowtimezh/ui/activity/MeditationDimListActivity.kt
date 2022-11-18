package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.ui.adapter.MeditationDimListAdapter
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.qqtheme.framework.picker.OptionPicker
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_meditation_dim_list.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationDimListActivity : BaseActivity() {
    private var meditationLabelId: Long = -1L
    private lateinit var adapter: MeditationDimListAdapter
    private lateinit var experimentDimDao: ExperimentDimDao
    var mData = ArrayList<ExperimentDimModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_dim_list)
        initFullScreenDisplay()
        setStatusBarLight()
        meditationLabelId = intent.getLongExtra("meditationLabelId", -1L)
        initView()
    }

    fun initTitle() {
        tv_back.visibility = View.GONE
        tv_title.visibility = View.VISIBLE
        var duration = intent.getStringExtra("duration")
        tv_title.text = duration
        ll_back.visibility = View.VISIBLE
        iv_back.setOnClickListener {
            finish()
        }
    }

    fun initView() {
        initTitle()
        initNote()
        experimentDimDao = ExperimentDimDao(this)
        var dimIds = intent.getStringExtra("dimIds")
        var dimIdsString = dimIds!!.split(",")
        for (dimId in dimIdsString) {
            var dimId = Integer.parseInt(dimId)
            var dim = experimentDimDao.findByDimId(dimId)
            mData.add(dim)
        }

        adapter = MeditationDimListAdapter(mData)
        rv_list.adapter = adapter
        rv_list.layoutManager = LinearLayoutManager(this)
        adapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                if (!intent.getBooleanExtra("isFromReport", false)) {
                    showPicker(position)
                }
            }

    }

    fun initNote(){
        val note = intent.getStringExtra("note")
        if (note.isNullOrEmpty()){
            tv_note.text = "--"
        }else{
            tv_note.text = note
        }
    }

    fun showPicker(position: Int) {
        var dimModel = mData.get(position)
        var dims = experimentDimDao.findDimByTagId(dimModel.tagId)
        val strings = dims.map { it.nameCn }
        val picker = OptionPicker(this@MeditationDimListActivity, strings)
        picker.setOnOptionPickListener(object : OptionPicker.OnOptionPickListener() {
            override fun onOptionPicked(pickPosition: Int, option: String) {
                var selectedDim = dims[pickPosition]
                var dimIdsString = ""
                for (i in mData.indices) {
                    if (i == position) {
                        mData[i] = selectedDim
                    }
                    dimIdsString = "${dimIdsString},${mData[i].id}"
                }
                adapter.notifyDataSetChanged()
                var meditationLabelsDao = MeditationLabelsDao(this@MeditationDimListActivity)
                var meditationLabel = meditationLabelsDao.findMeditationLabelById(meditationLabelId)
                meditationLabel.dimIds = dimIdsString.substring(1, dimIdsString.length)
                meditationLabelsDao.create(meditationLabel)
            }
        })
        picker.show()
    }
}
