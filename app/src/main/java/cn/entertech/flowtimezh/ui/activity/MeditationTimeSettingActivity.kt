package cn.entertech.flowtimezh.ui.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.ui.adapter.MeditationEndSoundAdapter
import cn.entertech.flowtimezh.ui.adapter.MeditationTimeAdapter
import cn.entertech.flowtimezh.utils.ToastUtil
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_meditation_time_setting.*
import kotlinx.android.synthetic.main.layout_common_title.*
import java.util.*
import kotlin.collections.ArrayList

class MeditationTimeSettingActivity : BaseActivity() {
    private lateinit var adapter: MeditationTimeAdapter
    lateinit var pvCustomTime: TimePickerView
    var mEndSoundList: ArrayList<MeditationEndSoundAdapter.EndSound> = ArrayList()
    var mTimeList: ArrayList<MeditationTimeAdapter.Time> = ArrayList()
    var time = intArrayOf(10, 15, 20, 25, 30)
    var mediaPlayer:MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLight()
        setContentView(R.layout.activity_meditation_time_setting)
        initEndSound()
        initView()
    }

    fun initEndSound() {
        mEndSoundList.clear()
        var firstEndSound = MeditationEndSoundAdapter.EndSound("None")
        var secondEndSound = MeditationEndSoundAdapter.EndSound("Bell", R.raw.meditation_end_bell)
        var thirdEndSound = MeditationEndSoundAdapter.EndSound("Block", R.raw.meditation_end_block)
        var fourthEndSound = MeditationEndSoundAdapter.EndSound("Bowl", R.raw.meditation_end_bowl)
        var fivth = MeditationEndSoundAdapter.EndSound("Chime", R.raw.meditation_end_chime)
        mEndSoundList.add(firstEndSound)
        mEndSoundList.add(secondEndSound)
        mEndSoundList.add(thirdEndSound)
        mEndSoundList.add(fourthEndSound)
        mEndSoundList.add(fivth)
    }

    override fun onResume() {
        super.onResume()
    }
    fun initEndSoundList() {
        initEndSound()
        var adapter = MeditationEndSoundAdapter(mEndSoundList)
        rv_end_sound.adapter = adapter
        rv_end_sound.layoutManager = LinearLayoutManager(this)
        rv_end_sound.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                clearEndSoundListCheck()
                mEndSoundList[position].isSelected = true
                adapter?.notifyDataSetChanged()
                SettingManager.getInstance().meditationEndSoundIndex = position
                SettingManager.getInstance().meditationEndSoundRes = mEndSoundList[position]!!.resId!!
                if (SettingManager.getInstance().meditationEndSoundRes != 0){
                    mediaPlayer?.reset()
                    mediaPlayer = MediaPlayer.create(
                        this@MeditationTimeSettingActivity,
                        SettingManager.getInstance().meditationEndSoundRes
                    )
                    mediaPlayer?.setVolume(1f,1f)
                    mediaPlayer?.start()
                }
            }
        })
        clearEndSoundListCheck()
        mEndSoundList[SettingManager.getInstance().meditationEndSoundIndex].isSelected = true
        adapter.notifyDataSetChanged()
    }

    fun initTimeList() {
        for (time in time) {
            var timeEntity = MeditationTimeAdapter.Time(time)
            mTimeList.add(timeEntity)
        }
        adapter = MeditationTimeAdapter(mTimeList)
        rv_time.adapter = adapter
        rv_time.layoutManager = LinearLayoutManager(this)
        rv_time.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                clearTimeListCheck()
                mTimeList[position].isSelected = true
                adapter?.notifyDataSetChanged()
                SettingManager.getInstance().meditationTimeSelectedInex = position
                SettingManager.getInstance().isMeditationTimeReset = true
                SettingManager.getInstance().setIsCustomTime(false)
                hideCustomTime()
            }
        })
    }

    fun clearEndSoundListCheck() {
        for (endSound in mEndSoundList) {
            endSound.isSelected = false
        }
    }

    fun clearTimeListCheck() {
        for (time in mTimeList) {
            time.isSelected = false
        }
        adapter?.notifyDataSetChanged()
    }

    fun initView() {
        initTitle()
        initTimeList()
        initEndSoundList()
        initCustomTimePicker()
        initCurrentCheck()
    }

    fun initTitle() {
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text = "End Sound"
    }

    fun initCurrentCheck() {
        clearTimeListCheck()
        if (SettingManager.getInstance().isCustomTime) {
            setCustomTime(
                SettingManager.getInstance().meditationUnguideTime / 60,
                SettingManager.getInstance().meditationUnguideTime % 60
            )
        } else {
            hideCustomTime()
            mTimeList[SettingManager.getInstance().meditationTimeSelectedInex].isSelected = true
            adapter.notifyDataSetChanged()
        }
    }

    fun hideCustomTime() {
        tv_custom_time.visibility = View.INVISIBLE
        iv_custom_check.visibility = View.INVISIBLE
    }

    fun setCustomTime(hour: Int, min: Int) {
        tv_custom_time.visibility = View.VISIBLE
        iv_custom_check.visibility = View.VISIBLE
        if (hour == 0) {
            tv_custom_time.text = "${min} ${getString(R.string.min)}"
        } else if (hour > 1) {
            tv_custom_time.text = "${hour} ${getString(R.string.hours)} ${min} ${getString(R.string.min)}"
        } else {
            tv_custom_time.text = "${hour} ${getString(R.string.hour)} ${min} ${getString(R.string.min)}"
        }
    }

    private fun initCustomTimePicker() {
        rl_custom_time.setOnClickListener {
            pvCustomTime.show()
        }

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
        var meditationUnguideTime = SettingManager.getInstance().meditationUnguideTime
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
            if (date.hours * 60 + date.minutes < 5) {
                ToastUtil.toastShort(this@MeditationTimeSettingActivity, getString(R.string.meditation_time_lesson_5_min_tip))
            } else {
                pvCustomTime.dismiss()
                SettingManager.getInstance().meditationUnguideTime = date.hours * 60 + date.minutes
                SettingManager.getInstance().isMeditationTimeReset = true
                SettingManager.getInstance().setIsCustomTime(true)
                clearTimeListCheck()
                setCustomTime(date.hours, date.minutes)
            }
        }).setType(booleanArrayOf(false, false, false, true, true, false))
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.layout_time_pick_view_title) { v ->
                val tvCancel = v.findViewById<TextView>(R.id.tv_cancel)
                val tvConfirm = v.findViewById<TextView>(R.id.tv_confirm)
                tvConfirm.setOnClickListener {
                    pvCustomTime.returnData()
                }
                tvCancel.setOnClickListener { pvCustomTime.dismiss() }
            }
            .setContentTextSize(18)
            .setLabel("", "", "", "${getString(R.string.hours)}", "${getString(R.string.min)}", "")
            .setLineSpacingMultiplier(2f)
            .setTextXOffset(0, 0, 0, 40, 0, -40)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDecorView(rl_bg)
            .build()

    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

}
