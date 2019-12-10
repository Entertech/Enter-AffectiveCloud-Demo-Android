package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.ExperimentModeDao
import cn.entertech.flowtimezh.database.ExperimentTagDao
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.database.model.ExperimentModeModel
import cn.entertech.flowtimezh.database.model.ExperimentModel
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import cn.entertech.flowtimezh.entity.LabelsEntity
import cn.entertech.flowtimezh.entity.TabEntity
import cn.entertech.flowtimezh.server.presenter.ExperimentLabelsPresenter
import cn.entertech.flowtimezh.server.view.ExperimentLabelsView
import cn.entertech.flowtimezh.ui.fragment.*
import com.flyco.tablayout.listener.CustomTabEntity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var experimentLabelPresenter: ExperimentLabelsPresenter? = null
    var mTabEntitys = ArrayList<CustomTabEntity>()
    private lateinit var homeFragment: HomeFragment
    private lateinit var journeyFragment: JourneyFragment
    private lateinit var meFragment: MeFragment
    var mFragments = ArrayList<androidx.fragment.app.Fragment>()
    var mIconSelected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_selected,
        R.mipmap.ic_tab_bar_lib_selected,
        R.mipmap.ic_tab_bar_me_selected
    )
    var mIconUnselected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_unselected,
        R.mipmap.ic_tab_bar_lib_unselected,
        R.mipmap.ic_tab_bar_me_unselected
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            for (experiment in labelEntity) {
                var experimentModel = ExperimentModel()
                experimentModel.isSelected =
                    selectedExperiment != null && experiment.id == selectedExperiment.id
                experimentModel.app = experiment.app
                experimentModel.createTime = experiment.gmt_create
                experimentModel.modifyTime = experiment.gmt_modify
                experimentModel.desc = experiment.desc
                experimentModel.id = experiment.id
                experimentModel.nameCn = experiment.name_cn
                experimentModel.nameEn = experiment.name_en
                experimentDao.create(experimentModel)

                for (mode in experiment.mode) {
                    var modeModel = ExperimentModeModel()
                    modeModel.createTime = mode.gmt_create
                    modeModel.modifyTime = mode.gmt_modify
                    modeModel.desc = mode.desc
                    modeModel.id = mode.id
                    modeModel.nameCn = mode.name_cn
                    modeModel.nameEn = mode.name_en
                    modeModel.expermentId = experiment.id
                    experimentModeDao.create(modeModel)
                }

                for (tag in experiment.tag) {
                    var tagModel = ExperimentTagModel()
                    tagModel.createTime = tag.gmt_create
                    tagModel.modifyTime = tag.gmt_modify
                    tagModel.desc = tag.desc
                    tagModel.id = tag.id
                    tagModel.nameCn = tag.name_cn
                    tagModel.nameEn = tag.name_en
                    tagModel.expermentId = experiment.id
                    experimentTagDao.create(tagModel)

                    for (dim in tag.dim) {
                        var dimModel = ExperimentDimModel()
                        dimModel.createTime = dim.gmt_create
                        dimModel.modifyTime = dim.gmt_modify
                        dimModel.desc = dim.desc
                        dimModel.id = dim.id
                        dimModel.nameCn = dim.name_cn
                        dimModel.nameEn = dim.name_en
                        dimModel.tagId = tag.id
                        experimentDimDao.create(dimModel)
                    }
                }
            }
        }

        override fun onError(error: String) {
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
        }

    }

    fun initExperimentLabelPresenter() {
        experimentLabelPresenter = ExperimentLabelsPresenter(Application.getInstance())
        experimentLabelPresenter?.onCreate()
        experimentLabelPresenter?.attachView(experimentLabelSView)
        experimentLabelPresenter?.getExperimentLabels()
    }

    fun initView() {
        var mTitles = arrayOf(
            getString(R.string.home), getString(R.string.statistics), getString(
                R.string.me
            )
        )
        homeFragment = HomeFragment()
        journeyFragment = JourneyFragment()
        meFragment = MeFragment()
        mFragments.add(homeFragment)
        mFragments.add(journeyFragment)
        mFragments.add(meFragment)
        for (i in 0..2) {
            mTabEntitys.add(TabEntity(mTitles[i], mIconSelected[i], mIconUnselected[i]))
        }
        ctl_main.setTabData(mTabEntitys, this, R.id.fl_container, mFragments)
    }

    override fun onDestroy() {
        experimentLabelPresenter?.onStop()
        super.onDestroy()
    }
}
