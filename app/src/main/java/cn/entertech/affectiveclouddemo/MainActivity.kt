package cn.entertech.affectiveclouddemo

import android.os.Bundle
import cn.entertech.affectiveclouddemo.model.TabEntity
import cn.entertech.affectiveclouddemo.ui.activity.BaseActivity
import cn.entertech.affectiveclouddemo.ui.fragment.*
import com.flyco.tablayout.listener.CustomTabEntity
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var statisticsFragment: StatisticsFragment
    var mTabEntitys = ArrayList<CustomTabEntity>()
    private lateinit var homeFragment: HomeFragment
    private lateinit var journeyFragment: JourneyFragment
    private lateinit var meFragment: MeFragment
    var mFragments = ArrayList<androidx.fragment.app.Fragment>()
    var mIconSelected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_selected, R.mipmap.ic_tab_bar_lib_selected, R.mipmap.ic_tab_bar_me_selected
    )
    var mIconUnselected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_unselected,
        R.mipmap.ic_tab_bar_lib_unselected,
        R.mipmap.ic_tab_bar_me_unselected
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }


    fun initView() {
        var mTitles = arrayOf(getString(R.string.home), getString(R.string.statistics), getString(R.string.me))
        homeFragment = HomeFragment()
        statisticsFragment = StatisticsFragment()
        meFragment = MeFragment()
        mFragments.add(homeFragment)
        mFragments.add(statisticsFragment)
        mFragments.add(meFragment)
        for (i in 0..2) {
            mTabEntitys.add(TabEntity(mTitles[i], mIconSelected[i], mIconUnselected[i]))
        }
        ctl_main.setTabData(mTabEntitys, this, R.id.fl_container, mFragments)
    }

}
