package cn.entertech.flowtimezh.mvp.model

import com.google.gson.annotations.SerializedName

data class FirebaseRemoteConfigShare(
    val course_page_share_conditions: CoursePageShareConditions,
    val hrv_page_share_conditions: HrvPageShareConditions,
    val pressure_page_share_conditions: PressurePageShareConditions,
    @SerializedName("r&a_page_share_conditions")
    val r_a_page_share_conditions: RaPageShareConditions,
    val report_page_share_conditions: ReportPageShareConditions
)

data class CoursePageShareConditions(
    val min_meditation_time: Int,
    val min_meditation_total_time: Int
)

data class HrvPageShareConditions(
    val min_data_valid_ratio: Double
)

data class PressurePageShareConditions(
    val max_pressure_value: Int,
    val min_data_valid_ratio: Double
)

data class RaPageShareConditions(
    val min_data_valid_ratio: Double,
    @SerializedName("min_r&a_value")
    val min_r_a_value: Int
)

data class ReportPageShareConditions(
    val max_pressure_value: Int,
    val min_hr: Int,
    val min_meditation_time: Int,
    @SerializedName("min_r&a_value")
    val min_r_a_value: Int
)