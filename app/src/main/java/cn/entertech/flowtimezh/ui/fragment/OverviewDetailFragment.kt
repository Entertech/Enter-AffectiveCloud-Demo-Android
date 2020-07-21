package cn.entertech.flowtimezh.ui.fragment

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.StatisticsDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.FirebaseRemoteConfigShare
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.getFormatNumSuffix

import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_overview_detail.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class OverviewDetailFragment : Fragment() {

    private var currentDuration: Int = 0
    private var totalDuration: String = ""
    private var isMeditationTimeLongerThanAverage: Boolean = false
    private var isMeditationTimeLongerThanLast: Boolean = false
    private var totalReocrds: MutableList<UserLessonEntity>? = null
    private var mRecord: UserLessonEntity? = null
    private var mRecordDao: UserLessonRecordDao? = null
    private var mRecordId: Long? = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecordId = arguments?.getLong(Constant.RECORD_ID, -1)
        init()
        initMeditationTime()
        initStatistics()
        initCourseView()
        initShareView()
    }

    fun initShareView(){
        if (isShareCondition()&&!SettingManager.getInstance().isReportShared("${mRecordId}_${Constant.REPORT_SHARE_PAGE_COURSE}")){
            var messageEvent = MessageEvent()
            messageEvent.message = "$mRecordId"
            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_SHARE_REPORT
            messageEvent.data = "course"
            EventBus.getDefault().post(messageEvent)
        }
    }
    fun refresh(recordId: Long) {
        this.mRecordId = recordId
        init()
        initMeditationTime()
        initStatistics()
//        initCourseView()
    }


    private fun init() {
        mRecordDao = UserLessonRecordDao(activity!!)
        if (mRecordId != null) {
            mRecord = mRecordDao?.findRecordById(SettingManager.getInstance().userId, mRecordId!!)
        }
    }

    private fun initMeditationTime() {
        iv_meditation_info.setOnClickListener {
            var uri = Uri.parse("https://www.notion.so/Last-7-Times-15a5331f15a3438ca1abdcbf2b0ff331")
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        totalReocrds = mRecordDao?.listAll(SettingManager.getInstance().userId)
        if (totalReocrds == null || mRecord == null) {
            return
        }
        var lastRecordList = ArrayList<UserLessonEntity>()
        lastRecordList.add(mRecord!!)
        var currentRecordIndex = 0
        for (i in totalReocrds!!.indices) {
            if (totalReocrds!![i].id == mRecord!!.id) {
                currentRecordIndex = i
            }
        }
        while (lastRecordList.size < 7 && currentRecordIndex++ < totalReocrds!!.size - 1) {
            lastRecordList.add(totalReocrds!![currentRecordIndex])
        }

        var lastMeditationTime = lastRecordList.asReversed().map {
            Integer.parseInt(
                TimeUtils.timeStampToMin(
                    TimeUtils.getStringToDate(
                        it?.finishTime,
                        "yyyy-MM-dd HH:mm:ss"
                    ) - TimeUtils.getStringToDate(
                        it?.startTime,
                        "yyyy-MM-dd HH:mm:ss"
                    )
                )
            )
        }
        last_meditation_time_chart.setValues(lastMeditationTime)
        var average = lastMeditationTime.average().toInt()
        currentDuration = lastMeditationTime[lastMeditationTime.size - 1]
        if (lastMeditationTime != null && lastMeditationTime.size >= 2) {
            currentDuration > lastMeditationTime[lastMeditationTime.size - 2]
            isMeditationTimeLongerThanLast = true
        }
        if (currentDuration > average) {
            isMeditationTimeLongerThanAverage = true
            iv_average_icon.setImageResource(R.drawable.vector_drawable_report_average_arrow_up)
            tv_average_tip.text = "The meditation time is longer than the average of last 7 times."
        } else if (currentDuration < average) {
            iv_average_icon.setImageResource(R.drawable.vector_drawable_report_average_arrow_down)
            tv_average_tip.text = "The meditation time is shorter than the average of last 7 times."
        } else {
            iv_average_icon.setImageResource(R.mipmap.ic_average_equal)
            tv_average_tip.text = "The meditation time is the same as the average of last 7 times."
        }
        tv_average_tip.setTextColor((activity as BaseActivity).getColorInDarkMode(R.color.common_text_lv1_base_color_light,R.color.common_text_lv1_base_color_dark))
        (tip_bg.background as GradientDrawable).setColor((activity as BaseActivity).getColorInDarkMode(R.color.common_bg_z2_color_light,R.color.common_bg_z2_color_dark))
    }

    fun initStatistics() {
        if (totalReocrds == null) {
            return
        }
        var currentRecordIndex = 0
        var totalRecodsReverse = totalReocrds!!.asReversed()
        for (i in totalRecodsReverse!!.indices) {
            if (totalRecodsReverse!![i].id == mRecord!!.id) {
                currentRecordIndex = i
            }
        }
        tv_experience_time.text =
            "${currentRecordIndex + 1}${getFormatNumSuffix(currentRecordIndex + 1)}"
        var statisticsDao = StatisticsDao(activity!!)
        var statistics = statisticsDao.listAll(SettingManager.getInstance().userId)
        if (statistics != null && statistics.isNotEmpty()) {
            totalDuration = statisticsDao.totalMins
            tv_experience_total_min.text = "${statisticsDao.totalMins} minutes"
        }
        card_statistics.setOnClickListener {
//            postButtonEvent(activity!!,"1903","课程详情报表界面 statistics详情")
//            startActivity(
//                Intent(activity!!, StatisticsActivity::class.java).putExtra(
//                    RECORD_ID,
//                    mRecordId
//                )
//            )
            //todo
        }
    }

    fun isShareCondition(): Boolean {
        var shareJson = SettingManager.getInstance().remoteConfigShareCondition
        var shareCondition = Gson().fromJson<FirebaseRemoteConfigShare>(
            shareJson,
            FirebaseRemoteConfigShare::class.java
        )
        var isDurationLarge = (currentDuration > shareCondition.course_page_share_conditions.min_meditation_time)
        var isTotalDurationLarge = ( try {
            Integer.parseInt(totalDuration) > shareCondition.course_page_share_conditions.min_meditation_total_time
        } catch (e: Exception) {
            false
        })
        return isDurationLarge && isTotalDurationLarge && isMeditationTimeLongerThanLast && isMeditationTimeLongerThanAverage
    }

    fun initCourseView() {
        if (mRecord == null) {
            return
        }
//        tv_course_name.text = mRecord?.courseName
//        var courseDao = CourseDao(activity)
//        var course = courseDao.findByCourseId(mRecord!!.courseId)
//        if (course == null || course.courseName == "unguide") {
//            iv_course_info.visibility = View.GONE
//        }
//        card_course.setOnClickListener {
//            if (course != null && course.courseName != "unguide") {
//                postButtonEvent(activity!!,"1904","课程详情报表界面 course详情")
//                var intent = Intent(activity!!, CourseDetailActivity::class.java)
//                intent.putExtra(COURSE_NAME, course.courseName)
//                intent.putExtra(COURSE_DESCRIPTION, course.courseDescription)
//                intent.putExtra(COURSE_IMAGE, course.courseSourceImage)
//                intent.putExtra(COURSE_AUTHOR_ID, course.authorId)
//                intent.putExtra(COURSE_ID, course.courseId)
//                startActivity(intent)
                //todo
//            }

//        }
    }

    open fun getShareView(): View {
        return scroll_view
    }

    open fun getShareViewBg(): View {
        return ll_bg
    }
}
