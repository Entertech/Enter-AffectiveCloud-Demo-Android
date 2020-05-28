package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Constant.Companion.RECORD_ID
import cn.entertech.affectiveclouddemo.database.UserLessonRecordDao
import cn.entertech.affectiveclouddemo.ui.fragment.JournalFragment
import cn.entertech.affectiveclouddemo.ui.fragment.JournalLargeFragment
import cn.entertech.affectiveclouddemo.utils.TimeUtils
import kotlinx.android.synthetic.main.layout_common_title.*

class DataActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    private var mRecordId: Long = -1L

    private lateinit var fragment: Fragment

    fun initView() {
        mRecordId = intent.getLongExtra(RECORD_ID, -1)
        iv_back.setOnClickListener {
            finish()
        }
        val fm = supportFragmentManager
        if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) {
            fragment = JournalLargeFragment()
        } else {
            fragment = JournalFragment()
        }
        var bundle = Bundle()
        bundle.putLong(RECORD_ID, mRecordId)
        fragment.arguments = bundle
        fm.beginTransaction().replace(R.id.data_content, fragment).commitAllowingStateLoss()
        initTitle()
    }

    fun initTitle() {
        var shareHeadView = LayoutInflater.from(this).inflate(R.layout.layout_share_head_view, null)
        var tvTime = shareHeadView.findViewById<TextView>(R.id.tv_time)
//        var tvUserName = shareHeadView.findViewById<TextView>(R.id.tv_user_name)
//        tvUserName.text = "${SettingManager.getInstance().socialUserName}'s"
//        var shareFootView = LayoutInflater.from(this).inflate(R.layout.layout_product_share_view, null)
//        rl_menu_ic.visibility = View.VISIBLE
//        iv_menu_icon.setBackgroundResource(R.drawable.vector_drawable_share)
//        iv_menu_icon.setOnClickListener {
//            var scrollView = fragment.getScrollView()
//            var llBg = fragment.getLinearLayoutBg()
//            llBg.setBackgroundColor(this.getColor(R.color.colorShareBg))
//            ShotShareUtil.shotScrollView(this, scrollView, shareHeadView, shareFootView)
//            llBg.setBackgroundColor(this.getColor(R.color.statisticsBg))
//        }
//        window.statusBarColor = this.getColor(R.color.statisticsBg)
//        rl_title_bg.setBackgroundColor(this.getColor(R.color.statisticsBg))
//        tv_menu_text.visibility = View.VISIBLE
//        tv_menu_text.setOnClickListener {
//            startActivity(Intent(this@DataActivity, StatisticsEditActivity::class.java))
//        }
        if (mRecordId != -1L) {
            var userLessonRecordDao = UserLessonRecordDao(this)
            var record = userLessonRecordDao.findRecordById(0, mRecordId)
            var formatStartTime = record.startTime.replace("T", " ").replace("Z", "")
            var startTime = TimeUtils.getFormatTime(
                TimeUtils.getStringToDate(formatStartTime, "yyyy-MM-dd HH:mm:ss"),
                "dd.MM.yyyy"
            )
            tv_title.text = startTime
            tvTime.text = startTime
        } else {
            tv_title.text = "Statistics"
            tvTime.text = ""
        }
    }
}
