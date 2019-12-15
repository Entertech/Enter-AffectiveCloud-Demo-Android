package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.entertech.flowtimezh.R
import kotlinx.android.synthetic.main.activity_person_info.*
import kotlinx.android.synthetic.main.layout_common_title.*

class PersonInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        initFullScreenDisplay()
        initView()
    }

    fun initView() {
        tv_menu_text.visibility = View.VISIBLE
        tv_menu_text.text = "下一步"
        tv_menu_text.setOnClickListener {
            if (et_num.text.toString() == "") {
                Toast.makeText(this@PersonInfoActivity, "请输入被试者编号!", Toast.LENGTH_SHORT).show()
            } else {
                var intent = Intent(this@PersonInfoActivity, MeditationActivity::class.java)
                intent.putExtra("userId", et_num.text.toString())
                var sex = ""
                if (rb_male.isChecked) {
                    sex = "m"
                } else {
                    sex = "f"
                }
                intent.putExtra("sex", sex)
                intent.putExtra("age", et_age.text.toString())
                startActivity(intent)
                finish()
            }
        }
        tv_title.visibility = View.VISIBLE
        tv_back.visibility = View.VISIBLE
        tv_title.text = "个人信息"
        ll_back.setOnClickListener {
            finish()
        }
    }
}
