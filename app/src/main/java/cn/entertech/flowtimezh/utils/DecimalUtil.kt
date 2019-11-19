package cn.entertech.flowtimezh.utils

fun formatNum(number: Float): Float? {
    return String.format("%.1f", number).toFloat()
}