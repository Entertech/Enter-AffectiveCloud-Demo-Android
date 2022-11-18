package cn.entertech.flowtimezh.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_LABEL_ID
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.ExperimentTagDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import cn.entertech.flowtimezh.ui.adapter.MeditationTagSelectListAdapter
import cn.entertech.flowtimezh.utils.ToastUtil
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.synthetic.main.activity_meditation_labels_record.*
import kotlinx.android.synthetic.main.layout_common_title.*

class MeditationLabelsRecordActivity : BaseActivity() {

    private var labelId: Long = -1
    private var meditationLabelsModel:MeditationLabelsModel? = null
    private var meditationLabelsDao: MeditationLabelsDao? = null
    private var meditationId: Long = -1
    private var meditationStartTime: Long = -1
    private var labelEndTime: Long = -1
    private var labelStartTime: Long = -1
    var mData = ArrayList<MultiItemEntity>()
    private var adapter: MeditationTagSelectListAdapter? = null
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
//        labelStartTime = intent.getLongExtra(EXTRA_LABEL_START_TIME, -1L)
//        labelEndTime = intent.getLongExtra(EXTRA_LABEL_END_TIME, -1L)
//        meditationStartTime = intent.getLongExtra(EXTRA_MEDITATION_START_TIME, -1L)
        labelId = intent.getLongExtra(EXTRA_LABEL_ID, -1L)
        meditationLabelsDao = MeditationLabelsDao(Application.getInstance())
        meditationLabelsModel = meditationLabelsDao!!.findMeditationLabelById(labelId)
        if (meditationLabelsModel == null){
            ToastUtil.toastShort(this@MeditationLabelsRecordActivity,"标签记录异常")
            finish()
        }

        tv_title.visibility = View.VISIBLE
        tv_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.label_category)
        ll_back.visibility = View.INVISIBLE

        var dimIds = initDimSelect()
        var isAllTagSelected = isAllTagSelected()
        initCommitBtn(isAllTagSelected)
        initNote()
        adapter =
            MeditationTagSelectListAdapter(experimentTags!!,fun(){
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
                initCommitBtn(isAllTagSelected)
            })
        label_list.adapter = adapter
        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        label_list.layoutManager = linearLayoutManager
        btn_commit_label.setOnClickListener {
            if (isAllTagSelected) {
                var meditationLabelsDao = MeditationLabelsDao(this)
                meditationLabelsModel!!.dimIds = dimIds.substring(1, dimIds.length)
                meditationLabelsModel!!.note = et_note?.text?.toString()
                meditationLabelsDao.create(meditationLabelsModel)
                clearSelectInDB()
                startActivity(Intent(this, MeditationActivity::class.java))
                finish()
            }
        }

        btn_delete.setOnClickListener {
            showDeleteDialog()
        }
    }

    fun isAllTagSelected():Boolean{
        var isAllTagSelected = true
        if (experimentTags != null){
            for (tag in experimentTags!!) {
                var dims = experimentDimDao!!.findDimByTagId(tag.id)
                var isTagSelected = false
                for (dim in dims) {
                    if (dim.isSelected) {
                        isTagSelected = true
                    }
                }
                isAllTagSelected = isAllTagSelected && isTagSelected
            }
        }
        return isAllTagSelected
    }

    fun initCommitBtn(isAllTagSelected:Boolean){
        if (isAllTagSelected) {
            btn_commit_label.setBackgroundResource(R.drawable.meditation_labels_commit_enable_bg)
        } else {
            btn_commit_label.setBackgroundResource(R.drawable.meditation_labels_commit_disable_bg)
        }
    }

    fun initDimSelect():String{
        val dimSelected = meditationLabelsModel!!.dimIds
        if (dimSelected.isNullOrEmpty()){
            return ""
        }
        val dimSelectedArray = dimSelected.split(",")
        for (tag in experimentTags!!) {
            var dims = experimentDimDao!!.findDimByTagId(tag.id)
            for (dim in dims){
                dim.isSelected = false
                experimentDimDao?.create(dim)
            }
            for (dim in dims){
                if (dimSelectedArray.contains("${dim.id}")){
                    dim.isSelected = true
                    experimentDimDao?.create(dim)
                }
            }
        }
        return ",${dimSelected}"
    }

    fun initNote(){
        val note = meditationLabelsModel!!.note
        et_note.setText(note)
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
                deleteLabel()
                startActivity(Intent(this, MeditationActivity::class.java))
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

    fun deleteLabel(){
        if (meditationLabelsDao != null && meditationLabelsModel != null){
            meditationLabelsDao!!.deleteLabel(meditationLabelsModel)
        }
    }
}
