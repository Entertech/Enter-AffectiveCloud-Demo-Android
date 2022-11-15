package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.model.ExperimentModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExperimentListAdapter(data: List<ExperimentModel>) :
    BaseQuickAdapter<ExperimentModel, BaseViewHolder>(R.layout.item_lab_list, data) {
    override fun convert(helper: BaseViewHolder, item: ExperimentModel) {
        if (helper == null) {
            return
        }
        helper.setText(R.id.tv_experiment_name, item.nameCn)

        if (item.isSelected) {
            helper.setVisible(R.id.iv_icon_is_selected, true)
        } else {
            helper.setVisible(R.id.iv_icon_is_selected, false)
        }

        helper.addOnClickListener(R.id.iv_icon_to_labels)
    }
}