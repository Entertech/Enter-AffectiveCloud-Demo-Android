package cn.entertech.flowtimezh.mvp.presenter

import cn.entertech.flowtimezh.mvp.view.View
interface Presneter{
    fun onCreate()
    fun onStart()
    fun onStop()
    fun attachView(view: View)
}