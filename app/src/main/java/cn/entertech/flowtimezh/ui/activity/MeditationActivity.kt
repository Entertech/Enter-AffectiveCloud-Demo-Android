package cn.entertech.flowtimezh.ui.activity

import android.animation.AnimatorSet
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.ContentObserver
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.entertech.affectivecloudsdk.*
import cn.entertech.affectivecloudsdk.entity.RecData
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.ble.cushion.CushionBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import cn.entertech.flowtime.utils.reportfileutils.*
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.*
import cn.entertech.flowtimezh.database.model.MeditationLabelsModel
import cn.entertech.flowtimezh.entity.MeditationEntity
import cn.entertech.flowtimezh.entity.MessageEvent
import cn.entertech.flowtimezh.entity.RecDataRecord
import cn.entertech.flowtimezh.entity.UserLessonEntity
import cn.entertech.flowtimezh.entity.meditation.*
import cn.entertech.flowtimezh.ui.fragment.MeditationFragment
import cn.entertech.flowtimezh.ui.service.AffectiveCloudService
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.ui.view.scrolllayout.ScrollLayout
import cn.entertech.flowtimezh.utils.*
import cn.entertech.flowtimezh.utils.reportfileutils.HexDump
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_meditation.*
import kotlinx.android.synthetic.main.activity_meditation.chronometer
import kotlinx.android.synthetic.main.activity_meditation.tv_experiment_name
import kotlinx.android.synthetic.main.activity_meditation.tv_record_btn
import kotlinx.android.synthetic.main.activity_meditation_labels_record.*
import kotlinx.android.synthetic.main.activity_meditation_time_record.*
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class MeditationActivity : BaseActivity() {
    private var serviceConnection: ServiceConnection? = null
    private var labelFileHelper: FileStoreHelper? = null
    private var bcgFileHelper: FileStoreHelper? = null
    private var gyroFileHelper: FileStoreHelper? = null
    private var cushionBleManager: CushionBleManager? = null
    private var meditationStatusPlayer: MeditationStatusPlayer? = null
    private var mContentObserver: SettingsContentObserver? = null
    private var connection: ServiceConnection? = null
    internal var affectiveCloudService: AffectiveCloudService? = null
    private var isRecordTime: Boolean = false
    private var userId: String = ""

    var animatorSet: AnimatorSet? = null
    private var meditaiton: MeditationEntity? = null
    private var userLessonEntity: UserLessonEntity? = null
    var handler: Handler = Handler()
    var lessonId: Int = 0
    var courseId: Int = 0
    var lessonName: String = ""
    var courseName: String? = ""
    lateinit var scrollLayout: ScrollLayout
    var meditationFragment: MeditationFragment? = null
//    var meditationEditFragment: MeditationEditFragment? = null

    //    var webSocketManager: WebSocketManager = WebSocketManager.getInstance()
    var fragmentBuffer = FragmentBuffer()
    var sessionId: String? = null
    var userLessonStartTime: String? = null
    var meditationStartTime: Long? = null
    var meditationEndTime: String? = null
    var loadingDialog: LoadingDialog? = null
    private var meditationId: Long = -1
    private var mRecordId: Long = -1

    private lateinit var labelRecordPath: String

    var isFirstReceiveData = true
    var isFirstReceiveHRData = true

    private var endTime: Long? = null
    private var startTime: Long? = null

    var needToCheckSensor = false

    var lastReceiveDataTimeMs: Long? = null

    private var wl: PowerManager.WakeLock? = null
    private var airSoundMediaPlayer: MediaPlayer? = null

    var isMeditationPause = true
    var isStartRecord = false
    var isBcgDataUpload = false
    var isGyroDataUpload = false

    //    var canExit = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setContentView(R.layout.activity_meditation)
        setStatusBarLight()
        meditationStartTime = System.currentTimeMillis()
        userLessonStartTime = getCurrentTimeFormat(meditationStartTime!!)
        meditationId = System.currentTimeMillis()
        loadingDialog = LoadingDialog(this)
        bindAffectiveService()
        initView()
        initBleManager()
        playAirSound()
        initPowerManager()
        playSleepNoise()
        mContentObserver = SettingsContentObserver(
            this, Handler(
                Looper.getMainLooper()
            ), ::onVoiceChange
        )
        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            mContentObserver
        )
        meditationStatusPlayer = MeditationStatusPlayer(this)
        labelRecordPath =
            getExternalFilesDir("label").path + File.separator + "${userLessonStartTime}"
        labelFileHelper = FileStoreHelper()
        bcgFileHelper = FileStoreHelper()
        gyroFileHelper = FileStoreHelper()
        labelFileHelper?.setPath(labelRecordPath, "labels")
        bcgFileHelper?.setPath(labelRecordPath, "bcg")
        EventBus.getDefault().register(this)
