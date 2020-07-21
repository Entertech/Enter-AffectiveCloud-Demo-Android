package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.AuthSocialEntity


interface AuthSocialView : View {
    fun onSuccess(authSocialEntity: AuthSocialEntity)
    fun onError(error: String)
}