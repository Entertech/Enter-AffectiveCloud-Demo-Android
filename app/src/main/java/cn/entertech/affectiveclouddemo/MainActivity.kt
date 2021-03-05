package cn.entertech.affectiveclouddemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothClass
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.core.app.ActivityCompat
import cn.entertech.affectiveclouddemo.app.Constant
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.model.TabEntity
import cn.entertech.affectiveclouddemo.ui.activity.BaseActivity
import cn.entertech.affectiveclouddemo.ui.fragment.*
import cn.entertech.affectiveclouddemo.utils.getAppVersionName
import cn.entertech.affectiveclouddemo.utils.isNewVersion
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
        initBleConnectedListener()
    }

    var bleConnectedListener = fun(mac:String){
        SettingManager.getInstance().bleMac = mac
    }

    fun initBleConnectedListener(){
        DeviceUIConfig.getInstance(this).managers[0].addConnectListener(bleConnectedListener)
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
            .request { allGranted, _, _ ->
                if (allGranted) {
                    initMta()
                    downloadFirmware()
                    checkAppUpdate()
                } else {
                }
            }
    }


    fun downloadFirmware() {
        var downloadlistener = object : FileDownloadListener() {
            override fun warn(task: BaseDownloadTask?) {
//                Log.d("#####", "download firmware warn:$task")
            }

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
//                Log.d("#####", "download firmware error:$e")
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

//                Log.d("#####", "download firmware progress:$soFarBytes")
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
        DeviceUIConfig.getInstance(this).updateFirmware(
            SettingManager.getInstance().serverFirmwareVersion,
            firmwareFilePath,
            false
        )
    }

    fun initMta() {
        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this.application)
        SettingManager.getInstance().serverFirmwareVersion =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_FIRMWARE_VERSION)
        SettingManager.getInstance().serverFirmwareUrl =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_FIRMWARE_URL)
        SettingManager.getInstance().serverAppVersion =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_APP_VERSION)
        SettingManager.getInstance().remoteConfigHelpCenter =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HELP_CENTER)
        SettingManager.getInstance().remoteConfigPressureReportInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_PRESSURE_REPORT_INFO)
        SettingManager.getInstance().remoteConfigAttentionRelaxationReportInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO)
        SettingManager.getInstance().remoteConfigHRVReportInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HRV_REPORT_INFO)
        SettingManager.getInstance().remoteConfigHRVRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HRV_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigHRReportInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HR_REPORT_INFO)
        SettingManager.getInstance().remoteConfigBrainReportInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_BRAIN_REPORT_INFO)
        SettingManager.getInstance().remoteConfigPressureRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_PRESSURE_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigRelaxationRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_RELAXATION_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigAttentionRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_ATTENTION_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigBrainRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_BRAIN_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigEEGRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_EEG_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigCoherenceRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_COHERENCE_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigHRRealtimeInfo =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_HR_REALTIME_INFO)
        SettingManager.getInstance().remoteConfigFlowtimeHeadhandIntro =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO)
        SettingManager.getInstance().remoteConfigDeviceCanNotConnect =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT)
        SettingManager.getInstance().remoteConfigTermsOfUser =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_TERMS_OF_USER)
        SettingManager.getInstance().remoteConfigPrivacy =
            StatConfig.getCustomProperty(this, Constant.REMOTE_CONFIG_PRIVACY)
    }

    fun checkAppUpdate(){
        var currentAppVersion = getAppVersionName(this)
        var serverAppVersion = SettingManager.getInstance().serverAppVersion
        if (isNewVersion(currentAppVersion,serverAppVersion)){
            showDialog()
        }
    }

    @SuppressLint("NewApi")
    fun showDialog() {
        var dialog =
            AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='${R.color.colorDialogTitle}'>App升级提示</font>",Html.FROM_HTML_MODE_LEGACY))
                .setMessage(Html.fromHtml("<font color='${R.color.colorDialogContent}'>有新的APP版本是否更新？</font>",Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(
                    Html.fromHtml("<font color='${R.color.colorDialogExit}'>确定</font>",Html.FROM_HTML_MODE_LEGACY)
                ) { dialog, _ ->
                    dialog.dismiss()
                    val uri = Uri.parse("market://details?id=$packageName")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .setNegativeButton(
                    Html.fromHtml("<font color='${R.color.colorDialogCancel}'>取消</font>",Html.FROM_HTML_MODE_LEGACY)
                ) { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialog.show()
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

    override fun onDestroy() {
        DeviceUIConfig.getInstance(this).managers[0].removeConnectListener(bleConnectedListener)
        super.onDestroy()
    }

}
