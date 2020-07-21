package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.CourseEntity


interface CourseListView : View {
    fun onSuccess(courseEntity: CourseEntity)
    fun onError(error: String)
}