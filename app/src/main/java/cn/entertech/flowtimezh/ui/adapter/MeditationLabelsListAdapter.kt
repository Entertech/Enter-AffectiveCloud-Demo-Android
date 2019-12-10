package cn.entertech.flowtimezh.ui.adapter

import android.content.Intent
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.ExperimentDimDao
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import cn.entertech.flowtimezh.ui.activity.MeditationDimListActivity
import cn.entertech.flowtimezh.utils.TimeUtils.getFormatTime
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeditationLabelsListAdapter(data: List<MeditationLabelsModel>) :
    BaseQuickAdapter<MeditationLabelsModel, BaseViewHolder>(
        R.layout.item_meditation_labels_list,
        data
    ) {
    override fun convert(helper: BaseViewHolder, item: MeditationLabelsModel) {

        var dimDao = ExperimentDimDao(mContext)
        var dimNameList =
            item.dimIds.split(",").map { dimDao.findByDimId(Integer.parseInt(it)).nameCn }
        var dims = ""
        for (dimName in dimNameList) { dims = "$dims,$dimName" }
        helper.setText(R.id.tv_dims, dims.substring(1, dims.length))
        var duration = "${getFormatTime(item.startTime - item.meditationStartTime, "mm:ss")}" +
                "-${getFormatTime(item.endTime - item.meditationStartTime, "mm:ss")}"
        helper.setText(R.id.tv_label_time, duration)

        helper.itemView.setOnClickListener {
            var intent = Intent(mContext, MeditationDimListActivity::class.java)
            intent.putExtra("dimIds", item.dimIds)
            intent.putExtra("duration", duration)
            mContext.startActivity(intent)
        }
    }
}