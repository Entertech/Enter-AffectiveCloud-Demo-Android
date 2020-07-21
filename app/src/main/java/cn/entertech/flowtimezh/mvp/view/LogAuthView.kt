package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.LogAuthResult


interface LogAuthView :View{
    fun onSuccess(result: LogAuthResult?)
    fun onError(error:String)
}