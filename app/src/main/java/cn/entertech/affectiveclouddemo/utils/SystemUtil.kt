package cn.entertech.affectiveclouddemo.utils

import android.content.Context
import android.util.Log


fun getAppVersionName(context: Context): String {
    var versionName = "";
    try {
        // ---get the package info---
        var pm = context.getPackageManager();
        var pi = pm.getPackageInfo(context.getPackageName(), 0);
        versionName = pi.versionName
        if (versionName == null || versionName.isEmpty()) {
            return ""
        }
    } catch (e: Exception) {
        Log.e("VersionInfo", "Exception", e)
    }
    return versionName;
}


fun getAppVersionCode(context: Context): Int {
    var versioncode = 0
    try {
        // ---get the package info---
        var pm = context.getPackageManager();
        var pi = pm.getPackageInfo(context.getPackageName(), 0);
        versioncode = pi.versionCode
    } catch (e: Exception) {
        Log.e("VersionInfo", "Exception", e)
    }
    return versioncode
}

/**
 * 通过文件名获取资源id
 *
 * @param variableName
 * @param c
 * @return
 */
fun <T> getResId(variableName: String, c: Class<T>): Int {
    try {
        var idField = c.getDeclaredField(variableName)
        return idField.getInt(idField)
    } catch (e: Exception) {
        e.printStackTrace()
        return -1
    }
}