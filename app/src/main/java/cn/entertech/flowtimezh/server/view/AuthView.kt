package cn.entertech.flowtimezh.server.view

import cn.entertech.flowtimezh.entity.AuthEntity

interface AuthView : View {
    fun onSuccess(authEntity: AuthEntity?)
    fun onError(error: String)
}