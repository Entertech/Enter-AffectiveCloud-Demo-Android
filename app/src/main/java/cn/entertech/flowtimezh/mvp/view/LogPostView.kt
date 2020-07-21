package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.LogPostResult


interface LogPostView :View{
    fun onSuccess(result: LogPostResult?)
    fun onError(error:String)
}