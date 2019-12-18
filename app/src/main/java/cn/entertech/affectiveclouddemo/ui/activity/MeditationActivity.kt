package cn.entertech.affectiveclouddemo.ui.activity

import android.os.Bundle
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.ui.fragment.MeditationFragment
import kotlinx.android.synthetic.main.activity_meditation.*

class MeditationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
    }


    override fun onBackPressed() {
        (fragment_meditaiton as MeditationFragment).showDialog()
    }
}
