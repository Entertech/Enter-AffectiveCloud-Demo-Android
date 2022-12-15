package cn.entertech.flowtimezh.entity

data class Dim(
    val desc: String,
    val gmt_create: String,
    val gmt_modify: String,
    val id: Int,
    val name: String,
    val user: Int,
    val values: List<Value>
)