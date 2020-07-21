package cn.entertech.flowtimezh.ui.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.location.LocationManager
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.utils.LogManager
import com.tbruyelle.rxpermissions2.RxPermissions


/**
 * Created by EnterTech on 2017/11/22.
 */
class DevicePermissionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setStatusBarLight()
        if (checkPermission()) {
            if (!SettingManager.getInstance().isConnectBefore) {
                startActivity(
                    Intent(
                        this,
                        DeviceTurnOnActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        DeviceStatusActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                )
            }
            finish()
            return
        }
        setContentView(R.layout.activity_device_permission)
//        initToolBar()
    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return (isLocationEnable() && RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION) && RxPermissions(
                this
            ).isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && BluetoothAdapter.getDefaultAdapter().isEnabled)
        } else {
            return isLocationEnable() && RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION) && BluetoothAdapter.getDefaultAdapter().isEnabled
        }
    }

    override fun onResume() {
        super.onResume()
        initViews()
    }

    fun initViews() {
        if (checkPermission()) {
            if (!SettingManager.getInstance().isConnectBefore) {
                startActivity(Intent(this, DeviceTurnOnActivity::class.java))
            } else {
                startActivity(Intent(this, DeviceActivity::class.java))
            }
            finish()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById<View>(R.id.permisson_location).visibility =
                if (isLocationEnable() && RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION) && RxPermissions(
                        this
                    ).isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                ) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        } else {
            findViewById<View>(R.id.permisson_location).visibility =
                if (isLocationEnable() && RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }


        findViewById<View>(R.id.permisson_ble).visibility =
            if (BluetoothAdapter.getDefaultAdapter().isEnabled) {
                View.GONE
            } else {
                View.VISIBLE
            }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_title).visibility = View.INVISIBLE
    }

    fun onLocation(view: View) {
        LogManager.getInstance()
            .logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        if (!isLocationEnable()) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        if (!RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            LogManager.getInstance()
                .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_APPLY)
            RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (!granted) {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_DENIED)
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_COARSE_LOCATION_GRANTED)
                        requestBackgroundLocation()
                    }
                }
        } else {
            requestBackgroundLocation()
        }
    }

    fun requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !RxPermissions(this).isGranted(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            LogManager.getInstance()
                .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_BACKGROUND_LOCATION_APPLY)
            RxPermissions(this).request(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .subscribe { granted ->
                    if (!granted) {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_BACKGROUND_LOCATION_DENIED)
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_PERMISSION_ACCESS_BACKGROUND_LOCATION_GRANTED)
                    }
                }
        } else {
            initViews()
        }
    }

    fun onBluetooth(view: View) {
        LogManager.getInstance()
            .logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    fun onConnect(view: View) {
        LogManager.getInstance()
            .logPost("Button $${currentActivity} ${Thread.currentThread().stackTrace[2].methodName}")
        startActivity(Intent(this, DeviceActivity::class.java))
    }

    fun isLocationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
    }
}