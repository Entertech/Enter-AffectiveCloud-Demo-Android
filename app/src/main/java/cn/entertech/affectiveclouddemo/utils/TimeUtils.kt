package cn.entertech.affectiveclouddemo.utils

import java.text.SimpleDateFormat

fun getCurrentTimeFormat(time :Long = System.currentTimeMillis()): String {
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return simpleDateFormat.format(time)
}