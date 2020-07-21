package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.StatisticsEntity


interface StatisticsView : View {
    fun onSuccess(statisticsEntity: StatisticsEntity?)
    fun onError(error: String)
}