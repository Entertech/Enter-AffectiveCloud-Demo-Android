package cn.entertech.flowtimezh.mvp.view

import okhttp3.ResponseBody

interface DeleteUserLessonView : View {
    fun onSuccess(result: ResponseBody?,lessonId:Long)
    fun onError(error: String)
}