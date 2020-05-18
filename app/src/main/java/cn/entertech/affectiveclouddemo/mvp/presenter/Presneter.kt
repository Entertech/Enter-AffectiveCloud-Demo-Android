package cn.entertech.affectiveclouddemo.mvp.presenter

import cn.entertech.affectiveclouddemo.mvp.view.View
interface Presneter{
    fun onCreate()
    fun onStart()
    fun onStop()
    fun attachView(view: View)
}