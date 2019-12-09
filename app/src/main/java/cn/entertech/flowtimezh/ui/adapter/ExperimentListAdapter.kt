package cn.entertech.flowtimezh.ui.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_EXPERIMENT_ID
import cn.entertech.flowtimezh.database.model.ExperimentModel
import cn.entertech.flowtimezh.ui.activity.ExperimentLabelActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExperimentListAdapter(data: List<ExperimentModel>) :
    BaseQuickAdapter<ExperimentModel, BaseViewHolder>(R.layout.item_experiment_choose_list, data) {
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
//        helper.setEnabled(R.id.iv_icon_to_labels, true)
//        helper.setOnClickListener(R.id.iv_icon_to_labels) {
//            mContext.startActivity(
//                Intent(
//                    mContext,
//                    ExperimentLabelActivity::class.java
//                ).putExtra(EXTRA_EXPERIMENT_ID, item.id)
//            )
//        }
    }
}