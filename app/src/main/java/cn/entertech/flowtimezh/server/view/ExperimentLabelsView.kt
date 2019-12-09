package cn.entertech.flowtimezh.server.view

import cn.entertech.flowtimezh.entity.LabelsEntity

interface ExperimentLabelsView : View {
    fun onSuccess(labelEntity: List<LabelsEntity>?)
    fun onError(error: String)
}