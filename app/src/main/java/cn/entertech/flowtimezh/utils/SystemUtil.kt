package cn.entertech.flowtimezh.utils

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


fun isNewVersion(localVersion: String, cloudVersion: String): Boolean {
    var cloudVersions = cloudVersion.split(".")
    var localVersions = localVersion.split(".")
    if (cloudVersions != null && cloudVersions.size == 3 && localVersions != null && localVersions.size == 3) {
        var cloudVersionMajor = cloudVersions[0]
        var cloudVersionMinor = cloudVersions[1]
        var cloudVersionPatch = cloudVersions[2]
        var localVersionsMajor = localVersions[0]
        var localVersionsMinor = localVersions[1]
        var localVersionsPatch = localVersions[2]
        if (cloudVersionMajor > localVersionsMajor) {
            return true
        } else if (cloudVersionMajor == localVersionsMajor) {
            if (cloudVersionMinor > localVersionsMinor) {
                return true
            } else if (cloudVersionMinor == localVersionsMinor) {
                if (cloudVersionPatch > localVersionsPatch) {
                    return true
                }
            }
        }
    }
    return false
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