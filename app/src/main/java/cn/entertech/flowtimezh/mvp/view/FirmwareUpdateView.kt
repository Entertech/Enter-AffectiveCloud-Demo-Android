package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.FirmwareVersionEntity

interface FirmwareUpdateView : View {
    fun onSuccess(firmwareVersionEntity: List<FirmwareVersionEntity>?)
    fun onError(error: String)
}