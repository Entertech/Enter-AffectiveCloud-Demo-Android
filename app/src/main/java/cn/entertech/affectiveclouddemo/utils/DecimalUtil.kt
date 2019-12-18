package cn.entertech.affectiveclouddemo.utils

fun formatNum(number: Float): Float? {
    return String.format("%.1f", number).toFloat()
}