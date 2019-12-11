package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.entity.UserLessonEntity
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.getResId
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class JourneyListAdapter(data: List<UserLessonEntity>) :
    BaseQuickAdapter<UserLessonEntity, BaseViewHolder>(R.layout.item_journey_list, data) {
    override fun convert(helper: BaseViewHolder, item: UserLessonEntity) {
        if (helper == null) {
            return
        }

        var finishTimeLong = TimeUtils.getStringToDate(
            item.finishTime.replace("T", " ").replace("Z", ""),
            "yyyy-MM-dd HH:mm:ss"
        )
        var startTimeLong = TimeUtils.getStringToDate(
            item.startTime.replace("T", " ").replace("Z", ""),
            "yyyy-MM-dd HH:mm:ss"
        )
        var duration = finishTimeLong - startTimeLong
        var min = String.format("%.1f", duration / 1000 / 60f)
        var timeDuration =
            "${TimeUtils.getFormatTime(startTimeLong, "HH:mma")}~${TimeUtils.getFormatTime(
                finishTimeLong,
                "HH:mma"
            )}"
        helper?.setText(R.id.tv_time_duration, timeDuration)
        var formatTime = TimeUtils.getFormatTime(
            TimeUtils.getStringToDate(
                item.startTime.replace("T", " ").replace("Z", ""),
                "yyyy-MM-dd HH:mm:ss"
            ), "dd.MM.yyyy EEEE"
        )
        helper?.setText(R.id.tv_date, formatTime)
        if (item.meditation == null) {
            helper?.setVisible(R.id.tv_feedback_flag, false)
        } else {
            var meditationDao = MeditationDao(mContext)
            var meditation = meditationDao.findMeditationById(item.meditation)
            var experimentDao = ExperimentDao(mContext)
            var experiment = experimentDao.findExperimentById(meditation.experimentId)
            helper?.setText(R.id.tv_duration, experiment.nameCn)
            if (meditation == null || meditation.meditationFile == null) {
                helper?.setVisible(R.id.tv_feedback_flag, false)
            } else {
                helper?.setVisible(R.id.tv_feedback_flag, true)
            }
        }
//        if (item.isSampleData) {
//            helper?.setVisible(R.id.tv_sample_data, true)
//        } else {
//            helper?.setVisible(R.id.tv_sample_data, false)
//        }
        if ((helper?.layoutPosition + 1)!! % 4 == 0) {
            helper?.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_red
            )
            helper?.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_red_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )

            helper?.setTextColor(
                R.id.tv_time_duration,
                mContext.resources.getColor(R.color.colorJourneyTextRed)
            )
            helper?.setTextColor(
                R.id.tv_duration,
                mContext.resources.getColor(R.color.colorJourneyTextRed)
            )
            helper?.setTextColor(
                R.id.tv_date,
                mContext.resources.getColor(R.color.colorJourneyTextRed)
            )
        } else if ((helper?.layoutPosition + 1)!! % 4 == 3) {
            helper?.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_blue
            )
            helper?.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_blue_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )

            helper?.setTextColor(
                R.id.tv_time_duration,
                mContext.resources.getColor(R.color.colorJourneyTextBlue)
            )
            helper?.setTextColor(
                R.id.tv_duration,
                mContext.resources.getColor(R.color.colorJourneyTextBlue)
            )
            helper?.setTextColor(
                R.id.tv_date,
                mContext.resources.getColor(R.color.colorJourneyTextBlue)
            )
        } else if ((helper?.layoutPosition + 1)!! % 4 == 2) {
            helper?.setBackgroundRes(
                R.id.item_bg,
                R.drawable.shape_record_bg_yellow
            )
            helper?.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_yellow_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )

            helper?.setTextColor(
                R.id.tv_time_duration,
                mContext.resources.getColor(R.color.colorJourneyTextYellow)
            )
            helper?.setTextColor(
                R.id.tv_duration,
                mContext.resources.getColor(R.color.colorJourneyTextYellow)
            )
            helper?.setTextColor(
                R.id.tv_date,
                mContext.resources.getColor(R.color.colorJourneyTextYellow)
            )
        } else {
            helper?.setBackgroundRes(R.id.item_bg, R.drawable.shape_record_bg_green)
            helper?.setBackgroundRes(
                R.id.rl_bg_cover,
                getResId(
                    "pic_journey_bg_green_${java.util.Random().nextInt(3) + 1}",
                    R.mipmap::class.java
                )
            )

            helper?.setTextColor(
                R.id.tv_time_duration,
                mContext.resources.getColor(R.color.colorJourneyTextGreen)
            )
            helper?.setTextColor(
                R.id.tv_duration,
                mContext.resources.getColor(R.color.colorJourneyTextGreen)
            )
            helper?.setTextColor(
                R.id.tv_date,
                mContext.resources.getColor(R.color.colorJourneyTextGreen)
            )
        }
    }
}