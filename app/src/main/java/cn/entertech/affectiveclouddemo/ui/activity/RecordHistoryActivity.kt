package cn.entertech.affectiveclouddemo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.entertech.affectiveclouddemo.R
import kotlinx.android.synthetic.main.layout_common_title.*

class RecordHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_history)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
    }

    private fun initView() {
        initTitle()
    }

    fun initTitle() {
        iv_back.visibility = View.VISIBLE
        iv_back.setOnClickListener {
            finish()
        }
    }
}
