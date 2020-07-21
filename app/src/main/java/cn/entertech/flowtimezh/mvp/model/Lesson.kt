package cn.entertech.flowtimezh.mvp.model

data class Lesson(
    val `file`: String,
    val courses: List<Course>,
    val duration: String,
    val id: Int,
    val is_free: Boolean,
    val name: String,
    val order_in_course: Int
)