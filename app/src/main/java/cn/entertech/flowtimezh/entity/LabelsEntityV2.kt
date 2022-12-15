package cn.entertech.flowtimezh.entity

data class LabelsEntityV2(
    val app: Int,
    val desc: String,
    val dims: List<Dim>,
    val gmt_create: String,
    val gmt_modify: String,
    val id: Int,
    val name: String
)