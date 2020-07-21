package cn.entertech.flowtimezh.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.text.Html
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.mvp.presenter.DeleteUserLessonPresenter
import cn.entertech.flowtimezh.mvp.presenter.GetUserLessonPresenter
import cn.entertech.flowtimezh.mvp.view.DeleteUserLessonView
import cn.entertech.flowtimezh.ui.activity.DataActivity
import cn.entertech.flowtimezh.ui.adapter.JourneyListAdapter
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.utils.reportfileutils.SyncManager

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import kotlinx.android.synthetic.main.fragment_journey.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class JourneyFragment : androidx.fragment.app.Fragment() {
    private var deleteUserLessonPresenter: DeleteUserLessonPresenter? = null
    private var userLessonsDao: UserLessonRecordDao? = null
    lateinit var mSelfView: View
    private lateinit var getUserLessonsPresenter: GetUserLessonPresenter
    var mUserLessons = ArrayList<UserLessonEntity>()
    var adapter = JourneyListAdapter(mUserLessons)

    private var loadingDialog: LoadingDialog? = null
    var deleteItem: UserLessonEntity? = null
    var deleteItemIds = ArrayList<Long>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mSelfView = inflater.inflate(R.layout.fragment_journey, container, false)
        loadingDialog = LoadingDialog(activity!!)
        EventBus.getDefault().register(this)
        deleteUserLessonPresenter = DeleteUserLessonPresenter(activity!!)
        deleteUserLessonPresenter?.onCreate()
        deleteUserLessonPresenter?.attachView(object : DeleteUserLessonView {
            override fun onSuccess(result: ResponseBody?, lessonId: Long) {
                loadingDialog?.dismiss()
                Toast.makeText(activity!!, "delete success", Toast.LENGTH_SHORT).show()
                var userLessonRecordDao = UserLessonRecordDao(activity)
                var deleteLesson = userLessonRecordDao.findRecordById(
                    SettingManager.getInstance().userId,
                    lessonId
                )
                deleteLesson.isDelete = true
                userLessonRecordDao.create(deleteLesson)
                initRecord()
            }

            override fun onError(error: String) {
                loadingDialog?.dismiss()
                Toast.makeText(activity!!, error, Toast.LENGTH_SHORT).show()
            }

        })
        return mSelfView
    }

    fun initSampleData() {
        var userLessonRecordDao = UserLessonRecordDao(activity)
        var meditationDao = MeditationDao(activity)
        var userLessonEntity = UserLessonEntity()
        var meditation = MeditationEntity()
        userLessonEntity.isSampleData = true
        userLessonEntity.id = -2
        userLessonEntity.createdAt = "2019-06-21T08:33:24.479980Z"
        userLessonEntity.updatedAt = "2019-06-21T08:33:24.480021Z"
        userLessonEntity.startTime = "2019-06-21T16:20:48Z"
        userLessonEntity.finishTime = "2019-06-21T16:33:19Z"
        userLessonEntity.user = SettingManager.getInstance().userId
        userLessonEntity.meditation = -2
        userLessonEntity.courseName = "Pain"
        userLessonEntity.lessonName = "Sensory Elements of Pain"
        userLessonEntity.courseImage =
            "https://dh6oa59q6zlln.cloudfront.net/courses/Pain/33x_N3lH2yp.png"
        userLessonRecordDao.create(userLessonEntity)
        meditation.id = -2
        meditation.startTime = "2019-06-21T16:20:50Z"
        meditation.finishTime = "2019-06-21T16:33:19Z"
        meditation.heartRateAvg = 84f
        meditation.heartRateMax = 102f
        meditation.heartRateMin = 77f
        meditation.heartRateVariabilityAvg = 23.75f
        meditation.attentionAvg = 78.98f
        meditation.attentionMax = 99.3f
        meditation.attentionMin = 0.0f
        meditation.relaxationAvg = 27.17f
        meditation.relaxationMax = 92.02f
        meditation.relaxationMin = 0f
        meditation.meditationFile = "sample"
        meditation.user = SettingManager.getInstance().userId
        meditationDao.create(meditation)

        var userLessonWithoutFile = UserLessonEntity()
        userLessonWithoutFile.isSampleData = true
        userLessonWithoutFile.id = -3
        userLessonWithoutFile.createdAt = "2019-06-21T08:33:24.479980Z"
        userLessonWithoutFile.updatedAt = "2019-06-21T08:33:24.480021Z"
        userLessonWithoutFile.startTime = "2019-06-21T16:20:48Z"
        userLessonWithoutFile.finishTime = "2019-06-21T16:33:19Z"
        userLessonWithoutFile.user = SettingManager.getInstance().userId
        userLessonWithoutFile.courseName = "Pain"
        userLessonWithoutFile.lessonName = "Sensory Elements of Pain"
        userLessonWithoutFile.courseImage =
            "https://dh6oa59q6zlln.cloudfront.net/courses/Pain/33x_N3lH2yp.png"
        userLessonRecordDao.create(userLessonWithoutFile)
    }

    fun initView() {
        adapter.setNewData(mUserLessons)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.ll_item->{
//                    postButtonEvent(activity!!, "1102", "报表列表界面 选择报表")
                    var intent = Intent(activity, DataActivity::class.java)
                    intent.putExtra(RECORD_ID, (adapter?.getItem(position) as UserLessonEntity).id)
                    startActivity(intent)
                }
            }
        }
        rv_journey_list.adapter = adapter
        rv_journey_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
