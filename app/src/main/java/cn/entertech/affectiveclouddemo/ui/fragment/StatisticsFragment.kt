package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.database.MeditationDao
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.model.MeditationEntity
import cn.entertech.affectiveclouddemo.model.UserLessonEntity
import cn.entertech.affectiveclouddemo.ui.activity.RecordHistoryActivity
import cn.entertech.affectiveclouddemo.ui.activity.currentActivity
import cn.entertech.affectiveclouddemo.utils.LogManager
import cn.entertech.bleuisdk.utils.SettingManager
import kotlinx.android.synthetic.main.layout_common_title.*

class StatisticsFragment : Fragment() {

    private var mRecordId: Long = -1
    private var mCurrentRecord: UserLessonEntity? = null
    private var mRecordDao: UserLessonRecordDao? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        initTitle()
        mRecordDao = UserLessonRecordDao(activity!!)
        refreshFragment()
    }

    fun initTitle() {
        tv_title.text = "统计"
        iv_back.visibility = View.GONE
        iv_menu_icon.visibility = View.VISIBLE
        iv_menu_icon.setImageResource(R.drawable.vector_drawable_record_history_icon)
        iv_menu_icon.setOnClickListener {
            LogManager.getInstance().logPost("Button $currentActivity to history record list")
            var intent = Intent(activity!!,RecordHistoryActivity::class.java)
            activity!!.startActivity(intent)
        }
    }

    fun getLastRecord(): UserLessonEntity? {
        var totalRecords = mRecordDao?.listAll(0)
        if (totalRecords == null || totalRecords.isEmpty()) {
            initSampleData()
            mRecordId = -2
        } else {
            mRecordId = totalRecords[0].id
        }
        return mRecordDao?.findRecordById(0, mRecordId)
    }

    fun refreshFragment() {
        var lastRecord: UserLessonEntity? = getLastRecord() ?: return
        mCurrentRecord = lastRecord
        if (mRecordDao?.getReportDataFromFile(lastRecord) != null) {
            var fragment = JournalFragment()
            var bundle = Bundle()
            bundle.putLong(RECORD_ID, mRecordId)
            fragment.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.report_container, fragment).commitAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        var lastRecord = getLastRecord()
        if (lastRecord != null && (lastRecord.id != mCurrentRecord?.id)) {
            refreshFragment()
        }
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


}
