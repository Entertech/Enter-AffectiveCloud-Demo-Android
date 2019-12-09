package cn.entertech.flowtimezh.server.presenter

import cn.entertech.flowtimezh.server.view.View

interface Presneter{
    fun onCreate()
    fun onStart()
    fun onStop()
    fun attachView(view: View)
}