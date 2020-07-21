package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.UserCourseEntity

interface UserCourseView : View {
    fun onSuccess(userCourseEntity: UserCourseEntity)
    fun onError(error: String)
}