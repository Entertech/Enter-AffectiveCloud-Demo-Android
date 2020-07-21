package cn.entertech.flowtimezh.mvp.model

data class CourseX(
    val authors: List<Any>,
    val description: String,
    val id: Int,
    val image: String,
    val is_free: Boolean,
    val lesson_count: Int,
    val lessons: List<Int>,
    val name: String,
    val tags: List<Any>
)