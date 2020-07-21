package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.AuthorEntity

interface AuthorInfoView : View {
    fun onSuccess(authorEntity: AuthorEntity)
    fun onError(error: String)
}