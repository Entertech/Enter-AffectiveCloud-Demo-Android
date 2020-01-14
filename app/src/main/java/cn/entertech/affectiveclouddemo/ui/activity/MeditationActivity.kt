package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationFragment
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationLargeFragment

class MeditationActivity : BaseActivity() {

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        initFragment()
    }

    fun initFragment() {
        if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) {
            fragment = MeditationLargeFragment()
        } else {
            fragment = MeditationFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment!!).commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (fragment is MeditationFragment) {
            (fragment as MeditationFragment).showDialog()
        } else {
            (fragment as MeditationLargeFragment).showDialog()
        }
    }
}
