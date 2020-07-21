package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.ui.adapter.MeditationTimeEntity
import cn.entertech.flowtimezh.ui.adapter.MeditationTimeSelectAdapter
import cn.entertech.flowtimezh.utils.ToastUtil
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_unguide_pre_settings.*
import kotlinx.android.synthetic.main.layout_common_title.*
import java.util.*
import kotlin.collections.ArrayList

class UnguidePreSettingsActivity : BaseActivity() {
    private var pvCustomTime: TimePickerView? = null
    private var adapter: MeditationTimeSelectAdapter? = null
    private var footView: View? = null
    var meditationTimes = ArrayList<MeditationTimeEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unguide_pre_settings)
        initFullScreenDisplay()
        initView()
    }

    fun initView() {
        initTitle()
        initCustomTimePicker()
        initList()
        initBtn()
    }

    fun initTitle(){
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = "Unguided"
    }

    override fun onResume() {
        super.onResume()
        initEndSoundView()
    }
    fun initEndSoundView(){
        var endSounds = listOf<String>("None","Bell","Block","Bowl","Chime")
        var selectedSounds = endSounds[SettingManager.getInstance().meditationEndSoundIndex]
        tv_end_sound.text = selectedSounds
    }

    fun initBtn(){
        btn_start.setOnClickListener {
            startActivity(Intent(this@UnguidePreSettingsActivity,MeditationWithoutGuideActivity::class.java))
            finish()
        }
        rl_end_sound_bg.setOnClickListener {
            startActivity(Intent(this@UnguidePreSettingsActivity,MeditationTimeSettingActivity::class.java))
        }
    }
    fun initList() {
        initTime()
        adapter = MeditationTimeSelectAdapter(meditationTimes)
        footView = LayoutInflater.from(this).inflate(R.layout.item_meditation_time_foot, null)
        initFootView()
        adapter?.addFooterView(footView)
//        footView.layoutParams.width = ScreenUtil.dip2px(activity!!, 140f)
//        footView.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        footView?.layoutParams?.height = ScreenUtil.dip2px(this!!, 104f)
        (footView?.layoutParams as LinearLayout.LayoutParams).setMargins(
            ScreenUtil.dip2px(this!!, 8f),
            ScreenUtil.dip2px(this!!, 16f)
            , ScreenUtil.dip2px(this!!, 8f)
            , ScreenUtil.dip2px(this!!, 0f)
        )
        rv_time_list.adapter = adapter
        rv_time_list.layoutManager = GridLayoutManager(this, 3)
        rv_time_list.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                for (i in meditationTimes.indices) {
                    meditationTimes[i].isSelected = false
                }
                meditationTimes[position].isSelected = true
                adapter?.notifyDataSetChanged()

                SettingManager.getInstance().setIsCustomMeditationTime(false)
                SettingManager.getInstance().meditationTime = meditationTimes[position].min!!
                customTimeItemUnselected()
            }
        })
    }

    fun initFootView() {
        footView?.setOnClickListener {
            for (i in meditationTimes.indices) {
                meditationTimes[i].isSelected = false
            }
            adapter?.notifyDataSetChanged()
            customTimeItemSelect()
            pvCustomTime?.show()
        }
        var customMeditationTime = SettingManager.getInstance().customMeditationTime
        if (customMeditationTime != 0) {
            var hour = customMeditationTime / 60
            var min = customMeditationTime - hour * 60
            setCustomTime(hour,min)
        }else{
            footView?.findViewById<LinearLayout>(R.id.ll_time_text)?.visibility = View.GONE
            footView?.findViewById<TextView>(R.id.tv_no_time_text)?.visibility = View.VISIBLE
        }
        if (SettingManager.getInstance().isCustomMeditationTime) {
            customTimeItemSelect()
        } else {
            customTimeItemUnselected()
        }
    }


    fun customTimeItemSelect() {
        footView?.findViewById<RelativeLayout>(R.id.rl_bg)
            ?.setBackgroundResource(R.drawable.shape_meditation_time_select)
        footView?.findViewById<TextView>(R.id.tv_no_time_text)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_hour)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_hour_unit)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_min)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_min_unit)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_small_custom_text)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_white_color_light,
                R.color.common_white_color_dark
            )
        )
    }

    fun customTimeItemUnselected() {
        footView?.findViewById<RelativeLayout>(R.id.rl_bg)
            ?.setBackgroundResource(R.drawable.shape_meditation_time_unselect)
        footView?.findViewById<TextView>(R.id.tv_no_time_text)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_hour)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_hour_unit)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_min)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_min_unit)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
        footView?.findViewById<TextView>(R.id.tv_small_custom_text)?.setTextColor(
            this.getColorInDarkMode(
                R.color.common_text_lv1_base_color_light,
                R.color.common_text_lv1_base_color_dark
            )
        )
    }

    fun initTime() {
        meditationTimes.clear()
        var currentMeditationTime = SettingManager.getInstance().meditationTime
        var times = listOf<Int>(10, 15, 20, 25, 30)
        for (time in times) {
            var meditationTime = MeditationTimeEntity()
            if (time == currentMeditationTime && !SettingManager.getInstance().isCustomMeditationTime) {
                meditationTime.isSelected = true
            }
            meditationTime.min = time
            meditationTimes.add(meditationTime)
        }

    }

    fun setCustomTime(hour:Int,min:Int){
        footView?.findViewById<LinearLayout>(R.id.ll_time_text)?.visibility = View.VISIBLE
        footView?.findViewById<TextView>(R.id.tv_no_time_text)?.visibility = View.GONE
        footView?.findViewById<TextView>(R.id.tv_hour)?.text = "$hour"
        footView?.findViewById<TextView>(R.id.tv_min)?.text = "$min"
    }

    private fun initCustomTimePicker() {
        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */

        var selectedDate = Calendar.getInstance()//系统当前时间
        var meditationUnguideTime = SettingManager.getInstance().customMeditationTime
        var hour = 0
        var min = 0
        if (meditationUnguideTime < 60) {
            min = meditationUnguideTime
        } else {
            hour = meditationUnguideTime / 60
            min = meditationUnguideTime % 60
        }
        setCustomTime(hour, min)
        var date = Date()
        date.hours = hour
        date.minutes = min
        selectedDate.time = date
        val startDate = Calendar.getInstance()
        startDate.set(2014, 1, 23, 0, 5)
        val endDate = Calendar.getInstance()
        endDate.set(2027, 2, 28, 23, 59)
        //时间选择器 ，自定义布局
        pvCustomTime = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            //选中事件回调
            if (date.hours * 60 + date.minutes < 3) {
                ToastUtil.toastShort(this@UnguidePreSettingsActivity, getString(R.string.meditation_time_lesson_5_min_tip))
            } else {
                pvCustomTime?.dismiss()
                SettingManager.getInstance().customMeditationTime = date.hours * 60 + date.minutes
                SettingManager.getInstance().setIsCustomMeditationTime(true)
                setCustomTime(date.hours, date.minutes)
            }
        }).setType(booleanArrayOf(false, false, false, true, true, false))
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.layout_time_pick_view_title) { v ->
                val tvCancel = v.findViewById<TextView>(R.id.tv_cancel)
                val tvConfirm = v.findViewById<TextView>(R.id.tv_confirm)
                tvConfirm.setOnClickListener {
                    pvCustomTime?.returnData()
                }
                tvCancel.setOnClickListener {
                    initList()
                    pvCustomTime?.dismiss() }
            }
            .setContentTextSize(18)
            .setLabel("", "", "", "${getString(R.string.hours)}", "${getString(R.string.min)}", "")
            .setLineSpacingMultiplier(2f)
            .setTextXOffset(0, 0, 0, 40, 0, -40)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDecorView(ll_bg)
            .build()

    }
}