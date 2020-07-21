package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity

interface PostUserLessonView : View {
    fun onPostMeditationSuccess(meditationEntity: MeditationEntity?)
    fun onPostMeditationFailed(error: String)
    fun onPutMeditaionSuccess(meditationEntity: MeditationEntity?)
    fun onPutMeditationFailed(error: String)
    fun onPostUserLessonSuccess(userLessonEntity: UserLessonEntity)
    fun onPostUserLessonFailed(error: String)


}