package cn.entertech.flowtime.mvp.model

data class FirmwareVersionEntity(
    val md5: String,
    val update_notes: String,
    val url: String,
    val version: String
)