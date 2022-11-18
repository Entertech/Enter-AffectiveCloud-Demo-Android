package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_EXPERIMENT_ID
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.model.ExperimentModel
import cn.entertech.flowtimezh.ui.adapter.ExperimentListAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_experiment_choose.*
import kotlinx.android.synthetic.main.layout_common_title.*

class ExperimentChooseActivity : BaseActivity() {

    private var experimentDao: ExperimentDao? = null
    private var adapter: ExperimentListAdapter? = null
    private var experiments: List<ExperimentModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiment_choose)
        initFullScreenDisplay()
        setStatusBarLight()
        initExperimentData()
        initView()
    }

    fun initExperimentData() {
        experimentDao = ExperimentDao(Application.getInstance())
        experiments = experimentDao?.listAll()
    }

    fun initView() {
        tv_title.visibility = View.VISIBLE
        tv_back.visibility = View.GONE
        tv_title.text = "实验选择"
        iv_back.setOnClickListener {
            finish()
        }
        if (experiments == null) {
            return
        }
        adapter = ExperimentListAdapter(experiments!!)
        rv_list.layoutManager = LinearLayoutManager(this)
        rv_list.adapter = adapter
        var selctedExperiment = experimentDao!!.findExperimentBySelected()
        if (selctedExperiment == null && experiments!!.isNotEmpty()) {
            for (i in experiments!!.indices) {
                experiments!![0].isSelected = i == 0
                experimentDao?.create(experiments!![i])
            }
            adapter!!.notifyDataSetChanged()
        }
        rv_list.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                for (i in experiments!!.indices) {
                    experiments!![i].isSelected = false
                    experimentDao?.create(experiments!![i])
                }
                experiments!![position].isSelected = true
                experimentDao?.create(experiments!![position])
                adapter!!.notifyDataSetChanged()
            }
        })

        adapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
                startActivity(
                    Intent(
                        this@ExperimentChooseActivity,
                        ExperimentLabelActivity::class.java
                    ).putExtra(EXTRA_EXPERIMENT_ID, experiments!![position].id)
                )
            }
    }
}
