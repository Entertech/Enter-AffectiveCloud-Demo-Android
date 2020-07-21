package cn.entertech.flowtimezh.ui.adapter
import cn.entertech.flowtimezh.R
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DataEditListAdapter(data:List<String>):
    BaseItemDraggableAdapter<String, BaseViewHolder>(R.layout.item_data_edit_list,data){
    override fun convert(helper: BaseViewHolder?, item: String) {
        helper?.setText(R.id.tv_label,item)
    }
}