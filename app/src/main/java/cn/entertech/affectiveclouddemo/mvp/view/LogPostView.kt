package cn.entertech.affectiveclouddemo.mvp.view

import cn.entertech.flowtime.mvp.model.LogPostResult

interface LogPostView :View{
    fun onSuccess(result: LogPostResult?)
    fun onError(error:String)
}