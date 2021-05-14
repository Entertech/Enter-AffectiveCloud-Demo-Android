package cn.entertech.flowtimezh.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.model.ExperimentTagModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class MeditationTagSelectListAdapter(data: List<ExperimentTagModel>,var onDimClickListener:()->Unit) :
    BaseQuickAdapter<ExperimentTagModel, BaseViewHolder>(
        R.layout.item_meditation_label_list_parent,
        data
    ) {

    var experimentDimDao = ExperimentDimDao(Application.getInstance())
    override fun convert(helper: BaseViewHolder, item: ExperimentTagModel) {
        helper.setText(R.id.tv_meditation_label, item.nameCn)
        var dims = experimentDimDao!!.findDimByTagId(item.id)
        var meditationDimSelectListAdapter = MeditationDimSelectListAdapter(dims)
        var list = helper.getView<RecyclerView>(R.id.rv_dim_select_list)
        list.addOnItemTouchListener(object : com.chad.library.adapter.base.listener.OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                for (dim in dims){
                    dim.isSelected = false
                    experimentDimDao.create(dim)
                }
                dims[position].isSelected = true
                experimentDimDao.create(dims[position])
                meditationDimSelectListAdapter.notifyDataSetChanged()
                onDimClickListener.invoke()
            }
        })
        list.adapter = meditationDimSelectListAdapter
        var layoutManager = FlexboxLayoutManager(mContext)
        layoutManager.flexWrap = FlexWrap.WRAP
        list.layoutManager = layoutManager
    }
}