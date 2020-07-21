package cn.entertech.flowtimezh.mvp.model

import com.google.gson.annotations.SerializedName

data class FirebaseRemoteConfigRate(
    val min_data_valid_ratio: Double,
    val min_meditation_time: Int,
    @SerializedName("min_r&a_value_1")
    val min_r_a_value_1: Int,
    @SerializedName("min_r&a_value_2")
    val min_r_a_value_2: Int
)