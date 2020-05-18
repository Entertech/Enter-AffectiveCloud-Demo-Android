package cn.entertech.affectiveclouddemo.mvp.view
import cn.entertech.flowtime.mvp.model.FirmwareVersionEntity

interface FirmwareUpdateView : View {
    fun onSuccess(firmwareVersionEntity: List<FirmwareVersionEntity>?)
    fun onError(error: String)
}