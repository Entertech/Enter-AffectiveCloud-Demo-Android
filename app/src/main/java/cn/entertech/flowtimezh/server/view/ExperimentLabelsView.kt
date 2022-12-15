package cn.entertech.flowtimezh.server.view

import cn.entertech.flowtimezh.entity.LabelsEntityV2

interface ExperimentLabelsView : View {
    fun onSuccess(labelEntity: List<LabelsEntityV2>?)
    fun onError(error: String)
}