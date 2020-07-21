package cn.entertech.flowtimezh.utils

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import java.text.SimpleDateFormat

fun getCurrentTimeFormat(time :Long = System.currentTimeMillis()): String {
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return simpleDateFormat.format(time)
}
fun getExperienceStartTime(context: Context, recordId:Long): String {
    var userLessonRecordDao = UserLessonRecordDao(context)
    var record =
        userLessonRecordDao.findRecordById(SettingManager.getInstance().userId, recordId)
    var formatStartTime = record.startTime.replace("T", " ").replace("Z", "")
    return TimeUtils.getFormatTime(
        TimeUtils.getStringToDate(formatStartTime, "yyyy-MM-dd HH:mm:ss"),
        "MM.dd.yyyy"
    )
}