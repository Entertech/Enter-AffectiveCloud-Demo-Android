package cn.entertech.flowtimezh.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.ExperimentTagDao
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import cn.entertech.flowtimezh.ui.adapter.LabelsAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.synthetic.main.activity_experiment_label.*
import kotlinx.android.synthetic.main.layout_common_title.*

class ExperimentLabelActivity : BaseActivity() {
    private var adapter: LabelsAdapter? = null
    private var experimentTags: MutableList<ExperimentTagModel>? = null
    private var experimentTagDao: ExperimentTagDao? = null
    private var experimentDimDao: ExperimentDimDao? = null

    var mData = ArrayList<MultiItemEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiment_label)
        initFullScreenDisplay()
        setStatusBarLight()
        initLabels()
        initView()
    }

    fun initView(){
        tv_title.visibility = View.VISIBLE
        tv_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.label_category)
        ll_back.setOnClickListener {
            finish()
        }
        adapter = LabelsAdapter(mData!!)
        rv_tags_list.adapter = adapter
        rv_tags_list.layoutManager =  LinearLayoutManager(this)
        adapter!!.expandAll()
    }


    fun initLabels() {
        var experimentId = intent.getIntExtra(Constant.EXTRA_EXPERIMENT_ID, -1)
        experimentTagDao = ExperimentTagDao(Application.getInstance())
        experimentDimDao = ExperimentDimDao(Application.getInstance())
        experimentTags = experimentTagDao?.findTagByExperimentId(experimentId)
        for (tag in experimentTags!!){
            var dims = experimentDimDao!!.findDimByTagId(tag.id)
            tag.subItems = dims
            mData.add(tag as MultiItemEntity)
        }
    }
}
