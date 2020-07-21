package cn.entertech.flowtimezh.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.ItemTouchHelper
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.ui.adapter.DataEditListAdapter
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import org.greenrobot.eventbus.EventBus


class MeditationEditFragment : androidx.fragment.app.Fragment() {
    lateinit var mSelfView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mSelfView = inflater.inflate(R.layout.fragment_data_edit, container, false)
        initView()
        return mSelfView
    }


    fun initView() {
        var itemsArray = SettingManager.getInstance().meditationViewOrder.split(",")
        var rvdataEditList = mSelfView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_data_edit_list)
        var adapter = DataEditListAdapter(itemsArray.toList())
        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(rvdataEditList)

        adapter.enableDragItem(itemTouchHelper, R.id.iv_drag, true)
        adapter.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragMoving(
                source: androidx.recyclerview.widget.RecyclerView.ViewHolder?,
                from: Int,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder?,
                to: Int
            ) {

            }

            override fun onItemDragStart(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, pos: Int) {
            }

            override fun onItemDragEnd(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, pos: Int) {
                var viewOrder = ""
                for (i in 0 until adapter.data.size) {
                    viewOrder += adapter.data[i] + ","
                }
                SettingManager.getInstance().meditationViewOrder = viewOrder.substring(0, viewOrder.length - 1)
            }

        })
        rvdataEditList.adapter = adapter
        rvdataEditList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        mSelfView.findViewById<TextView>(R.id.tv_edit_done).setOnClickListener {
            var messageEvent = MessageEvent()
            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_DATA_EDIT_DONE
            messageEvent.message = "edit done"
            EventBus.getDefault().post(messageEvent)
        }
    }
}
