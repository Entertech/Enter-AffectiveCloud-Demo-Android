package cn.entertech.affectiveclouddemo.mvp.view

import cn.entertech.flowtime.mvp.model.LogAuthResult

interface LogAuthView :View{
    fun onSuccess(result: LogAuthResult?)
    fun onError(error:String)
}