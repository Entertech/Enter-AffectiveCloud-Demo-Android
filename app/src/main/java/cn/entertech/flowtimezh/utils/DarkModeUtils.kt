package cn.entertech.flowtimezh.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color

fun getDarkModeColor(context: Context, light: String, dark: String): Int {
    if (getDarkModeStatus(context)) {
        return  Color.parseColor(dark)
    } else {
        return Color.parseColor(light)
    }
}

//检查当前系统是否已开启暗黑模式
fun getDarkModeStatus(context: Context): Boolean {
    val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}