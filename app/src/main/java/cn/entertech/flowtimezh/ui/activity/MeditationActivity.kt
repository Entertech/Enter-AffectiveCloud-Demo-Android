package cn.entertech.flowtimezh.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.entertech.affectivecloudsdk.*
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RecData
import cn.entertech.affectivecloudsdk.entity.Service
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.affectivecloudsdk.interfaces.Callback2
import cn.entertech.affectivecloudsdk.utils.ConvertUtil
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import cn.entertech.bleuisdk.ui.activity.DeviceManagerActivity
import cn.entertech.flowtime.utils.reportfileutils.*
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_ID
import cn.entertech.flowtimezh.app.Constant.Companion.EXTRA_MEDITATION_START_TIME
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.*
import cn.entertech.flowtimezh.entity.MeditationEntity
import cn.entertech.flowtimezh.entity.MessageEvent
import cn.entertech.flowtimezh.entity.RecDataRecord
import cn.entertech.flowtimezh.entity.UserLessonEntity
import cn.entertech.flowtimezh.entity.meditation.*
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.ui.activity.DataActivity
import cn.entertech.flowtimezh.ui.fragment.MeditationFragment
import cn.entertech.flowtimezh.ui.service.AffectiveCloudService
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.ui.view.scrolllayout.ScrollLayout
import cn.entertech.flowtimezh.utils.*
import cn.entertech.uicomponentsdk.utils.dp
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_meditation.*
import kotlinx.android.synthetic.main.activity_meditation.chronometer
import kotlinx.android.synthetic.main.activity_meditation.tv_experiment_name
import kotlinx.android.synthetic.main.activity_meditation.tv_record_btn
import kotlinx.android.synthetic.main.activity_meditation_time_record.*
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class MeditationActivity : BaseActivity() {
    private var connection: ServiceConnection? = null
    internal var affectiveCloudService: AffectiveCloudService? = null
    private var isRecordTime: Boolean = false
    private var userId: String = ""

    var animatorSet: AnimatorSet? = null
    var biomoduleBleManager: MultipleBiomoduleBleManager =
        DeviceUIConfig.getInstance(Application.getInstance()).managers[0]
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

    private var MEDITATION_LABEL_RECORD_PATH =
        Environment.getExternalStorageDirectory().path + File.separator + "心流实验/标签数据"

    var isFixingFirmware = false
    var isFirstReceiveData = true
    var isFirstReceiveHRData = true

    private var endTime: Long? = null
    private var startTime: Long? = null

    var isCheckContact = false
    var isSensorCheckShow = false
    var contactWellCount = 0
    var isContactWell = false
    var needToCheckSensor = false
    var isTimeEnd = false

    var lastReceiveDataTimeMs: Long? = null

    private var wl: PowerManager.WakeLock? = null
    private var airSoundMediaPlayer: MediaPlayer? = null

    var isMeditationPause = true

    //    var canExit = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        initFullScreenDisplay()
        setStatusBarLight()
        EventBus.getDefault().register(this)
        userLessonStartTime = getCurrentTimeFormat()
        loadingDialog = LoadingDialog(this)
        bindAffectiveService()
        initView()
        initFlowtimeManager()
        playAirSound()
        initPowerManager()
        playSleepNoise()
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
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                affectiveCloudService = (service as AffectiveCloudService.MyBinder).service
                initAffectiveCloudManager()
                meditationFragment?.initNetListener()
            }

        }
        bindService(serviceIntent, connection!!, Context.BIND_AUTO_CREATE)
    }

    fun initSensorCheckDialog() {
        initAnimate()
        iv_skip.setOnClickListener {
            isCheckContact = false
            card_2.visibility = View.GONE
            card_3.visibility = View.VISIBLE
            sensor_check_animate_layout.toSecondPage()
        }
        btn_begin.setOnClickListener {
            isCheckContact = false
            hideSensorCheckDialog()
        }
        btn_skip_cancel.setOnClickListener {
            isCheckContact = true
            sensor_check_animate_layout.toFirstPage()
        }
        tv_skip_ok.setOnClickListener {
            isCheckContact = false
            hideSensorCheckDialog()
        }
        rl_get_good_signal.setOnClickListener {
            startActivity(
                Intent(
                    this@MeditationActivity,
                    SensorContactCheckActivity::class.java
                )
            )
            if (!isMeditationPause) {
                pauseMeditation()
            }
        }
    }

    fun initAnimate() {
        var currentDialogY = sensor_check_animate_layout.translationY
        var dialogAnimator = ObjectAnimator.ofFloat(
            sensor_check_animate_layout,
            "translationY",
            currentDialogY,
            currentDialogY + 434f.dp()
        )
        dialogAnimator.duration = 1
        dialogAnimator.interpolator = AccelerateInterpolator()
        dialogAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (biomoduleBleManager.isConnected()) {
                    if (!isSensorCheckShow) {
                        showSensorCheckDialog()
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        dialogAnimator.start()
    }

    fun showSensorCheckDialog() {
        if (biomoduleBleManager.isConnected()) {
            biomoduleBleManager.startHeartAndBrainCollection()
        }
        rl_cover_sensor.visibility = View.VISIBLE
        rl_cover_sensor.setOnClickListener {
        }
        var currentY = sl_statistics_container.translationY
        var objectAnimation = ObjectAnimator.ofFloat(
            sl_statistics_container,
            "translationY",
            currentY,
            currentY + sl_statistics_container.screenHeight - sl_statistics_container.top
        )
        objectAnimation.duration = 300
        objectAnimation.interpolator = AccelerateDecelerateInterpolator()
        var currentDialogY = sensor_check_animate_layout.translationY
        var dialogAnimator = ObjectAnimator.ofFloat(
            sensor_check_animate_layout,
            "translationY",
            currentDialogY,
            currentDialogY - 434f.dp()
        )
        dialogAnimator.duration = 300
        dialogAnimator.interpolator = AccelerateDecelerateInterpolator()
        dialogAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                isCheckContact = true
                isSensorCheckShow = true
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        animatorSet = AnimatorSet()
        animatorSet?.play(objectAnimation)?.before(dialogAnimator)
        animatorSet?.start()
    }


    fun hideSensorCheckDialog() {
        rl_cover_sensor.visibility = View.GONE
        var currentY = sl_statistics_container.translationY
        var objectAnimation = ObjectAnimator.ofFloat(
            sl_statistics_container,
            "translationY",
            currentY,
            0f
        )
        objectAnimation.duration = 300
        objectAnimation.interpolator = AccelerateDecelerateInterpolator()
        var currentDialogY = sensor_check_animate_layout.translationY
        var dialogAnimator = ObjectAnimator.ofFloat(
            sensor_check_animate_layout,
            "translationY",
            currentDialogY,
            currentDialogY + 434f.dp()
        )
        dialogAnimator.duration = 300
        dialogAnimator.interpolator = AccelerateDecelerateInterpolator()
        animatorSet = AnimatorSet()
        animatorSet?.play(dialogAnimator)?.before(objectAnimation)
        animatorSet?.start()
        isSensorCheckShow = false
        needToCheckSensor = false
        if (affectiveCloudService != null && !affectiveCloudService!!.isInited() && !affectiveCloudService!!.isConnected()) {
            connectWebSocket()
        }
    }

    fun resumeMeditation() {
        isMeditationPause = false
        biomoduleBleManager.startHeartAndBrainCollection()
    }

    fun pauseMeditation() {
        isMeditationPause = true
        biomoduleBleManager.stopHeartAndBrainCollection()
    }

    var mMainHanlder = Handler()
    var isToConnectDevice: Boolean = false
    override fun onResume() {
        super.onResume()
        if (isToConnectDevice && biomoduleBleManager.isConnected()) {
            mMainHanlder.postDelayed(Runnable {
                showSensorCheckDialog()
                isToConnectDevice = false
            }, 1000)
        }
        if (!needToCheckSensor && isMeditationPause) {
            resumeMeditation()
        }
    }

    fun connectWebSocket() {
        if (biomoduleBleManager.isConnected()) {
            affectiveCloudService?.connectCloud(object : Callback {
                override fun onError(error: Error?) {
                }

                override fun onSuccess() {
//                    Logger.d("affectivecloudmanager init success:")
//                    biomoduleBleManager.startHeartAndBrainCollection()
                }
            })
        }
    }

    fun initAffectiveCloudManager() {
        affectiveCloudService?.addListener({
            runOnUiThread {
                if (it != null && it!!.realtimeEEGData != null) {
                    Log.d("####", "eeg data:" + it!!.realtimeEEGData?.alphaPower)
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
                }
                if (it != null && it!!.realtimeHrData != null && !SettingManager.getInstance()
                        .timeCountIsEEG()
                ) {
                    if (isFirstReceiveHRData) {
                        MeditationTimeManager.getInstance().timeReset()
                        isFirstReceiveHRData = false
                    } else {
                        MeditationTimeManager.getInstance().timeIncrease()
                    }
                }
                meditationFragment?.showHeart(
                    it?.realtimeHrData?.hr?.toInt(),
                    it?.realtimeHrData?.hrv
                )
                meditationFragment?.showBrain(it?.realtimeEEGData)
            }
        }, {
            runOnUiThread {
                meditationFragment?.showAttention(it?.realtimeAttentionData?.attention?.toFloat())
                meditationFragment?.showRelaxation(it?.realtimeRelaxationData?.relaxation?.toFloat())
                meditationFragment?.showPressure(it?.realtimePressureData?.pressure?.toFloat())
                meditationFragment?.showMood(it?.realtimePleasureData?.pleasure?.toFloat())
                meditationFragment?.showArousal(it?.realtimeArousalData?.arousal?.toFloat())
                meditationFragment?.showCoherence(it?.realtimeCoherenceData?.coherence?.toFloat())
                meditationFragment?.showPleasure(it?.realtimePleasureData?.pleasure?.toFloat())
                meditationFragment?.showSleep(it?.realtimeSleepData?.sleepDegree?.toFloat())
                isSleep(it?.realtimeSleepData?.sleepState?.toInt())
            }
        })
    }

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
            isRecordTime = true
            ll_back.visibility = View.VISIBLE
            ll_home_layout.visibility = View.GONE
            ll_time_record_layout.visibility = View.VISIBLE
            initTimeRecordView()
        }
        btn_end_record.setOnClickListener {
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
        initTilte()
        initDataFragment()
        initScrollLayout()
        initSensorCheckDialog()
    }

    fun initTimeRecordView() {
        var meditationId = meditationId
        var meditationStartTime = meditationStartTime
        if (meditationId == -1L || meditationStartTime == -1L) {
            finish()
            Toast.makeText(this, "请先开始有效的体验", Toast.LENGTH_LONG).show()
        }
        tv_record_btn.setOnClickListener {
            if (tv_record_btn.text == "开始记录") {
                ll_back.visibility = View.GONE
                chronometer.visibility = View.VISIBLE
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                startTime = MeditationTimeManager.getInstance().currentTimeMs()
                tv_record_btn.setBackgroundResource(R.drawable.shape_time_record_end_bg)
                tv_record_btn.text = "结束记录"
            } else {
                ll_back.visibility = View.VISIBLE
                endTime = MeditationTimeManager.getInstance().currentTimeMs()
                chronometer.stop()
                var intent = Intent(this, MeditationLabelsRecordActivity::class.java)
                intent.putExtra(Constant.EXTRA_LABEL_START_TIME, startTime)
                intent.putExtra(Constant.EXTRA_LABEL_END_TIME, endTime!!)
                intent.putExtra(
                    EXTRA_MEDITATION_ID,
                    meditationId
                )
                intent.putExtra(
                    EXTRA_MEDITATION_START_TIME,
                    meditationStartTime
                )

                startActivity(intent)
            }
        }

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

    private lateinit var rawListener: (ByteArray) -> Unit
    private lateinit var heartRateListener: (Int) -> Unit
    private lateinit var bleConnectListener: (String) -> Unit
    private lateinit var contactListener: (Int) -> Unit
    private lateinit var bleDisconnectListener: (String) -> Unit

    //    var brainDataList = ArrayList<Int>()
    fun initFlowtimeManager() {

        if (biomoduleBleManager.isConnected()) {
            needToCheckSensor = true
        }
        rawListener = fun(bytes: ByteArray) {
            var currentTimeMs = System.currentTimeMillis()
            if (affectiveCloudService?.isInited() == true) {
                affectiveCloudService?.appendEEGData(bytes)
            }
            if (lastReceiveDataTimeMs != null) {
                if ((currentTimeMs - lastReceiveDataTimeMs!!) >= 15000L) {
                    lastReceiveDataTimeMs = null
                    affectiveCloudService?.closeWebSocket()
                }
            }

        }
        heartRateListener = fun(heartRate: Int) {
            if (affectiveCloudService?.isInited() == true) {
                affectiveCloudService?.appendHeartRateData(heartRate)
            }
        }
        bleConnectListener = fun(mac: String) {
            needToCheckSensor = true
        }
        bleDisconnectListener = fun(error: String) {
            needToCheckSensor = true
        }
        contactListener = fun(state: Int) {
//            Log.d("######", "contact is $state")
            runOnUiThread {
                if (isCheckContact) {
                    if (state == 0) {
                        if (!isContactWell) {
                            contactWellCount++
//                            Log.d("######", "contact is well")
                            if (contactWellCount == 5) {
                                isContactWell = true
                                card_2.visibility = View.VISIBLE
                                card_3.visibility = View.GONE
                                sensor_check_animate_layout.toSecondPage()
                            }
                        }
                    } else {
                        isContactWell = false
                        contactWellCount = 0
                    }
                }
            }
        }
        biomoduleBleManager.addRawDataListener(rawListener)
        biomoduleBleManager.addHeartRateListener(heartRateListener)
        biomoduleBleManager.addConnectListener(bleConnectListener)
        biomoduleBleManager.addContactListener(contactListener)
        biomoduleBleManager.addDisConnectListener(bleDisconnectListener)
        biomoduleBleManager.startHeartAndBrainCollection()

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
        meditaiton!!.attentionAvg = report.reportAttentionEnitty!!.attentionAvg!!.toFloat()
        meditaiton!!.attentionMax =
            java.util.Collections.max(report.reportAttentionEnitty?.attentionRec).toFloat()
        meditaiton!!.attentionMin =
            java.util.Collections.min(report.reportAttentionEnitty?.attentionRec).toFloat()
        meditaiton!!.heartRateAvg = report.reportHRDataEntity!!.hrAvg!!.toFloat()
        meditaiton!!.heartRateMax = report.reportHRDataEntity!!.hrMax!!.toFloat()
        meditaiton!!.heartRateMin = report.reportHRDataEntity!!.hrMin!!.toFloat()
        meditaiton!!.heartRateVariabilityAvg = report.reportHRDataEntity!!.hrvAvg!!.toFloat()
        meditaiton!!.relaxationAvg = report.reportRelaxationEnitty!!.relaxationAvg!!.toFloat()
        meditaiton!!.relaxationMax =
            java.util.Collections.max(report.reportRelaxationEnitty?.relaxationRec).toFloat()
        meditaiton!!.relaxationMin =
            java.util.Collections.min(report.reportRelaxationEnitty?.relaxationRec).toFloat()
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
        if (biomoduleBleManager.isConnected()) {
            scrollLayout.scrollToOpen()
        } else {
            scrollLayout.scrollToExit()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
//        var fragmentManager = supportFragmentManager
//        var fragmentTransaction = fragmentManager.beginTransaction()
        when (event.messageCode) {
            MessageEvent.MESSAGE_CODE_TO_DEVICE_CONNECT -> {
                if (!biomoduleBleManager.isConnected()) {
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
                        override fun onError(error: Error?) {
                        }

                        override fun onSuccess() {
                        }

                    })
                } else {
                    affectiveCloudService?.connectCloud(object : Callback {
                        override fun onError(error: Error?) {
                        }

                        override fun onSuccess() {
                            biomoduleBleManager.startHeartAndBrainCollection()
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
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogTitle
                    )}'>${getString(R.string.dialogExitTitle)}</font>"
                )
            )
            .setMessage(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogContent
                    )}'>${getString(R.string.dialogExitContent)}</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogExit
                    )}'>${getString(R.string.dialogExit)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
                finishMeditation()
            }
            .setNegativeButton(
                Html.fromHtml(
                    "<font color='${ContextCompat.getColor(
                        this,
                        R.color.colorDialogCancel
                    )}'>${getString(R.string.dialogCancel)}</font>"
                )
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    override fun onBackPressed() {
        if (scrollLayout.currentStatus == ScrollLayout.Status.OPENED || scrollLayout.currentStatus == ScrollLayout.Status.CLOSED) {
            scrollLayout.scrollToExit()
        } else {
//            showDialog()
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
            }
            saveLabelInLocal(recDatas)
        }
        biomoduleBleManager?.stopHeartAndBrainCollection()
        startFinishTimer()
        reportMeditationData = ReportMeditationDataEntity()
        meditationEndTime = getCurrentTimeFormat()
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

                    override fun onError(error: Error?) {
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
//        FileStoreHelper.getInstance().writeData(json)
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
                Toast.makeText(this, "体验时间过短", Toast.LENGTH_SHORT).show()
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

    fun unBindAffectiveService() {
        unbindService(connection!!)
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
        SoundScapeAudioManager.getInstance(this).release()
        affectiveCloudService?.stopForeground()
        unBindAffectiveService()
        stopAirSound()
        wl?.release()
        handler?.removeCallbacks(finishRunnable)
        sessionId = null
        biomoduleBleManager.stopHeartAndBrainCollection()
        biomoduleBleManager.stopBrainCollection()
        biomoduleBleManager.removeRawDataListener(rawListener)
        biomoduleBleManager.removeHeartRateListener(heartRateListener)
        biomoduleBleManager.removeContactListener(contactListener)
        biomoduleBleManager.removeDisConnectListener(bleDisconnectListener)
        if (affectiveCloudService!!.isInited()) {
            affectiveCloudService?.release()
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