//        rv_journey_list.addOnItemTouchListener(object : OnItemClickListener() {
//            override fun onSimpleItemClick(
//                adapter: BaseQuickAdapter<*, *>?,
//                view: View?,
//                position: Int
//            ) {
//            }
//        })
        srl_list.setOnRefreshListener {
            loadingDialog?.loading()
            SyncManager.getInstance().getRecords(fun() {
                loadingDialog?.dismiss()
                srl_list.isRefreshing = false
                initRecord()
            })
        }
        rv_journey_list.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            var layoutManager = rv_journey_list.layoutManager as LinearLayoutManager
            var pos = layoutManager.findFirstVisibleItemPosition()
            var firstVisiableChildView = layoutManager?.findViewByPosition(pos)
            var itemHeight = firstVisiableChildView?.height
            if (itemHeight != null) {
                var y = (pos) * itemHeight - firstVisiableChildView!!.top
                mListScrollCallback?.invoke(y)
            }
        }

        if (SettingManager.getInstance().isJourneyTip) {
            gtv_journey.visibility = View.VISIBLE
        } else {
            gtv_journey.visibility = View.GONE
        }


        val onItemSwipeListener = object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                if (!deleteItemIds.contains(mUserLessons[pos].id)) {
                    deleteItemIds.add(mUserLessons[pos].id)
                }
            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            }

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                showDeleteDialog(deleteItemIds)
            }

            override fun onItemSwipeMoving(
                canvas: Canvas,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                isCurrentlyActive: Boolean
            ) {
            }
        }
        val mItemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val mItemTouchHelper = ItemTouchHelper(mItemDragAndSwipeCallback)
        mItemTouchHelper.attachToRecyclerView(rv_journey_list)
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        adapter.enableSwipeItem()
        adapter.setOnItemSwipeListener(onItemSwipeListener)
    }

    var isShowDeleteBtnListener = fun(isShowDeleteBtn: Boolean) {
        if (isShowDeleteBtn) {
            activity!!.findViewById<TextView>(R.id.tv_left_text).visibility = View.VISIBLE
            activity!!.findViewById<ImageView>(R.id.iv_back).visibility = View.GONE
        } else {
            activity!!.findViewById<TextView>(R.id.tv_left_text).visibility = View.GONE
            activity!!.findViewById<ImageView>(R.id.iv_back).visibility = View.VISIBLE
        }
    }

    fun edit() {
        adapter.isEdit(true,isShowDeleteBtnListener)
//        postButtonEvent(activity!!,"1101","报表列表界面 编辑")
    }

    fun cancel() {
        adapter.isEdit(false,isShowDeleteBtnListener)
    }

    fun deleteAll() {
//        postButtonEvent(activity!!,"1103","报表列表界面 删除")
        mUserLessons.filter { it.isSelected }.map { it.id }
            .forEach { deleteUserLessonPresenter?.deleteUserLesson(it) }
        cancel()
        activity!!.findViewById<TextView>(R.id.tv_menu_text).text = "Edit"
        activity!!.findViewById<TextView>(R.id.tv_left_text).visibility = View.GONE
        activity!!.findViewById<ImageView>(R.id.iv_back).visibility = View.VISIBLE
    }

    fun showDeleteDialog(deleteItemIds: ArrayList<Long>?) {
        var dialog = AlertDialog.Builder(activity)
            .setMessage(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        activity!!,
                        R.color.colorDialogContent
                    )}'>Delete record?</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        activity!!,
                        R.color.colorDialogExit
                    )}'>Yes</font>"
                )
            ) { dialog, which ->
                if (deleteItemIds != null) {
                    loadingDialog?.loading()
                    deleteItemIds.forEach {
                        deleteUserLessonPresenter?.deleteUserLesson(it)
                    }
                    deleteItemIds.clear()
                }
                dialog.dismiss()
            }
            .setNegativeButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        activity!!,
                        R.color.colorDialogCancel
                    )}'>No</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
                initRecord()
            }.create()
        dialog.show()
    }

    private var mListScrollCallback: ((Int) -> Unit)? = null

    open fun addListScrollCallback(callback: ((Int) -> Unit)?) {
        mListScrollCallback = callback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecord()
        initView()
        loadingDialog?.loading()
        SyncManager.getInstance().getRecords(fun() {
            loadingDialog?.dismiss()
            srl_list.isRefreshing = false
            initRecord()
        })
    }

    fun initRecord() {
        mUserLessons.clear()
        userLessonsDao = UserLessonRecordDao(activity)
        if (userLessonsDao!!.listAll(SettingManager.getInstance().userId).size == 0) {
            initSampleData()
            mUserLessons.addAll(userLessonsDao!!.listAllSampleData())
        } else {
            mUserLessons.addAll(userLessonsDao!!.listAll(SettingManager.getInstance().userId))
        }
        adapter.setNewData(mUserLessons)
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteUserLessonPresenter?.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_TO_REFRESH_RECORD) {
            var userLessonsDao = UserLessonRecordDao(activity)
            adapter.setNewData(userLessonsDao.listAll(SettingManager.getInstance().userId))
        }
        if (event.messageCode == MessageEvent.MESSAGE_CODE_REFRESH_JOURNEY) {
            getRecords()
        }
    }

    fun getRecords(){
        loadingDialog?.loading()
        SyncManager.getInstance().getRecords(fun() {
            loadingDialog?.dismiss()
            initRecord()
        }, fun() {
//            loadingDialog?.loading()
        }, fun() {
            loadingDialog?.dismiss()
            adapter.notifyDataSetChanged()
        })
    }
}
