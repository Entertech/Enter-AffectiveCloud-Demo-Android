package cn.entertech.flowtimezh.ui.activity

import android.os.Bundle
import cn.entertech.flowtimezh.R

class LabSelectActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_select)
        initFullScreenDisplay()
    }
}