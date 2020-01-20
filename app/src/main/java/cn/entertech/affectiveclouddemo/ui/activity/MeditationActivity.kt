package cn.entertech.affectiveclouddemo.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationFragment
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationLargeFragment
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import kotlinx.android.synthetic.main.activity_meditation.*

class MeditationActivity : BaseActivity() {

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        Log.d("####","onCreate")
        initFragment()
    }

    fun initFragment() {
        fl_container.removeAllViews()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("####","onConfigurationChanged")
        if (fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment!!).commit()
        }
        initFragment()
    }
}
