package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.CourseEntity

interface TopCoursesView : View {
    fun onSuccess(courseEntity: CourseEntity)
    fun onError(error: String)
}