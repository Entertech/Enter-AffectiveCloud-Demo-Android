package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class MeditationTimeSelectAdapter(data: List<MeditationTimeEntity>) :
    BaseQuickAdapter<MeditationTimeEntity, BaseViewHolder>(R.layout.item_meditation_time, data) {
    override fun convert(helper: BaseViewHolder?, time: MeditationTimeEntity) {
        helper?.setText(R.id.tv_time, "${time.min}")
        if (time.isSelected) {
            helper?.setBackgroundRes(R.id.rl_bg, R.drawable.shape_meditation_time_select)
            helper?.setTextColor(R.id.tv_time,(mContext as BaseActivity).getColorInDarkMode(R.color.common_white_color_light,R.color.common_white_color_dark))
            helper?.setTextColor(R.id.tv_time_unit,(mContext as BaseActivity).getColorInDarkMode(R.color.common_white_color_light,R.color.common_white_color_dark))
        } else {
            helper?.setBackgroundRes(R.id.rl_bg, R.drawable.shape_meditation_time_unselect)
            helper?.setTextColor(R.id.tv_time,(mContext as BaseActivity).getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
            helper?.setTextColor(R.id.tv_time_unit,(mContext as BaseActivity).getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
        }
    }
}

class MeditationTimeEntity {
    var min: Int? = 0
    var isSelected: Boolean = false
}