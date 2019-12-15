package cn.entertech.flowtimezh.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
        initPermission()
    }



    /**
     * Android6.0 auth
     */
    fun initPermission() {
        val needPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val needRequestPermissions = java.util.ArrayList<String>()
        for (i in needPermission.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    needPermission[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissions.add(needPermission[i])
            }
        }
        if (needRequestPermissions.size != 0) {
            val permissions = arrayOfNulls<String>(needRequestPermissions.size)
            for (i in needRequestPermissions.indices) {
                permissions[i] = needRequestPermissions[i]
            }
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        }
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
        }

        override fun onError(error: String) {
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
        }

    }

    fun initExperimentLabelPresenter() {
        experimentLabelPresenter = ExperimentLabelsPresenter(this)
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
