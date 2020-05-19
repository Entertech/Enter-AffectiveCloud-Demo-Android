package cn.entertech.affectiveclouddemo

import android.Manifest
import android.bluetooth.BluetoothClass
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.model.TabEntity
import cn.entertech.affectiveclouddemo.ui.activity.BaseActivity
import cn.entertech.affectiveclouddemo.ui.fragment.*
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.flyco.tablayout.listener.CustomTabEntity
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.permissionx.guolindev.PermissionX
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.stat.StatConfig
import com.tencent.stat.StatService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    private lateinit var firmwareFilePath: String
    private lateinit var statisticsFragment: StatisticsFragment
    var mTabEntitys = ArrayList<CustomTabEntity>()
    private lateinit var homeFragment: HomeFragment
    private lateinit var journeyFragment: JourneyFragment
    private lateinit var meFragment: MeFragment
    var mFragments = ArrayList<androidx.fragment.app.Fragment>()
    var mIconSelected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_selected,
        R.mipmap.ic_tab_bar_lib_selected,
        R.mipmap.ic_tab_bar_me_selected
    )
    var mIconUnselected = arrayOf(
        R.mipmap.ic_tab_bar_foryou_unselected,
        R.mipmap.ic_tab_bar_lib_unselected,
        R.mipmap.ic_tab_bar_me_unselected
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFullScreenDisplay()
        setStatusBarLight()
        initView()
        initPermission()
        DeviceUIConfig.getInstance(this).updateFirmware(
            SettingManager.getInstance().serverFirmwareVersion,
            firmwareFilePath,
            false
        )
    }


    /**
     * Android6.0以上必须手动申请该权限
     */
    fun initPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Log.d("####", "is allGranted")
                    initMta()
                    downloadFirmware()
                } else {
                }
            }
    }


    fun downloadFirmware() {
        var downloadlistener = object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
                Log.d("#####", "download firmware warn:$task")
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                Log.d("#####", "download firmware error:$e")
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

                Log.d("#####", "download firmware progress:$soFarBytes")
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun completed(task: BaseDownloadTask?) {
            }
        }
        firmwareFilePath =
            "${this.getExternalFilesDir(null)}${File.separator}${SettingManager.getInstance().serverFirmwareVersion}.zip"
        var firmwareUrl =
            "${SettingManager.getInstance().serverFirmwareUrl}${SettingManager.getInstance().serverFirmwareVersion}"
        FileDownloader.setup(this)
        FileDownloader.getImpl().create(firmwareUrl)
            .setPath(firmwareFilePath)
            .setListener(downloadlistener)
            .start()
    }

    fun initMta() {
        StatConfig.setDebugEnable(false);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this.application)
        Log.d(
            "#######", "mta firmware is ${StatConfig.getCustomProperty(
                this,
                Constant.MTA_FIRMWARE_VERSION
            )}"
        )
        SettingManager.getInstance().serverFirmwareVersion =
            StatConfig.getCustomProperty(this, Constant.MTA_FIRMWARE_VERSION)
        SettingManager.getInstance().serverFirmwareUrl =
            StatConfig.getCustomProperty(this, Constant.MTA_FIRMWARE_URL)
        SettingManager.getInstance().serverAppVersion =
            StatConfig.getCustomProperty(this, Constant.MTA_APP_VERSION)
    }


    fun initView() {
        var mTitles = arrayOf(
            getString(R.string.home),
            getString(R.string.statistics),
            getString(R.string.me)
        )
        homeFragment = HomeFragment()
        statisticsFragment = StatisticsFragment()
        meFragment = MeFragment()
        mFragments.add(homeFragment)
        mFragments.add(statisticsFragment)
        mFragments.add(meFragment)
        for (i in 0..2) {
            mTabEntitys.add(TabEntity(mTitles[i], mIconSelected[i], mIconUnselected[i]))
        }
        ctl_main.setTabData(mTabEntitys, this, R.id.fl_container, mFragments)
    }

}
