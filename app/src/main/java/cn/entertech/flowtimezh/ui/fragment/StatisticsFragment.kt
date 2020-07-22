package cn.entertech.flowtimezh.ui.fragment

import android.animation.Animator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.ui.activity.RecordHistoryActivity
import cn.entertech.flowtimezh.utils.ShotShareUtil
import cn.entertech.flowtimezh.utils.getExperienceStartTime

import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StatisticsFragment : androidx.fragment.app.Fragment() {
    private var titles: Array<String>? = null
    private var recordId: Long = -1

    private var popupWindow: PopupWindow? = null
    var self: View? = null
    var mFragment = ArrayList<androidx.fragment.app.Fragment>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this)
        titles = arrayOf(getString(R.string.report_title_journal), getString(R.string.report_title_statistics))
        self = inflater.inflate(R.layout.fragment_statistics, container, false)
        return self
    }

    override fun onResume() {
        super.onResume()
//        FirebaseAnalytics.getInstance(activity!!).setCurrentScreen(activity!!, "Statistics界面", null)
    }
    fun initView() {
        ll_bg.setBackgroundColor(activity!!.getColor(R.color.statisticsBg))
        var recordDao = UserLessonRecordDao(activity!!)
        recordId = -1L
        var totalRecords = recordDao.listAll(SettingManager.getInstance().userId)
        if (totalRecords == null || totalRecords.isEmpty()) {
            initSampleData()
            recordId = -2
        } else {
            recordId = totalRecords[0].id
        }

//        var lastRecord = recordDao.findRecordById(SettingManager.getInstance().userId, recordId)
//        if (recordDao.getReportDataFromFile(lastRecord) != null) {
//            var firstFragment = JournalFragment()
//            var bundle = Bundle()
//            bundle.putLong(RECORD_ID, recordId)
//            firstFragment.arguments = bundle
//            mFragment.add(firstFragment)
//        } else {
//            var firstFragment = OverviewDetailFragment()
//            var bundle = Bundle()
//            bundle.putLong(RECORD_ID, recordId)
//            firstFragment.arguments = bundle
//            mFragment.add(firstFragment)
//        }
        mFragment.add(ReportFragment())
        mFragment.add(CountFragment())
        view_pager.adapter = ViewPagerAdapter(fragmentManager!!)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 0) {
                    var maxTextSize = 19f
                    var minTextSize = 16f
                    var textSizeMaxOffset = maxTextSize - minTextSize
                    var journeyTextSize = maxTextSize - textSizeMaxOffset * positionOffset
                    var statisticsTextSize = minTextSize + textSizeMaxOffset * positionOffset
                    tv_title_journey.textSize = journeyTextSize
                    tv_title_statistics.textSize = statisticsTextSize
                    if (positionOffset < 0.5f) {
                        tv_title_journey.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.colorThemeBlue
                            )
                        )
                        tv_title_statistics.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.colorUnselectedText
                            )
                        )
                        tv_title_journey.setTypeface(null, Typeface.BOLD)
                        tv_title_statistics.setTypeface(null, Typeface.NORMAL)
                    } else {
                        tv_title_journey.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.colorUnselectedText
                            )
                        )
                        tv_title_statistics.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.colorThemeBlue
                            )
                        )
                        tv_title_journey.setTypeface(null, Typeface.NORMAL)
                        tv_title_statistics.setTypeface(null, Typeface.BOLD)
                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }

        })
