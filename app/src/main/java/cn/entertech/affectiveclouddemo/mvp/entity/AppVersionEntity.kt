package cn.entertech.flowtime.mvp.model

data class AppVersionEntity(
    val enable: Int,
    val level: Int,
    val min_version: String,
    val platform: Int,
    val update_notes: String,
    val version: String
)