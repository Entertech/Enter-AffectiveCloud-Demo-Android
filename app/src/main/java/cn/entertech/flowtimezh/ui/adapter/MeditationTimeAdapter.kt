package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeditationTimeAdapter(data: List<Time>) :
    BaseQuickAdapter<MeditationTimeAdapter.Time, BaseViewHolder>(R.layout.item_meditation_setting, data) {
    override fun convert(helper: BaseViewHolder?, item: Time) {
        helper?.setText(R.id.tv_name, "${item.time} min")
        if (item.isSelected) {
            helper?.setVisible(R.id.iv_is_check, true)
        } else {
            helper?.setVisible(R.id.iv_is_check, false)
        }
    }

    data class Time(
        var time: Int,
        var isSelected: Boolean = false
    )
}