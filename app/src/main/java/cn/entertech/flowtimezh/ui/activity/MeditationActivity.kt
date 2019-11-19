package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.ui.fragment.MeditationFragment
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
