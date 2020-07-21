package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.LessonEntity

interface LessonListView : View {
    fun onSuccess(lessonList: List<LessonEntity>)
    fun onError(error: String)
}