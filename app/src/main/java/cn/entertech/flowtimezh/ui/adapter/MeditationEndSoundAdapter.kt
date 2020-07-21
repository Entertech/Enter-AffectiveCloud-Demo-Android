package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MeditationEndSoundAdapter(data: List<EndSound>) :
    BaseQuickAdapter<MeditationEndSoundAdapter.EndSound, BaseViewHolder>(R.layout.item_meditation_setting, data) {
    override fun convert(helper: BaseViewHolder?, item: EndSound) {
        helper?.setText(R.id.tv_name, item.soundName)
        if (item.isSelected) {
            helper?.setVisible(R.id.iv_is_check, true)
        } else {
            helper?.setVisible(R.id.iv_is_check, false)
        }
    }

    data class EndSound(
        var soundName: String? = null,
        var resId: Int? = 0,
        var isSelected: Boolean = false
    )
}