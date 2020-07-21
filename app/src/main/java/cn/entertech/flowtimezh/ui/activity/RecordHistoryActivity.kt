package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import android.view.View
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.ui.fragment.JourneyFragment
import kotlinx.android.synthetic.main.layout_common_title.*

class RecordHistoryActivity : BaseActivity() {

    private var fragment: JourneyFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_history)
        initView()
    }

    fun initView() {
        initFullScreenDisplay()
        initTitle()
        fragment = JourneyFragment()
        supportFragmentManager.beginTransaction().add(R.id.fl_container, fragment!!).commit()
        tv_left_text.setOnClickListener {
            fragment!!.deleteAll()
        }
    }

    override fun onResume() {
        super.onResume()
//        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "报表列表界面", null)
    }

    fun initTitle() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = "Journey"
        tv_menu_text.visibility = View.VISIBLE
        tv_menu_text.text = "Edit"
        tv_menu_text.setOnClickListener {
            if (tv_menu_text.text == "Edit") {
                fragment!!.edit()
                tv_menu_text.text = "Cancel"
            } else {
                tv_left_text.visibility = View.GONE
                iv_back.visibility = View.VISIBLE
                fragment!!.cancel()
                tv_menu_text.text = "Edit"
            }
        }
    }
}
