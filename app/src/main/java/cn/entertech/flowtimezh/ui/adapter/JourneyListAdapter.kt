package cn.entertech.flowtimezh.ui.adapter

import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.database.ExperimentDao
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.MeditationLabelsDao
import cn.entertech.flowtimezh.entity.UserLessonEntity
import cn.entertech.flowtimezh.utils.TimeUtils
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder


class JourneyListAdapter(data: List<UserLessonEntity>) :
    BaseItemDraggableAdapter<UserLessonEntity, BaseViewHolder>(R.layout.item_journey_list, data) {
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
        helper?.setText(R.id.tv_time_duration, "${timeDuration},${min}${mContext.getString(R.string.minute)}")
        var formatTime = TimeUtils.getFormatTime(
            TimeUtils.getStringToDate(
                item.startTime.replace("T", " ").replace("Z", ""),
                "yyyy-MM-dd HH:mm:ss"
            ), "yyyy.MM.dd EEEE"
        )
        helper?.setText(R.id.tv_date, formatTime)

        val meditationDao = MeditationDao(Application.getInstance())
        val meditation = meditationDao.findMeditationById(item.meditation)
        if (meditation != null){
            val deviceType = when(meditation.deviceType){
                DEVICE_TYPE_CUSHION -> mContext.getString(R.string.journey_list_device_type_cushion)
                DEVICE_TYPE_HEADBAND -> mContext.getString(R.string.journey_list_device_type_headband)
                DEVICE_TYPE_ENTERTECH_VR -> mContext.getString(R.string.journey_list_device_type_vr)
                else -> "--"
            }
            val userName = meditation.experimentUserId
            val meditationLabelsDao = MeditationLabelsDao(Application.getInstance())
            val meditationLabels = meditationLabelsDao.findByMeditationId(meditation.id)
            val segmentsCount = meditationLabels?.size?:0
            helper.setText(R.id.tv_user_name_and_segments,"${userName},${segmentsCount}${mContext.getString(R.string.label_segments)}")
            val experimentDao = ExperimentDao(Application.getInstance())
            val experimentModel = experimentDao.findExperimentById(meditation.experimentId)
            if (experimentModel != null){
                val experimentName = experimentModel.nameCn
                helper.setText(R.id.tv_experiment_name,"${experimentName},${deviceType}")
            }else{
                helper.setText(R.id.tv_experiment_name,"--,${deviceType}")
            }
        }else{
            helper.setText(R.id.tv_user_name_and_segments,"--,--${mContext.getString(R.string.label_segments)}")
            helper.setText(R.id.tv_experiment_name,"--,--")
        }
    }
}