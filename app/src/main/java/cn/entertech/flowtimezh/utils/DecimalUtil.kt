package cn.entertech.flowtimezh.utils

fun formatNum(number: Float): Float? {
    return String.format("%.1f", number).toFloat()
}

fun getFormatNumSuffix(num: Int): String {
    return when (num % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}