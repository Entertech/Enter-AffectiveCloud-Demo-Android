package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.model.MeditationEntity
import cn.entertech.affectiveclouddemo.model.UserLessonEntity
import cn.entertech.affectiveclouddemo.ui.activity.DataActivity
import cn.entertech.affectiveclouddemo.ui.activity.currentActivity
import cn.entertech.affectiveclouddemo.ui.adapter.JourneyListAdapter
import cn.entertech.affectiveclouddemo.utils.LogManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_journey.*

class JourneyFragment : androidx.fragment.app.Fragment() {
    private var userLessonsDao: UserLessonRecordDao? = null
    lateinit var mSelfView: View
    var mUserLessons = ArrayList<UserLessonEntity>()
    var adapter = JourneyListAdapter(mUserLessons)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mSelfView = inflater.inflate(R.layout.fragment_journey, container, false)
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
        userLessonEntity.user = 0
        userLessonEntity.meditation = -2
        userLessonEntity.courseName = "Pain"
        userLessonEntity.lessonName = "Sensory Elements of Pain"
        userLessonEntity.courseImage = "https://dh6oa59q6zlln.cloudfront.net/courses/Pain/33x_N3lH2yp.png"
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
        meditation.user = 0
        meditationDao.create(meditation)

        var userLessonWithoutFile = UserLessonEntity()
        userLessonWithoutFile.isSampleData = true
        userLessonWithoutFile.id = -3
        userLessonWithoutFile.createdAt = "2019-06-21T08:33:24.479980Z"
        userLessonWithoutFile.updatedAt = "2019-06-21T08:33:24.480021Z"
        userLessonWithoutFile.startTime = "2019-06-21T16:20:48Z"
        userLessonWithoutFile.finishTime = "2019-06-21T16:33:19Z"
        userLessonWithoutFile.user = 0
        userLessonWithoutFile.courseName = "Pain"
        userLessonWithoutFile.lessonName = "Sensory Elements of Pain"
        userLessonWithoutFile.courseImage = "https://dh6oa59q6zlln.cloudfront.net/courses/Pain/33x_N3lH2yp.png"
        userLessonRecordDao.create(userLessonWithoutFile)
    }

    fun initView() {
        adapter.setNewData(mUserLessons)
        rv_journey_list.adapter = adapter
        rv_journey_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rv_journey_list.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                LogManager.getInstance().logPost("Button $currentActivity to history record")
                var intent = Intent(activity, DataActivity::class.java)
                intent.putExtra(RECORD_ID, (adapter?.getItem(position) as UserLessonEntity).id)
                startActivity(intent)
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecord()
        initView()
    }

    override fun onResume() {
        super.onResume()
        initRecord()
    }

    fun initRecord(){
        mUserLessons.clear()
        userLessonsDao = UserLessonRecordDao(activity)
        if (userLessonsDao!!.listAll(0).size == 0){
            initSampleData()
            mUserLessons.addAll(userLessonsDao!!.listAllSampleData())
        }else{
            mUserLessons.addAll(userLessonsDao!!.listAll(0))
        }
        adapter.setNewData(mUserLessons)
    }
}
