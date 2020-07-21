package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.AppVersionEntity


interface AppUpdateView : View {
    fun onSuccess(appVersionEntity: List<AppVersionEntity>?)
    fun onError(error: String)
}