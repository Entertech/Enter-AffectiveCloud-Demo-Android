package cn.entertech.flowtimezh.mvp.model

data class ReminderSettingEntity(
    val days_of_the_week: String,
    val enable: Boolean,
    val id: Int,
    val message: Int,
    val time: String,
    val user: Int
)