//        st_statistics.setViewPager(view_pager)
//        st_statistics.setTabData(titles, activity!!, R.id.data_content, mFragment)
        iv_record_history.setOnClickListener {
//            postButtonEvent(activity!!,"403","Statistics 报表列表记录")
            startActivity(Intent(activity!!, RecordHistoryActivity::class.java))
        }
        iv_share.setOnClickListener {
//            postButtonEvent(activity!!,"404","Statistics 分享")
            shareReport()
        }

        tv_title_journey.setOnClickListener {
//            postButtonEvent(activity!!,"401","Statistics Journal选择器")
            onJourneyTabSelected()
            view_pager.currentItem = 0
        }
        tv_title_statistics.setOnClickListener {
//            postButtonEvent(activity!!,"402","Statistics Statistics选择器")
            onStatisticsTabSelected()
            view_pager.currentItem = 1
        }
    }

    fun shareReport(){
        var shareHeadView =
            LayoutInflater.from(activity).inflate(R.layout.layout_share_head_view, null)
        var tvTime = shareHeadView.findViewById<TextView>(R.id.tv_time)
        tvTime.text = getExperienceStartTime(activity!!, recordId)
        var tvUserName = shareHeadView.findViewById<TextView>(R.id.tv_user_name)
        tvUserName.text = "${SettingManager.getInstance().socialUserName}'s"
        var shareFootView =
            LayoutInflater.from(activity!!).inflate(R.layout.layout_product_share_view, null)
        var scrollView = (mFragment[0] as ReportFragment).getShareView() as NestedScrollView
        var llBg = (mFragment[0] as ReportFragment).getShareViewBg() as LinearLayout
        llBg.setBackgroundColor((activity!! as BaseActivity).getColorInDarkMode(R.color.common_share_bg_color_light,R.color.common_share_bg_color_dark))
        ShotShareUtil.shotScrollView(activity!!, scrollView, shareHeadView, shareFootView)
        llBg.setBackgroundColor((activity!! as BaseActivity).getColorInDarkMode(R.color.common_bg2_color_light,R.color.common_bg2_color_dark))

    }

    fun onJourneyTabSelected() {
        tv_title_journey.textSize = 19f
        tv_title_statistics.textSize = 16f
        tv_title_journey.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorThemeBlue
            )
        )
        tv_title_statistics.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorUnselectedText
            )
        )
        tv_title_journey.setTypeface(null, Typeface.BOLD)
        tv_title_statistics.setTypeface(null, Typeface.NORMAL)
    }

    fun onStatisticsTabSelected() {
        tv_title_journey.textSize = 16f
        tv_title_statistics.textSize = 19f
        tv_title_journey.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorUnselectedText
            )
        )
        tv_title_statistics.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorThemeBlue
            )
        )
        tv_title_journey.setTypeface(null, Typeface.NORMAL)
        tv_title_statistics.setTypeface(null, Typeface.BOLD)
    }


    private inner class ViewPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return mFragment.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles!![position]
        }

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return mFragment[position]
        }
    }

    fun refreshFragment(recordId: Long) {
        if (mFragment[0] is OverviewDetailFragment) {
            (mFragment[0] as OverviewDetailFragment).refresh(recordId)
        } else {
            (mFragment[0] as JournalFragment).refresh(recordId)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_SHARE_REPORT && event.message == "$recordId") {
            if (event.data == "report"){
                SettingManager.getInstance().setIsReportShared("${recordId}_${Constant.REPORT_SHARE_PAGE_REPORT}",true)
            }else{
                SettingManager.getInstance().setIsReportShared("${recordId}_${Constant.REPORT_SHARE_PAGE_COURSE}",true)
            }
            showShareView()
        }else{
            hideShareView()
        }
    }
    fun showShareView() {
        self?.findViewById<ImageView>(R.id.iv_menu_icon)?.visibility = View.GONE
        self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.visibility = View.VISIBLE
        self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.setOnClickListener {
            shareReport()
            hideShareView()
        }
        var popView =
            LayoutInflater.from(activity).inflate(R.layout.pop_share_tip, null)
        popupWindow = PopupWindow(
            popView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.post {
            popupWindow?.showAsDropDown(self?.findViewById<LottieAnimationView>(R.id.lottie_view), 0, 0, Gravity.BOTTOM)
        }

        self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                self?.findViewById<ImageView>(R.id.iv_menu_icon)?.visibility = View.VISIBLE
                self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.visibility = View.GONE
            }

        })

        Handler().postDelayed(object:Runnable{
            override fun run() {
                if (popupWindow != null && popupWindow!!.isShowing){
                    popupWindow!!.dismiss()
                }
            }

        },5000)
    }

    fun hideShareView() {
        self?.findViewById<ImageView>(R.id.iv_menu_icon)?.visibility = View.VISIBLE
        self?.findViewById<LottieAnimationView>(R.id.lottie_view)?.visibility = View.GONE
        popupWindow?.dismiss()
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
