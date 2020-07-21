package cn.entertech.flowtimezh.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.database.StatisticsDao
import cn.entertech.flowtimezh.database.model.StatisticsModel
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.StatisticsEntity
import cn.entertech.flowtimezh.mvp.presenter.StatisticsPresenter
import cn.entertech.flowtimezh.mvp.view.StatisticsView
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.ToastUtil

import kotlinx.android.synthetic.main.fragment_count.*
import kotlinx.android.synthetic.main.fragment_count.ll_bg
import kotlinx.android.synthetic.main.fragment_count.scroll_view
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CountFragment : androidx.fragment.app.Fragment() {
    lateinit var mSelfView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mSelfView = inflater.inflate(R.layout.fragment_count, container, false)
        initPresenter()
        EventBus.getDefault().register(this)
        return mSelfView
    }


    fun initView(){
        tv_date.text = TimeUtils.getFormatTime(System.currentTimeMillis(), "MMMM.yyyy")
        srl_data.setOnRefreshListener {
            mStatisticsPresenter.getStatistics()
        }
        scv_statistics_calendar.setCheckedColor((activity as BaseActivity).getColorInDarkMode(R.color.common_blue_primary_color_light,R.color.common_blue_primary_color_dark))
        scv_statistics_calendar.setUncheckColor((activity as BaseActivity).getColorInDarkMode(R.color.common_blue5_color_light,R.color.common_blue5_color_dark))
    }
    private lateinit var mStatisticsPresenter: StatisticsPresenter

    fun initPresenter() {
        var mStatisticsView = object : StatisticsView {
            override fun onSuccess(statisticsEntity: StatisticsEntity?) {
                srl_data.isRefreshing = false
                if (statisticsEntity != null) {
                    var statisticsDao = StatisticsDao(activity!!)
                    tv_total_time.text = statisticsDao.totalMins
                    tv_total_lessons.text = "${statisticsDao.totalLessons}"
                    tv_total_days.text = "${statisticsEntity.total_days}"
                    tv_current_streak.text = "${statisticsEntity.current_streak}"
                    tv_longest_streak.text = "${statisticsEntity.longest_streak}"
                    scv_statistics_calendar.setActiveDays(statisticsEntity.active_days)
                    var statisticsModel = StatisticsModel()
                    statisticsModel.activeDays = statisticsEntity.active_days
                    statisticsModel.createdAt = statisticsEntity.created_at
                    statisticsModel.updatedAt = statisticsEntity.updated_at
                    statisticsModel.id = statisticsEntity.id
                    statisticsModel.currentStreak = statisticsEntity.current_streak
                    statisticsModel.longestStreak = statisticsEntity.longest_streak
                    statisticsModel.totalDays = statisticsEntity.total_days
                    statisticsModel.user = statisticsEntity.user
                    statisticsModel.totalLessons = statisticsEntity.total_lessons
                    statisticsModel.totalTime = statisticsEntity.total_time
                    statisticsDao.create(statisticsModel)
                }
            }

            override fun onError(error: String) {
                srl_data.isRefreshing = false
                ToastUtil.toastShort(Application.getInstance(), error)
            }

        }

        mStatisticsPresenter = StatisticsPresenter(Application.getInstance())
        mStatisticsPresenter.onCreate()
        mStatisticsPresenter.attachView(mStatisticsView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStatisticsPresenter.getStatistics()
        initView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_TO_REFRESH_RECORD) {
            mStatisticsPresenter.getStatistics()
            mSelfView.findViewById<SwipeRefreshLayout>(R.id.srl_data).isRefreshing = true
        }
    }

    override fun onDestroy() {
        mStatisticsPresenter.onStop()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    open fun getShareView():View{
        return scroll_view
    }

    open fun getShareViewBg():View{
        return ll_bg
    }

    override fun onResume() {
        super.onResume()
        mStatisticsPresenter.getStatistics()
    }
}
