package cn.entertech.flowtimezh.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_LABEL_END_TIME
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_LABEL_START_TIME
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_ID
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_START_TIME
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.ExperimentTagDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import cn.entertech.flowtimezh.ui.adapter.LabelsAdapter
import cn.entertech.flowtimezh.ui.adapter.MeditationLabelsAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.synthetic.main.activity_experiment_label.*
import kotlinx.android.synthetic.main.activity_meditation_labels_record.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationLabelsRecordActivity : BaseActivity() {

    private var meditationId: Long = -1
    private var meditationStartTime: Long = -1
    private var labelEndTime: Long = -1
    private var labelStartTime: Long = -1
    var mData = ArrayList<MultiItemEntity>()
    private var adapter: MeditationLabelsAdapter? = null
    private var experimentTags: MutableList<ExperimentTagModel>? = null
    private var experimentTagDao: ExperimentTagDao? = null
    private var experimentDimDao: ExperimentDimDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_labels_record)
        initFullScreenDisplay()
        setStatusBarLight()
        initLabels()
        initView()
    }

    fun initView() {
        labelStartTime = intent.getLongExtra(EXTRA_LABEL_START_TIME, -1L)
        labelEndTime = intent.getLongExtra(EXTRA_LABEL_END_TIME, -1L)
        meditationStartTime = intent.getLongExtra(EXTRA_MEDITATION_START_TIME, -1L)
        meditationId = intent.getLongExtra(EXTRA_MEDITATION_ID, -1L)
        tv_title.visibility = View.VISIBLE
        tv_back.visibility = View.VISIBLE
        tv_title.text = "数据标签"
        ll_back.visibility = View.INVISIBLE
        var dimIds = ""
        var isAllTagSelected = false
        adapter =
            MeditationLabelsAdapter(mData!!, MeditationLabelsAdapter.OnDimClickListener {
                isAllTagSelected = true
                dimIds = ""
                for (tag in experimentTags!!) {
                    var dims = experimentDimDao!!.findDimByTagId(tag.id)
                    var isTagSelected = false
                    for (dim in dims) {
                        if (dim.isSelected) {
                            dimIds = "${dimIds},${dim.id}"
                            isTagSelected = true
                        }
                    }
                    isAllTagSelected = isAllTagSelected && isTagSelected
                }
                if (isAllTagSelected) {
                    btn_commit_label.setBackgroundResource(R.drawable.meditation_labels_commit_enable_bg)
                } else {
                    btn_commit_label.setBackgroundResource(R.drawable.meditation_labels_commit_disable_bg)
                }
            })
        label_list.adapter = adapter
        val manager = GridLayoutManager(this, 3)
        manager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.getItemViewType(position) == LabelsAdapter.TYPE_LEVEL_1) 1 else manager.spanCount
            }
        })
        label_list.layoutManager = manager
        adapter!!.expandAll()

//        adapter!!.onItemChildClickListener =
//            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
//
//            }

        btn_commit_label.setOnClickListener {
            if (isAllTagSelected) {
                var meditationLabelsDao = MeditationLabelsDao(this)
                var meditationLabelsModel = MeditationLabelsModel()
                meditationLabelsModel.id = System.currentTimeMillis()
                meditationLabelsModel.dimIds = dimIds.substring(1, dimIds.length)
                meditationLabelsModel.endTime = labelEndTime
                meditationLabelsModel.startTime = labelStartTime
                meditationLabelsModel.meditationId = meditationId
                meditationLabelsModel.meditationStartTime = meditationStartTime
                meditationLabelsDao.create(meditationLabelsModel)
                Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show()
                clearSelectInDB()
                startActivity(Intent(this, MeditationActivity::class.java))
                finish()
            }
        }

        btn_delete.setOnClickListener {
            showDeleteDialog()
        }
    }

    fun showDeleteDialog() {
        var dialog = AlertDialog.Builder(this)
            .setMessage(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogContent
                    )}'>删除记录</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogExit
                    )}'>确定</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
                clearSelectInDB()
                startActivity(Intent(this, MeditationTimeRecordActivity::class.java))
                finish()
            }
            .setNegativeButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogCancel
                    )}'>取消</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    private fun initLabels() {
        var experimentDao = ExperimentDao(Application.getInstance())
        var selectedExperiment = experimentDao.findExperimentBySelected()
        experimentTagDao = ExperimentTagDao(Application.getInstance())
        experimentDimDao = ExperimentDimDao(Application.getInstance())
        experimentTags = experimentTagDao?.findTagByExperimentId(selectedExperiment.id)
        for (tag in experimentTags!!) {
            var dims = experimentDimDao!!.findDimByTagId(tag.id)
            for (dim in dims) {
                dim.isSelected = false
            }
            tag.subItems = dims
            mData.add(tag)
        }
    }

    fun clearSelectInDB() {
        for (tag in experimentTags!!) {
            var dims = experimentDimDao!!.findDimByTagId(tag.id)
            for (dim in dims) {
                dim.isSelected = false
                experimentDimDao!!.create(dim)
            }
        }
    }

    override fun onBackPressed() {
    }
}
