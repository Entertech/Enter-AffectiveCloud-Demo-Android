package cn.entertech.flowtimezh.mvp.model

data class StatisticsEntity(
    val active_days: String,
    val created_at: String,
    val current_streak: Int,
    val id: Int,
    val longest_streak: Int,
    val total_days: Int,
    val total_lessons: Int,
    val total_time: Int,
    val updated_at: String,
    val user: Int
)