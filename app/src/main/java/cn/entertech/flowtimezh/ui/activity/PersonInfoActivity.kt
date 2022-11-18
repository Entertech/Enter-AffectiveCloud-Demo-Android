package cn.entertech.flowtimezh.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.ui.fragment.AgeSelectFragment
import kotlinx.android.synthetic.main.activity_person_info.*
import kotlinx.android.synthetic.main.layout_common_title.*

class PersonInfoActivity : BaseActivity() {
    var sex = ""
    var age = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        initFullScreenDisplay()
        initView()
    }

    fun initView() {
        btn_gender.setOnClickListener {
            showMenu(it,R.menu.gender)
        }
        val ageSelectFragment = AgeSelectFragment()
        ageSelectFragment.setItems(initAgeList()){
            age = it
            btn_age.text = age
        }
        btn_age.setOnClickListener {
            ageSelectFragment.show(supportFragmentManager,AgeSelectFragment.TAG)
        }
        btm_next.setOnClickListener {
            if (et_num.text.toString() == "") {
                Toast.makeText(this@PersonInfoActivity, "请输入被试者编号!", Toast.LENGTH_SHORT).show()
            } else {
                var intent = Intent(this@PersonInfoActivity, MeditationActivity::class.java)
                intent.putExtra("userId", et_num.text.toString())
                intent.putExtra("sex", sex)
                intent.putExtra("age", age)
                startActivity(intent)
                finish()
            }
        }
    }

    fun initAgeList():List<String>{
        val list = mutableListOf<String>()
        for (i in 1..200){
            list.add("$i")
        }
        return list
    }

    @SuppressLint("RestrictedApi")
    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(this, v)
        val menu = popup.menu
        popup.menuInflater.inflate(menuRes, menu)
        if (popup.menu is MenuBuilder) {
            val m = popup.menu as MenuBuilder
            m.setOptionalIconsVisible(true)
        }
//        for (i in 0 until popup.menu.size()) {
//            val drawable: Drawable = menu.getItem(i).getIcon()
//            if (drawable != null) {
//                drawable.mutate()
//                drawable.colorFilter =
//                    PorterDuffColorFilter(mMenuItemIconColor, PorterDuff.Mode.SRC_ATOP)
//            }
//        }

        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.gender_female -> {
                        popup.dismiss()
                        toFemaleSelect()
                    }
                    R.id.gender_male -> {
                        popup.dismiss()
                        toMaleSelect()
                    }
                }
                return true
            }

        })
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    fun toFemaleSelect(){
        sex = "f"
        btn_gender.text = "女"
    }
    fun toMaleSelect(){
        sex = "m"
        btn_gender.text = "男"
    }
}
