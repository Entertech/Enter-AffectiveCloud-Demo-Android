package cn.entertech.flowtimezh.utils

import android.content.Context
import android.content.Intent
import cn.entertech.flowtimezh.app.Constant.Companion.FIRMWARE_PATH
import cn.entertech.flowtimezh.app.Constant.Companion.UPDATE_NOTES
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.ui.activity.DeviceUpdateActivity
import cn.entertech.flowtimezh.utils.reportfileutils.FileUtil.getFirmwareDir
import cn.entertech.flowtimezh.utils.reportfileutils.FileUtil.getFirmwareExtention
import java.io.File

fun isFirmwareFileExists(filePath: String): Boolean {
    var firmwareFile = File(filePath)
    return firmwareFile.exists()
}


fun toDeviceUpdate(context: Context) {
    var fileName = "firmware_${SettingManager.getInstance().bleFirmwareCloud}"
    var firmwareFilePath =
        getFirmwareDir() + File.separator + fileName + getFirmwareExtention()
    if (isNewVersion(
            SettingManager.getInstance().bleFirmware,
            SettingManager.getInstance().bleFirmwareCloud
        ) && isFirmwareFileExists(firmwareFilePath)
    ) {
        var intent = Intent(context, DeviceUpdateActivity::class.java).putExtra(
            FIRMWARE_PATH,
            firmwareFilePath
        )
        intent.putExtra(UPDATE_NOTES, SettingManager.getInstance().bleFirmwareCloudUpdateNotes)
        context.startActivity(intent)
    }
}