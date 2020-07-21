package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.NotificationTokenEntity

interface UserNotificationTokenView : View {
    fun onGetTokenSuccess(notificationTokens: List<NotificationTokenEntity>)
    fun onGeteTokenError(error:String)

    fun onPostTokenSuccess(notificationTokenEntity:NotificationTokenEntity)
    fun onPostTokenError(error:String)

    fun onUpdateTokenSuccess(notificationTokenEntity:NotificationTokenEntity)
    fun onUpdateTokenError(error:String)
}