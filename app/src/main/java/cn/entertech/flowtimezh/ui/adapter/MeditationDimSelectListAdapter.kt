package cn.entertech.flowtimezh.ui.adapter

import androidx.core.content.ContextCompat
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.model.ExperimentDimModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeditationDimSelectListAdapter(data: List<ExperimentDimModel>) :
    BaseQuickAdapter<ExperimentDimModel, BaseViewHolder>(
        R.layout.item_meditation_label_list_child,data
    ) {
    override fun convert(helper: BaseViewHolder, item: ExperimentDimModel) {
        helper.setText(R.id.tv_meditation_dim, item.nameCn)
        if (item.isSelected) {
            helper.setBackgroundRes(
                R.id.tv_meditation_dim,
                R.drawable.shape_meditation_dim_selected_bg
            )
            helper.setTextColor(
                R.id.tv_meditation_dim,
                ContextCompat.getColor(mContext, R.color.white)
            )
        } else {
            helper.setBackgroundRes(
                R.id.tv_meditation_dim,
                R.drawable.shape_meditation_dim_unselected_bg
            )
            helper.setTextColor(
                R.id.tv_meditation_dim,
                ContextCompat.getColor(mContext, R.color.colorBlack)
            )
        }
    }
}