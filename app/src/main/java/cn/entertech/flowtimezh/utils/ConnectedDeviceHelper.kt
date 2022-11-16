package cn.entertech.flowtimezh.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import cn.entertech.ble.cushion.CushionBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_ENTERTECH_VR
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_HEADBAND
import cn.entertech.flowtimezh.app.SettingManager
import com.tbruyelle.rxpermissions2.RxPermissions

enum class ConnectedDevice(var type: String) {
    NONE(""), HEADBAND("Headband"), CUSHION("Cushion"), HEADBAND_AND_CUSHION("Headband_Cushion"), ENTERTECH_VR(
        "Entertech_VR"
    )
}

class ConnectedDeviceHelper {
    companion object {
        const val CONNECT_TIMEOUT = 20000L
        fun currentConnectedDeviceType(): ConnectedDevice {
            if (BiomoduleBleManager.getInstance(Application.getInstance())
                    .isConnected() && CushionBleManager.getInstance(Application.getInstance())
                    .isConnected()
            ) {
                return ConnectedDevice.HEADBAND_AND_CUSHION
            } else if (BiomoduleBleManager.getInstance(Application.getInstance())
                    .isConnected()
            ) {
                return ConnectedDevice.HEADBAND
            } else if (CushionBleManager.getInstance(Application.getInstance())
                    .isConnected()
            ) {
                return ConnectedDevice.CUSHION
            } else {
                return ConnectedDevice.NONE
            }
        }

        fun startCollection() {
            if (BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
                BiomoduleBleManager.getInstance(Application.getInstance())
                    .startHeartAndBrainCollection()

            }
            if (CushionBleManager.getInstance(Application.getInstance()).isConnected()) {
                CushionBleManager.getInstance(Application.getInstance()).startCollection()

            }
        }

        fun stopCollection() {
            if (BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
                BiomoduleBleManager.getInstance(Application.getInstance())
                    .stopHeartAndBrainCollection()

            }
            if (CushionBleManager.getInstance(Application.getInstance()).isConnected()) {
                CushionBleManager.getInstance(Application.getInstance()).stopCollection()
            }
        }

//        fun isDeviceConnectedBefore(): Boolean {
//            return SettingManager.getInstance().isConnectHeadbandBefore || SettingManager.getInstance().isConnectCushionBefore
//        }

        fun disconnectAllDevice() {
            if (CushionBleManager.getInstance(Application.getInstance()).isConnected()) {
                CushionBleManager.getInstance(Application.getInstance()).disconnect()
            }
            if (BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
                BiomoduleBleManager.getInstance(Application.getInstance()).disConnect()
            }
        }

        fun disconnectHeadband() {
            if (BiomoduleBleManager.getInstance(Application.getInstance()).isConnected()) {
                BiomoduleBleManager.getInstance(Application.getInstance()).disConnect()
            }
        }

        fun disconnectCushion() {
            if (CushionBleManager.getInstance(Application.getInstance()).isConnected()) {
                CushionBleManager.getInstance(Application.getInstance()).disconnect()
            }
        }

        fun scanNearDeviceAndConnect(
            deviceType: String,
            scanSuccess: (String) -> Unit,
            scanError: (Exception, String) -> Unit,
            connectSuccess: (String, String) -> Unit,
            connectError: (String, String) -> Unit
        ) {
            if (deviceType == DEVICE_TYPE_CUSHION) {
                CushionBleManager.getInstance(Application.getInstance())
                    .scanNearDeviceAndConnect(fun() {
                        scanSuccess.invoke(deviceType)
                    }, fun(e: Exception) {
                        scanError.invoke(e, deviceType)
                    }, fun(mac: String) {
                        SettingManager.getInstance().bleCushionMac = mac
                        connectSuccess.invoke(mac, deviceType)
                    }, fun(error: String) {
                        connectError.invoke(error, deviceType)
                    })
            } else if (deviceType == DEVICE_TYPE_HEADBAND || deviceType == DEVICE_TYPE_ENTERTECH_VR) {
                BiomoduleBleManager.getInstance(Application.getInstance())
                    .scanNearDeviceAndConnect(fun() {
                        scanSuccess.invoke(deviceType)
                    }, fun(e: Exception) {
                        scanError.invoke(e, deviceType)
                    }, fun(mac: String) {
                        if (deviceType == DEVICE_TYPE_ENTERTECH_VR) {
                            SettingManager.getInstance().bleEntertechVRMac
                        } else {
                            SettingManager.getInstance().bleHeadbandMac
                        }
                        connectSuccess.invoke(mac, deviceType)
                    }, fun(error: String) {
                        connectError.invoke(error, deviceType)
                    })
            }

        }


        fun scanMacAndConnect(
            connectSuccess: (String, String) -> Unit,
            connectError: (String, String) -> Unit
        ) {
            val deviceType = SettingManager.getInstance().deviceType
            if (deviceType.isEmpty()) {
                return
            }
            if (deviceType == DEVICE_TYPE_CUSHION) {
                CushionBleManager.getInstance(Application.getInstance())
                    .scanMacAndConnect(
                        SettingManager.getInstance().bleCushionMac,
                        CONNECT_TIMEOUT,
                        fun(mac) {
                            connectSuccess.invoke(mac, deviceType)
                        },
                        fun(error: String) {
                            connectError.invoke(error, deviceType)
                        })
            } else {
                val mac =
                    if (deviceType == DEVICE_TYPE_ENTERTECH_VR) {
                        SettingManager.getInstance().bleEntertechVRMac
                    } else {
                        SettingManager.getInstance().bleHeadbandMac
                    }
                BiomoduleBleManager.getInstance(Application.getInstance())
                    .scanMacAndConnect(
                        mac,
                        CONNECT_TIMEOUT,
                        fun(mac) {
                            connectSuccess.invoke(mac, deviceType)
                        },
                        fun(error: String) {
                            connectError.invoke(error, deviceType)
                        })
            }

        }

    }
}