//        gyroFileHelper?.setPath(labelRecordPath,"gyro")
    }

    fun initAffectiveCloudManager() {
        affectiveCloudService?.addListener({
            runOnUiThread {
                if (it != null && it!!.realtimePEPRData != null) {
                    if (isFirstReceiveData) {
                        if (SettingManager.getInstance().timeCountIsEEG()) {
                            MeditationTimeManager.getInstance().timeReset()
                        }
                        meditationStartTime = System.currentTimeMillis()
                        meditationId = -System.currentTimeMillis()
                        Log.d("####", "meditation id is " + meditationId)
                        fragmentBuffer.fileName = getCurrentTimeFormat(meditationStartTime!!)
//                        FileStoreHelper.getInstance().setPath(
//                            MEDITATION_LABEL_RECORD_PATH,
//                            getCurrentTimeFormat(meditationStartTime!!)
//                        )
                        isFirstReceiveData = false
                    } else {
                        if (SettingManager.getInstance().timeCountIsEEG()) {
                            MeditationTimeManager.getInstance().timeIncrease()
                        }
                    }
                    meditationFragment?.showHeart(
                        it?.realtimePEPRData?.hr?.toInt(),
                        it?.realtimePEPRData?.hrv
                    )
                    meditationFragment?.showBcg(it.realtimePEPRData?.bcgWave)
                    meditationFragment?.showRw(it.realtimePEPRData?.rwWave)
                    meditationFragment?.showRwQuality(it.realtimePEPRData?.rwQuality)
                    meditationFragment?.showBcgQuality(it.realtimePEPRData?.bcgQuality)
                    meditationFragment?.showRR(it.realtimePEPRData?.rr)
                }

            }
        }, {
            runOnUiThread {
                meditationFragment?.showPressure(it?.realtimePressureData?.pressure?.toFloat())
                meditationFragment?.showCoherence(it?.realtimeCoherenceData?.coherence?.toFloat())
            }
        })
    }

    fun initDataFragment() {
        meditationFragment = MeditationFragment()
//        meditationEditFragment = MeditationEditFragment()
        meditationFragment?.setScrollTopListener(object : MeditationFragment.IScrollTopListener {
            override fun isScrollTop(flag: Boolean) {
                scrollLayout.setIsChildListTop(flag)
            }
        })
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        if (meditationFragment != null) {
            fragmentTransaction?.add(R.id.container, meditationFragment!!).commit()
//            fragmentTransaction?.add(R.id.container, meditationEditFragment!!)?.show(meditationFragment!!)
//                ?.hide(meditationEditFragment!!)
//                ?.commit()
        }
    }

    fun bindAffectiveService() {
        var serviceIntent = Intent(this, AffectiveCloudService::class.java)
        var userId = intent.getStringExtra("userId")
        var sex = intent.getStringExtra("sex")
        var age = intent.getStringExtra("age")
        var experimentDao = ExperimentDao(this)
        var modeDao = ExperimentModeDao(this)
        var experiment = experimentDao.findExperimentBySelected()
        var case = listOf(experiment.id).toIntArray()
        var mode = modeDao.findModeByExperimentId(experiment.id).map { it.id }.toIntArray()
        serviceIntent.putExtra("userId", userId)
        serviceIntent.putExtra("sex", sex)
        serviceIntent.putExtra("age", age)
        serviceIntent.putExtra("case", case)
        serviceIntent.putExtra("mode", mode)
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                affectiveCloudService = (service as AffectiveCloudService.MyBinder).service
                initAffectiveCloudManager()
                meditationFragment?.initNetListener()
                connectWebSocket()
            }

        }
        bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    fun unBindAffectiveService() {
        unbindService(serviceConnection!!)
    }

    fun onVoiceChange() {
        if (!isStartRecord) {
            Toast.makeText(this,"标签开始",Toast.LENGTH_SHORT).show()
            meditationStatusPlayer?.playRecordStartAudio()
            isStartRecord = true
            ll_back.visibility = View.GONE
            startTime = MeditationTimeManager.getInstance().currentTimeMs()
        } else {
            Toast.makeText(this,"标签结束",Toast.LENGTH_SHORT).show()
            meditationStatusPlayer?.playRecordEndAudio()
            ll_back.visibility = View.VISIBLE
            isStartRecord = false
            endTime = MeditationTimeManager.getInstance().currentTimeMs()
            var meditationLabelsDao = MeditationLabelsDao(this)
            var meditationLabelsModel = MeditationLabelsModel()
            meditationLabelsModel.id = System.currentTimeMillis()
            meditationLabelsModel.endTime = endTime!!
            meditationLabelsModel.startTime = startTime!!
            meditationLabelsModel.meditationId = meditationId
            meditationLabelsModel.meditationStartTime = meditationStartTime!!
            meditationLabelsDao.create(meditationLabelsModel)
        }
    }

    class SettingsContentObserver(context: Context, handler: Handler?, var callback: (() -> Unit)) :
        ContentObserver(handler) {
        private val audioManager: AudioManager
        override fun deliverSelfNotifications(): Boolean {
            return false
        }

        var lastVolume: Int? = null
        override fun onChange(selfChange: Boolean) {
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (lastVolume == null || currentVolume != lastVolume) {
                callback.invoke()
            }
            lastVolume = currentVolume
        }

        init {
            audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        }
    }

    var isToConnectDevice: Boolean = false

    var isSleep = false
    fun isSleep(sleepState: Int?) {
        if (sleepState == null) {
            return
        }
        if (!isSleep && sleepState == 1) {
            isSleep = true
            SoundScapeAudioManager.getInstance(this).pause()
        }
    }

    fun initPowerManager() {
        var pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My:sdf")
        wl?.acquire()
    }

    fun initView() {
        ll_back.setOnClickListener {
            ll_back.visibility = View.GONE
            isRecordTime = false
            ll_home_layout.visibility = View.VISIBLE
            ll_time_record_layout.visibility = View.GONE
        }
        btn_start_record.setOnClickListener {
            if (isBcgDataUpload) {
                initTimeRecordView()
                isRecordTime = true
            } else {
                Toast.makeText(this, "正在初始化采集...", Toast.LENGTH_LONG).show()
            }
        }
        btn_end_record.setOnClickListener {
            var meditationLabelsDao = MeditationLabelsDao(this@MeditationActivity)
            var meditationLabels = meditationLabelsDao.findByMeditationId(meditationId)
            if (meditationId == -1L || meditationLabels == null || meditationLabels.isEmpty()) {
                showExitDialog()
            } else {
                var intent = Intent(this, MeditationLabelsCommitActivity::class.java)
                intent.putExtra(EXTRA_MEDITATION_ID, meditationId)
                startActivity(intent)
            }
        }
        initTilte()
        initDataFragment()
        initScrollLayout()
    }

    private fun initScrollLayout() {
        scrollLayout = findViewById(R.id.sl_statistics_container)
        scrollLayout.setIsChildListTop(true)
        var coverView = findViewById<RelativeLayout>(R.id.rl_cover)
        scrollLayout.setCoverView(coverView)
        scrollLayout.setMinOffset(0)
        scrollLayout.setMaxOffset((ScreenUtil.getScreenHeight(this) * 0.5).toInt())
        scrollLayout.setIsSupportExit(true)
//        Log.d("###", "navigation height is " + ScreenUtil.getNavigationBarHeight(Application.getInstance()))
        scrollLayout.setExitOffset(
            ScreenUtil.dip2px(
                this,
                38f
            ) + ScreenUtil.getNavigationBarHeight(Application.getInstance())
        )
        scrollLayout.setTouchOffset(ScreenUtil.dip2px(this, 38f))
        scrollLayout.isAllowHorizontalScroll = true
        scrollLayout.setToOpen()
        scrollLayout.isDraggable = true
        scrollLayout.visibility = View.VISIBLE
        scrollLayout.setOnScrollChangedListener(object : ScrollLayout.OnScrollChangedListener {
            override fun onScrollProgressChanged(currentProgress: Float) {}

            override fun onScrollFinished(currentStatus: ScrollLayout.Status) {
                if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                    val myRotateAnimation_down = RotateAnimation(
                        180.0f,
                        +0.0f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    myRotateAnimation_down.duration = 300
                    myRotateAnimation_down.fillAfter = true
                    meditationFragment?.showMiniBar()
                } else if (currentStatus === ScrollLayout.Status.CLOSED) {
                    val myRotateAnimation_up = RotateAnimation(
                        0.0f,
                        +180.0f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    myRotateAnimation_up.duration = 300
                    myRotateAnimation_up.fillAfter = true
                    meditationFragment?.hideMiniBar()
                } else {
                    meditationFragment?.hideMiniBar()
                }

            }

            override fun onChildScroll(top: Int) {}
        })
        scrollLayout.setCoverView(findViewById<RelativeLayout>(R.id.rl_cover))
        if (cushionBleManager?.isConnected() == true) {
            scrollLayout.scrollToOpen()
        } else {
            scrollLayout.scrollToExit()
        }
    }

    fun connectWebSocket() {
        affectiveCloudService?.connectCloud(object : Callback {
            override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
                runOnUiThread {
                    Toast.makeText(
                        this@MeditationActivity,
                        "${error?.code}:${error?.msg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            override fun onSuccess() {
                Logger.d("affectivecloudmanager init success:")
//                    flowtimeBleManager?.startHeartAndBrainCollection()
            }
        })
    }

    fun initTimeRecordView() {
        ll_back.visibility = View.VISIBLE
        ll_home_layout.visibility = View.GONE
        ll_time_record_layout.visibility = View.VISIBLE
        var experimentDao = ExperimentDao(this)
        var experimentName = experimentDao.findExperimentBySelected().nameCn
        tv_experiment_name.text = experimentName
    }

    fun playSleepNoise() {
        var experimentDao = ExperimentDao(this)
        var experimentName = experimentDao.findExperimentBySelected().nameCn
        if (experimentName == "午休实验") {
            SoundScapeAudioManager.getInstance(this).setFile(R.raw.sleep_lightrain)
            SoundScapeAudioManager.getInstance(this).start()
        }
    }

    private fun resetPage() {
        ll_back.visibility = View.VISIBLE
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
//        chronometer.visibility = View.INVISIBLE
        tv_record_btn.setBackgroundResource(R.drawable.shape_time_record_start_bg)
        tv_record_btn.text = "开始记录"
    }

    private lateinit var rawDataListener: (ByteArray) -> Unit
    private lateinit var hrDataListener: (Int) -> Unit

    //    var brainDataList = ArrayList<Int>()
    fun initBleManager() {
        cushionBleManager = CushionBleManager.getInstance(this)
        rawDataListener = fun(data: ByteArray) {
            Log.d("raw data is", Arrays.toString(data))
//            var intArray = data.map { HexDump.converUnchart(it) }
//            var dataString = intArray
//                .toString().replace("[", "").replace("]", "").replace(" ", "") + ","
//            bcgFileHelper?.writeData(dataString)
//            MeditationTimeManager.getInstance().timeIncrease()
            isBcgDataUpload = true
            var currentTimeMs = System.currentTimeMillis()
            if (affectiveCloudService?.isInited() == true) {
                affectiveCloudService?.appendPEPRData(data)
            }
            if (lastReceiveDataTimeMs != null) {
                if ((currentTimeMs - lastReceiveDataTimeMs!!) >= 15000L) {
                    lastReceiveDataTimeMs = null
                    affectiveCloudService?.closeWebSocket()
                }
            }
        }

        cushionBleManager?.addRawDataListener(rawDataListener)
        cushionBleManager?.startCollection()
    }

    lateinit var reportMeditationData: ReportMeditationDataEntity

    var finishRunnable = {
        exitWithoutMeditation()
    }

    fun saveReportFile(
        reportMeditationData: ReportMeditationDataEntity,
        fileWriteComplete: ((String) -> Unit)?
    ) {
        fragmentBuffer.addFileWriteCompleteCallback(fileWriteComplete)
        fragmentBuffer.appendMeditationReport(
            reportMeditationData,
            meditationStartTime!!,
            MeditaionInterruptManager.getInstance().interruptTimestampList
        )
    }

    fun saveMeditationInDB(report: ReportMeditationDataEntity) {
        var meditationDao = MeditationDao(this)
        meditaiton = MeditationEntity()
        meditaiton!!.id = meditationId
        meditaiton!!.startTime = fragmentBuffer.fileName
        meditaiton!!.finishTime = meditationEndTime
        meditaiton!!.heartRateAvg = report.reportHRDataEntity!!.hrAvg!!.toFloat()
        meditaiton!!.heartRateMax = report.reportHRDataEntity!!.hrMax!!.toFloat()
        meditaiton!!.heartRateMin = report.reportHRDataEntity!!.hrMin!!.toFloat()
        meditaiton!!.heartRateVariabilityAvg = report.reportHRDataEntity!!.hrvAvg!!.toFloat()
        meditaiton!!.user = 0
//        var reportFileUri =
//            "${SettingManager.getInstance().userId}/${courseId}/${lessonId}/${fragmentBuffer.fileName}"
        meditaiton!!.meditationFile = fragmentBuffer.fileName

        var experimentDao = ExperimentDao(this)
        var experiment = experimentDao.findExperimentBySelected()
        if (experiment != null) {
            meditaiton!!.experimentId = experiment.id
        }
        meditaiton!!.experimentUserId = userId
        meditationDao.create(meditaiton)
    }

    fun saveUserLessonInDB() {
        var userLessonRecordDao = UserLessonRecordDao(this)
        userLessonEntity = UserLessonEntity()
        mRecordId = -System.currentTimeMillis()
        userLessonEntity!!.id = mRecordId
        userLessonEntity!!.lessonName = lessonName
        userLessonEntity!!.courseName = courseName
        userLessonEntity!!.lessonId = lessonId
        userLessonEntity!!.courseId = courseId
        userLessonEntity!!.startTime = userLessonStartTime
        userLessonEntity!!.finishTime = meditationEndTime
        userLessonEntity!!.user = 0
        userLessonEntity!!.meditation = meditationId
        userLessonRecordDao.create(userLessonEntity)
    }

    fun toDataActivity() {
        var intent = Intent(this, DataActivity::class.java)
        intent.putExtra(Constant.RECORD_ID, mRecordId)
        startActivity(intent)
        finish()
    }

    fun pauseMeditation() {
        isMeditationPause = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
//        var fragmentManager = supportFragmentManager
//        var fragmentTransaction = fragmentManager.beginTransaction()
        when (event.messageCode) {
            MessageEvent.MESSAGE_CODE_TO_DEVICE_CONNECT -> {
                if (cushionBleManager?.isConnected() == false) {
                    isToConnectDevice = true
                }
                if (!isMeditationPause) {
                    pauseMeditation()
                }
                scrollLayout.scrollToOpen()
                startActivity(Intent(this, DeviceManagerActivity::class.java))
            }
            MessageEvent.MESSAGE_CODE_TO_NET_RESTORE -> {
                if (affectiveCloudService!!.getSessionId() != null) {
                    affectiveCloudService?.restoreCloud(object : Callback {
                        override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
                        }

                        override fun onSuccess() {
                        }

                    })
                } else {
                    affectiveCloudService?.connectCloud(object : Callback {
                        override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
                        }

                        override fun onSuccess() {
//                            flowtimeBleManager.startHeartAndBrainCollection()
                        }

                    })
                }
            }
        }
    }


    fun showDialog() {
        var dialog = AlertDialog.Builder(this)
            .setTitle(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogTitle
                        )
                    }'>${getString(R.string.dialogExitTitle)}</font>"
                )
            )
            .setMessage(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogContent
                        )
                    }'>${getString(R.string.dialogExitContent)}</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogExit
                        )
                    }'>${getString(R.string.dialogExit)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
                finishMeditation()
            }
            .setNegativeButton(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogCancel
                        )
                    }'>${getString(R.string.dialogCancel)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    fun showErrorDialog() {
        var dialog = AlertDialog.Builder(this)
            .setTitle(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogTitle
                        )
                    }'>${getString(R.string.dialogExitTitle)}</font>"
                )
            )
            .setMessage(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogContent
                        )
                    }'>体验时间过短，是否继续退出？</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogExit
                        )
                    }'>${getString(R.string.dialogExit)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(
                Html.fromHtml(
                    "<font color='${
                        ContextCompat.getColor(
                            this,
                            R.color.colorDialogCancel
                        )
                    }'>${getString(R.string.dialogCancel)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    fun showExitDialog() {
        if (!isRecordTime) {
            meditationEndTime = getCurrentTimeFormat()
            if (!meditationTimeError()) {
                showDialog()
            }
        }
    }

    fun initTilte() {
//        iv_back.setImageResource(R.mipmap.ic_premium_close)
//        iv_back.setOnClickListener {
//            showDialog()
////            showExitView()
//        }
        tv_title.visibility = View.INVISIBLE
        rl_menu_ic.visibility = View.GONE
        rl_menu_ic.setOnClickListener {
            var meditationLabelsDao = MeditationLabelsDao(this@MeditationActivity)
            var meditationLabels = meditationLabelsDao.findByMeditationId(meditationId)
            if (meditationId == -1L || meditationLabels == null || meditationLabels.isEmpty()) {
                finishMeditation()
            } else {
                var intent = Intent(this, MeditationLabelsCommitActivity::class.java)
                intent.putExtra(EXTRA_MEDITATION_ID, meditationId)
                startActivity(intent)
            }
        }
    }

    fun getReportAndExit() {
        affectiveCloudService?.getBiodataReport {
            reportMeditationData = it
            exitWithMeditation(reportMeditationData)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (isRecordTime) {
            resetPage()
        } else {
            finishMeditation()
        }
    }

    fun finishMeditation() {
        meditationEndTime = getCurrentTimeFormat()
        var meditationLabelsDao = MeditationLabelsDao(this)
        var meditationLabels = meditationLabelsDao.findByMeditationId(meditationId)
        var experimentDimDao = ExperimentDimDao(this)
        var experimentTagDao = ExperimentTagDao(this)
        var recDatas = ArrayList<RecData>()
        if (meditationLabels != null && meditationLabels.isNotEmpty()) {
            for (meditationLabel in meditationLabels) {
                var recData = RecData()
                recData.note = listOf(meditationLabel.note)
                recData.st =
                    (meditationLabel.startTime) / 1000f
                recData.et =
                    (meditationLabel.endTime) / 1000f
                var tagMap = HashMap<String, Any>()
                var dimIdStrings = meditationLabel.dimIds.split(",")
                for (dimIdString in dimIdStrings) {
                    var dimIdInt = Integer.parseInt(dimIdString)
                    var dimModel = experimentDimDao.findByDimId(dimIdInt)
                    var dimValue = dimModel.value
                    var tag = experimentTagDao.findTagById(dimModel.tagId)
                    var tagNameEn = tag.nameEn
                    tagMap[tagNameEn] = dimValue
                }
                recData.tag = tagMap
                recDatas.add(recData)
                Log.d("####", "save label2222")
            }
            Log.d("####", "save label3333")
            saveLabelInLocal(recDatas)
        }
        Log.d("####", "save label4444")
//        saveMeditationInDB(reportMeditationData)
//        saveUserLessonInDB()
//        toDataActivity()
        startFinishTimer()
        reportMeditationData = ReportMeditationDataEntity()
        if (meditationTimeError() || !affectiveCloudService!!.isConnected()) {
            exitWithoutMeditation()
        } else {
            if (meditationLabels == null || meditationLabels.isEmpty()) {
                getReportAndExit()
            } else {
                loadingDialog?.loading("正在提交数标签...")
                affectiveCloudService?.submit(recDatas, object : Callback {
                    override fun onSuccess() {
                        runOnUiThread {
                            loadingDialog?.dismiss()
                            Toast.makeText(this@MeditationActivity, "标签提交成功！", Toast.LENGTH_SHORT)
                                .show()
                            getReportAndExit()
                        }
                    }

                    override fun onError(error: cn.entertech.affectivecloudsdk.entity.Error?) {
                        runOnUiThread {
                            loadingDialog?.dismiss()
                            Toast.makeText(
                                this@MeditationActivity,
                                "标签提交失败！${error.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            getReportAndExit()
                        }
                    }
                })
            }

        }
    }

    fun saveLabelInLocal(recDatas: List<RecData>) {
        var recDataRecord = RecDataRecord()
        recDataRecord.recDatas = recDatas
        recDataRecord.session_id = affectiveCloudService?.getSessionId()
        var json = Gson().toJson(recDataRecord)
        labelFileHelper?.writeData(json)
    }


    fun exitWithMeditation(reportMeditationData: ReportMeditationDataEntity) {
        handler.removeCallbacks(finishRunnable)
        saveReportFile(reportMeditationData, fun(filePath) {
            saveMeditationInDB(reportMeditationData)
            saveUserLessonInDB()
            toDataActivity()
        })
    }

    fun exitWithoutMeditation() {
        finish()
    }

    fun meditationTimeError(): Boolean {
        if (meditationStartTime == null) {
            return true
        } else {
            var meditationEndTime = System.currentTimeMillis()
            var duration = (meditationEndTime - meditationStartTime!!) / 1000 / 60
            if (duration < 1) {
                showErrorDialog()
                return true
            }
        }
        return false
    }
//    fun postRecord() {
//        SyncManager.getInstance().uploadRecord(userLessonEntity!!, meditaiton, fun() {
//            var messageEvent = MessageEvent()
//            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_REFRESH_RECORD
//            messageEvent.message = "refreshRecord"
//            EventBus.getDefault().post(messageEvent)
//        })
//        toDataActivity()
//    }

    fun startFinishTimer() {
        handler.postDelayed({
            finishRunnable
        }, 10000)
    }

    fun playAirSound() {
        airSoundMediaPlayer = MediaPlayer.create(
            this@MeditationActivity,
            R.raw.airsound
        )
        airSoundMediaPlayer?.setOnCompletionListener {
            airSoundMediaPlayer?.start()
        }
        airSoundMediaPlayer?.start()
    }

    fun stopAirSound() {
        airSoundMediaPlayer?.stop()
        airSoundMediaPlayer?.release()
    }

    override fun onDestroy() {
        unBindAffectiveService()
        cushionBleManager?.removeRawDataListener(rawDataListener)
        meditationStatusPlayer?.release()
        contentResolver.unregisterContentObserver(mContentObserver)
        SoundScapeAudioManager.getInstance(this).release()
        stopAirSound()
        wl?.release()
        handler?.removeCallbacks(finishRunnable)
        sessionId = null
        if (affectiveCloudService!!.isInited()) {
            affectiveCloudService?.release()
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
