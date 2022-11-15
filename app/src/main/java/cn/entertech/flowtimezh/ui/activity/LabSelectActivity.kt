package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.ExperimentModeDao
import cn.entertech.flowtimezh.database.ExperimentTagDao
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.database.model.ExperimentModeModel
import cn.entertech.flowtimezh.database.model.ExperimentModel
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import cn.entertech.flowtimezh.entity.LabelsEntity
import cn.entertech.flowtimezh.server.presenter.ExperimentLabelsPresenter
import cn.entertech.flowtimezh.server.view.ExperimentLabelsView
import cn.entertech.flowtimezh.ui.adapter.ExperimentListAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_lab_select.*

class LabSelectActivity : BaseActivity() {
    private var experimentDao: ExperimentDao? = null
    private var adapter: ExperimentListAdapter? = null
    private var experiments = listOf<ExperimentModel>()
    private var experimentLabelPresenter: ExperimentLabelsPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_select)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
        initExperimentLabelPresenter()
    }

    var experimentLabelSView = object : ExperimentLabelsView {
        override fun onSuccess(labelEntity: List<LabelsEntity>?) {
            if (labelEntity == null) {
                return
            }
            var experimentDao = ExperimentDao(Application.getInstance())
            var experimentTagDao = ExperimentTagDao(Application.getInstance())
            var experimentModeDao = ExperimentModeDao(Application.getInstance())
            var experimentDimDao = ExperimentDimDao(Application.getInstance())
            var selectedExperiment = experimentDao.findExperimentBySelected()
            for (i in labelEntity.indices) {
                var experimentModel = ExperimentModel()
                if (selectedExperiment == null) {
                    experimentModel.isSelected = i == 0
                } else {
                    experimentModel.isSelected = labelEntity[i].id == selectedExperiment.id
                }
                experimentModel.app = labelEntity[i].app
                experimentModel.createTime = labelEntity[i].gmt_create
                experimentModel.modifyTime = labelEntity[i].gmt_modify
                experimentModel.desc = labelEntity[i].desc
                experimentModel.id = labelEntity[i].id
                experimentModel.nameCn = labelEntity[i].name_cn
                experimentModel.nameEn = labelEntity[i].name_en
                experimentModel.appKey = SettingManager.getInstance().appKey
                experimentDao.create(experimentModel)

                for (mode in labelEntity[i].mode) {
                    var modeModel = ExperimentModeModel()
                    modeModel.createTime = mode.gmt_create
                    modeModel.modifyTime = mode.gmt_modify
                    modeModel.desc = mode.desc
                    modeModel.id = mode.id
                    modeModel.nameCn = mode.name_cn
                    modeModel.nameEn = mode.name_en
                    modeModel.expermentId = labelEntity[i].id
                    experimentModeDao.create(modeModel)
                }

                for (tag in labelEntity[i].tag) {
                    var tagModel = ExperimentTagModel()
                    tagModel.createTime = tag.gmt_create
                    tagModel.modifyTime = tag.gmt_modify
                    tagModel.desc = tag.desc
                    tagModel.id = tag.id
                    tagModel.nameCn = tag.name_cn
                    tagModel.nameEn = tag.name_en
                    tagModel.expermentId = labelEntity[i].id
                    experimentTagDao.create(tagModel)

                    for (dim in tag.dim) {
                        var dimModel = ExperimentDimModel()
                        dimModel.createTime = dim.gmt_create
                        dimModel.modifyTime = dim.gmt_modify
                        dimModel.desc = dim.desc
                        dimModel.id = dim.id
                        dimModel.value = dim.value
                        dimModel.nameCn = dim.name_cn
                        dimModel.nameEn = dim.name_en
                        dimModel.tagId = tag.id
                        experimentDimDao.create(dimModel)
                    }
                }
            }
            experiments = experimentDao.listAll()
            adapter?.setNewData(experiments)
        }

        override fun onError(error: String) {
            Toast.makeText(this@LabSelectActivity, error, Toast.LENGTH_SHORT).show()
        }

    }

    fun initExperimentLabelPresenter() {
        experimentLabelPresenter = ExperimentLabelsPresenter(this)
        experimentLabelPresenter?.onCreate()
        experimentLabelPresenter?.attachView(experimentLabelSView)
        experimentLabelPresenter?.getExperimentLabels()
    }
    fun initView(){
        initList()
    }

    fun initList() {
        experimentDao = ExperimentDao(Application.getInstance())
        adapter = ExperimentListAdapter(experiments)
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        var selctedExperiment = experimentDao!!.findExperimentBySelected()
        if (selctedExperiment == null && experiments.isNotEmpty()) {
            for (i in experiments.indices) {
                experiments[0].isSelected = i == 0
                experimentDao?.create(experiments[i])
            }
            adapter!!.notifyDataSetChanged()
        }
        rv_list.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                for (i in experiments.indices) {
                    experiments[i].isSelected = false
                    experimentDao?.create(experiments[i])
                }
                experiments[position].isSelected = true
                experimentDao?.create(experiments[position])
                adapter!!.notifyDataSetChanged()
            }
        })

        adapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                startActivity(
                    Intent(
                        this@LabSelectActivity,
                        ExperimentLabelActivity::class.java
                    ).putExtra(Constant.EXTRA_EXPERIMENT_ID, experiments!![position].id)
                )
            }

    }

    override fun onDestroy() {
        experimentLabelPresenter?.onStop()
        super.onDestroy()
    }
}