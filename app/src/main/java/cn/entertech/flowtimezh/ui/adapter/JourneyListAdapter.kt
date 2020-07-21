package cn.entertech.flowtimezh.ui.adapter

import android.app.Activity
import android.content.Intent
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.ui.activity.DataActivity
import cn.entertech.flowtimezh.ui.activity.RecordHistoryActivity
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.getResId
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder

class JourneyListAdapter(data: List<UserLessonEntity>) :
    BaseItemDraggableAdapter<UserLessonEntity, BaseViewHolder>(R.layout.item_journey_list, data) {
    private var mIsShowDeleteBtnListener: ((Boolean) -> Unit)? = null
    private var mIsEdit: Boolean = false
    override fun convert(helper: BaseViewHolder?, item: UserLessonEntity) {
        if (item.courseName == "unguide") {
            helper?.setText(R.id.tv_lesson_name, "Unguide Meditation")
        } else {
            if (item.lessonName.contains(". ")) {
                var formatArray = item.lessonName.split(". ")
                if (formatArray != null && formatArray.isNotEmpty()) {
                    var formatLessonName =
                        item.lessonName.subSequence(
                            formatArray[0].length + 2,
                            item.lessonName.length
                        )
                    helper?.setText(R.id.tv_lesson_name, formatLessonName)
                } else {
                    helper?.setText(R.id.tv_lesson_name, item.lessonName)
                }
            } else {
                helper?.setText(R.id.tv_lesson_name, item.lessonName)
            }
        }
        helper?.getView<LinearLayout>(R.id.ll_item)?.setOnClickListener {
            if (mIsEdit) {
                var isCheck = helper?.getView<CheckBox>(R.id.check_box).isChecked
                helper?.getView<CheckBox>(R.id.check_box).isChecked = !isCheck
            } else {
//                postButtonEvent(mContext as Activity, "1102", "报表列表界面 选择报表")
                var intent = Intent(mContext as Activity, DataActivity::class.java)
                intent.putExtra(Constant.RECORD_ID, item.id)
                (mContext as Activity).startActivity(intent)
            }
        }

        helper?.getView<RelativeLayout>(R.id.rl_check)?.setOnClickListener { }
        helper?.getView<CheckBox>(R.id.check_box)?.isChecked = item.isSelected
        helper?.getView<CheckBox>(R.id.check_box)
            ?.setOnCheckedChangeListener { buttonView, isChecked ->
                var isShowDeleteBtn = false
                data[helper.adapterPosition].isSelected = isChecked
                for (lesson in data) {
                    if (lesson.isSelected) {
                        isShowDeleteBtn = true
                    }
                }
                mIsShowDeleteBtnListener?.invoke(isShowDeleteBtn)
            }
        if (mIsEdit) {
            helper?.setGone(R.id.rl_check, true)
        } else {
            mIsShowDeleteBtnListener?.invoke(false)
            helper?.setGone(R.id.rl_check, false)
            helper?.setChecked(R.id.check_box, false)
        }
        var formatTime = TimeUtils.getFormatTime(
            TimeUtils.getStringToDate(
                item.startTime.replace("T", " ").replace("Z", ""),
                "yyyy-MM-dd HH:mm:ss"
            ), "HH:mm MM.dd.yyyy"
        )
        helper?.setText(R.id.tv_time, formatTime)

        var formatStartTIme = item?.startTime?.replace("T", " ")?.replace("Z", "")
        var startTime = TimeUtils.getFormatTime(
            TimeUtils.getStringToDate(formatStartTIme, "yyyy-MM-dd HH:mm:ss"),
            "MM.dd.yyyy"
        )
        var duration =
            TimeUtils.timeStampToMin(
                TimeUtils.getStringToDate(
                    item?.finishTime,
                    "yyyy-MM-dd HH:mm:ss"
                ) - TimeUtils.getStringToDate(
                    item?.startTime,
                    "yyyy-MM-dd HH:mm:ss"
                )
            )

        helper?.setText(R.id.tv_duration, "${duration}mins")
        if (item.meditation == null) {
            helper?.setVisible(R.id.tv_feedback_flag, false)
        } else {
            var userLessonRecordDao = UserLessonRecordDao(mContext)
            if (userLessonRecordDao.isFileExist(item)) {
                helper?.setVisible(R.id.tv_feedback_flag, true)
            } else {
                helper?.setVisible(R.id.tv_feedback_flag, false)
            }
        }
        if (helper == null || helper.layoutPosition == null) {
            return
        }
        if (item.isSampleData) {
            helper.setGone(R.id.tv_sample_data, true)
        } else {
            helper.setGone(R.id.tv_sample_data, false)
        }
        if ((helper?.layoutPosition + 1)!! % 4 == 0) {
            helper.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_red
            )
            helper.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_red_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )
            var textColor =
                (mContext as RecordHistoryActivity).getColorInDarkMode(
                    R.color.common_red2_color_light,
                    R.color.common_red2_color_dark
                )
            helper.setTextColor(R.id.tv_sample_data, textColor)
            helper.setTextColor(R.id.tv_time, textColor)
            helper.setTextColor(R.id.tv_lesson_name, textColor)
            helper.setTextColor(R.id.tv_duration, textColor)
        } else if ((helper?.layoutPosition + 1)!! % 4 == 3) {
            helper.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_blue
            )
            helper.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_blue_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )

            var textColor =
                (mContext as RecordHistoryActivity).getColorInDarkMode(
                    R.color.common_blue2_color_light,
                    R.color.common_blue2_color_dark
                )
            helper.setTextColor(
                R.id.tv_sample_data,
                textColor
            )
            helper.setTextColor(R.id.tv_time, textColor)
            helper.setTextColor(
                R.id.tv_lesson_name,
                textColor
            )
            helper.setTextColor(R.id.tv_duration, textColor)
        } else if ((helper?.layoutPosition + 1)!! % 4 == 2) {
            helper.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_yellow
            )
            helper.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_yellow_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )
            var textColor =
                (mContext as RecordHistoryActivity).getColorInDarkMode(
                    R.color.common_yellow2_color_light,
                    R.color.common_yellow2_color_dark
                )
            helper.setTextColor(
                R.id.tv_sample_data,
                textColor
            )
            helper.setTextColor(R.id.tv_time, textColor)
            helper.setTextColor(
                R.id.tv_lesson_name,
                textColor
            )
            helper.setTextColor(R.id.tv_duration, textColor)
        } else {
            helper.setBackgroundRes(R.id.item_bg, R.drawable.shape_record_bg_green)
            helper.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_green_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )
            var textColor =
                (mContext as RecordHistoryActivity).getColorInDarkMode(
                    R.color.common_green2_color_light,
                    R.color.common_green2_color_dark
                )

            helper.setTextColor(
                R.id.tv_sample_data,
                textColor
            )
            helper.setTextColor(R.id.tv_time, textColor)
            helper.setTextColor(
                R.id.tv_lesson_name,
                textColor
            )
            helper.setTextColor(R.id.tv_duration, textColor)
        }
    }

    fun isEdit(isEdit: Boolean, isShowDeleteBtnListener: ((Boolean) -> Unit)) {
        this.mIsShowDeleteBtnListener = isShowDeleteBtnListener
        this.mIsEdit = isEdit
        notifyDataSetChanged()
    }
}