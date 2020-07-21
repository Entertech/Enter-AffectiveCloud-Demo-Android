package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity


interface GetUserLessonView : View {
    fun onGetUserLessonsSuccess(userLessons: List<UserLessonEntity>)
    fun onGetUserLessonsFailed(error: String)

    fun onGetMeditationSuccess(meditationEntity: MeditationEntity)
    fun onGetMeditationFailed(error:String)
}