package cn.entertech.flowtimezh.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReportFragment : Fragment() {

    private var mCurrentRecord: UserLessonEntity? = null
    private var mRecordDao: UserLessonRecordDao? = null
    var mFragment: Fragment? = null
    var mRecordId: Long = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        mRecordDao = UserLessonRecordDao(activity!!)
        refreshFragment()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_TO_REFRESH_RECORD) {
            refreshFragment()
        }
    }

    fun getLastRecord(): UserLessonEntity? {
        var totalRecords = mRecordDao?.listAll(SettingManager.getInstance().userId)
        if (totalRecords == null || totalRecords.isEmpty()) {
            mRecordId = -2
        } else {
            mRecordId = totalRecords[0].id
        }
        return mRecordDao?.findRecordById(SettingManager.getInstance().userId, mRecordId)
    }

    fun refreshFragment() {
        var lastRecord: UserLessonEntity? = getLastRecord() ?: return
        mCurrentRecord = lastRecord
        if (mRecordDao?.getReportDataFromFile(lastRecord) != null) {
            mFragment = JournalFragment()
            var bundle = Bundle()
            bundle.putLong(RECORD_ID, mRecordId)
            mFragment!!.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.report_container, mFragment!!).commitAllowingStateLoss()
        } else {
            mFragment = OverviewDetailFragment()
            var bundle = Bundle()
            bundle.putLong(RECORD_ID, mRecordId)
            mFragment!!.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.report_container, mFragment!!).commitAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        var lastRecord = getLastRecord()
        if (lastRecord != null && (lastRecord.id != mCurrentRecord?.id)) {
            refreshFragment()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    open fun getShareView(): View {
        if (mFragment is JournalFragment) {
            return (mFragment as JournalFragment).getShareView()
        } else {
            return (mFragment as OverviewDetailFragment).getShareView()
        }
    }

    open fun getShareViewBg(): View {
        if (mFragment is JournalFragment) {
            return (mFragment as JournalFragment).getShareViewBg()
        } else {
            return (mFragment as OverviewDetailFragment).getShareViewBg()
        }
    }

}
