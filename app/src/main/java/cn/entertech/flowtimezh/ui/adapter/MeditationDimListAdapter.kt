package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeditationDimListAdapter(data: List<ExperimentDimModel>) :
    BaseQuickAdapter<ExperimentDimModel, BaseViewHolder>(
        R.layout.item_meditation_dim_list,
        data
    ) {
    override fun convert(helper: BaseViewHolder, item: ExperimentDimModel) {
        helper.setText(R.id.tv_dim_name, item.nameCn)
        helper.addOnClickListener(R.id.rl_item_view)

    }
}