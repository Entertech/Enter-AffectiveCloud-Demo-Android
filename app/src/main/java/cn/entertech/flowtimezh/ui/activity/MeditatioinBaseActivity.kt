package cn.entertech.flowtimezh.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
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
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.ui.fragment.MeditationFragment
import cn.entertech.affectivecloudsdk.*
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.Service
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.affectivecloudsdk.interfaces.Callback2
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.flowtime.utils.reportfileutils.*
import cn.entertech.uicomponentsdk.utils.dp
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.layout_common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.RECORD_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.mvp.model.meditation.*
import cn.entertech.flowtimezh.ui.fragment.MeditationEditFragment
import cn.entertech.flowtimezh.ui.view.DeviceConnectView
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.utils.LogManager
import cn.entertech.flowtimezh.utils.ToastUtil
import cn.entertech.flowtimezh.utils.getCurrentTimeFormat
import cn.entertech.flowtimezh.utils.reportfileutils.SyncManager
import cn.entertech.flowtimezh.utils.scrolllayout.ScrollLayout
import cn.entertech.uicomponentsdk.utils.ScreenUtil
import kotlinx.android.synthetic.main.activity_meditation1.*

abstract class MeditationBaseActivity : BaseActivity() {
    var animatorSet: AnimatorSet? = null
    var isToConnectDevice: Boolean = false
    var isMediaPlayMultipleTime = false
    var enterAffectiveCloudManager: EnterAffectiveCloudManager? = null
    private var userLessonEntity: UserLessonEntity? = null
    private var meditaiton: MeditationEntity? = null
    var lessonFileUrl: String? = null
    var mediaPlayer: MediaPlayer? = null
    var handlr: Handler = Handler()
    var lessonId: Int = 0
    var courseId: Int = 0
    var lessonName: String = ""
    var courseName: String? = ""
    lateinit var scrollLayout: ScrollLayout
    var meditationFragment: MeditationFragment? = null
    var meditationEditFragment: MeditationEditFragment? = null
    var biomoduleBleManager: BiomoduleBleManager =
        BiomoduleBleManager.getInstance(Application.getInstance())

    //    var webSocketManager: WebSocketManager = WebSocketManager.getInstance()
    var fragmentBuffer = FragmentBuffer()
    var sessionId: String? = null
    var userLessonStartTime: String? = null
    var meditationStartTime: Long? = null
    var meditationEndTime: String? = null
    var loadingDialog: LoadingDialog? = null
    private var meditationId: Long = -1
    private var mRecordId: Long = -1
    var goodQualityRate = 0f
    var qualityCollection = ArrayList<Double>()

    var isFixingFirmware = false
    var isFirstReceiveData = true

    var websocketAddress = "wss://server.affectivecloud.cn/ws/algorithm/v1/"

    val APP_KEY: String = "93e3cf84-dea1-11e9-ae15-0242ac120002"
    val APP_SECRET: String = "c28e78f98f154962c52fcd3444d8116f"

//    val APP_KEY: String = "015b7118-b81e-11e9-9ea1-8c8590cb54f9"
//    val APP_SECRET: String = "cd9c757ae9a7b7e1cff01ee1bb4d4f98"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFullScreenDisplay()
        setContentView(R.layout.activity_meditation1)
//        biomoduleBleManager.stopHeartAndBrainCollection()
        init()
        EventBus.getDefault().register(this)
        userLessonStartTime = getCurrentTimeFormat()
        loadingDialog = LoadingDialog(this)
        initView()
        initAffectiveCloudManager()
        initFlowtimeManager()
    }

    abstract fun resumeMeditation()
    abstract fun pauseMeditation()
    fun initAffectiveCloudManager() {
        var availableAffectiveServices =
            listOf(Service.ATTENTION, Service.PRESSURE, Service.RELAXATION, Service.PLEASURE,Service.COHERENCE,Service.AROUSAL)
        var availableBioServices = listOf(Service.EEG, Service.HR)
        var biodataSubscribeParams = BiodataSubscribeParams.Builder()
            .requestAllEEGData()
            .requestAllHrData()
            .build()

        var biodataTolerance = BiodataTolerance.Builder().eeg(2).build()

        var affectiveSubscribeParams = AffectiveSubscribeParams.Builder()
            .requestAllSleepData()
            .requestAttention()
            .requestRelaxation()
            .requestPressure()
            .requestPleasure()
            .requestCoherence()
            .requestArousal()
            .build()
        var enterAffectiveCloudConfig = EnterAffectiveCloudConfig.Builder(
            APP_KEY,
            APP_SECRET,
            "${SettingManager.getInstance().userId}"
        )
            .url(websocketAddress)
            .timeout(10000)
            .availableBiodataServices(availableBioServices)
            .availableAffectiveServices(availableAffectiveServices)
            .biodataSubscribeParams(biodataSubscribeParams!!)
            .affectiveSubscribeParams(affectiveSubscribeParams!!)
            .biodataTolerance(biodataTolerance)
            .build()
        enterAffectiveCloudManager = EnterAffectiveCloudManager(enterAffectiveCloudConfig)
        enterAffectiveCloudManager!!.addBiodataRealtimeListener {
            runOnUiThread {
                //            Logger.d("bio realtime data is " + it.toString())
                if (it != null && it!!.realtimeEEGData != null) {
                    if (isFirstReceiveData) {
                        qualityCollection.clear()
                        meditationStartTime = System.currentTimeMillis()
                        fragmentBuffer.fileName = getCurrentTimeFormat(meditationStartTime!!)
                        isFirstReceiveData = false
                    }
                }
                meditationFragment?.showHeart(
                    it?.realtimeHrData?.hr?.toInt(),
                    it?.realtimeHrData?.hrv
                )
                meditationFragment?.showBrain(it?.realtimeEEGData)
                meditationFragment?.dealQuality(it?.realtimeEEGData?.quality)
                if (it != null && it!!.realtimeEEGData != null && it!!.realtimeEEGData!!.quality != null) {
                    qualityCollection.add(it!!.realtimeEEGData!!.quality!!)
                }
            }
        }
        enterAffectiveCloudManager!!.addAffectiveDataRealtimeListener {
            //            Logger.d("affective realtime data is " + it.toString())
            runOnUiThread {
                meditationFragment?.showAttention(it?.realtimeAttentionData?.attention?.toFloat())
                meditationFragment?.showRelaxation(it?.realtimeRelaxationData?.relaxation?.toFloat())
                meditationFragment?.showPressure(it?.realtimePressureData?.pressure?.toFloat())
                meditationFragment?.showMood(it?.realtimePleasureData?.pleasure?.toFloat())
            }
        }

    }

    fun connectWebSocket(){
        if (biomoduleBleManager.isConnected()) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_AFFECTIVE_WEBSOCKET_CONNECTING)
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT)
            enterAffectiveCloudManager?.init(object : Callback {
                override fun onError(error: Error?) {
                    LogManager.getInstance()
                        .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT_FAILED)
//                    Logger.d("affectivecloudmanager init failed:" + error.toString())
                }

                override fun onSuccess() {
                    LogManager.getInstance()
                        .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT_COMPLETE)
//                    Logger.d("affectivecloudmanager init success:")
//                    biomoduleBleManager.startHeartAndBrainCollection()
                }
            })
        }
    }

    open abstract fun init()

    fun initView() {
        initTilte()
        tv_lesson_index.text = courseName
        tv_lesson_name.text = lessonName
        initDataFragment()
        initScrollLayout()
        initSensorCheckDialog()
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
            startActivity(Intent(this@MeditationBaseActivity,SensorContactCheckActivity::class.java))
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

    private lateinit var rawListener: (ByteArray) -> Unit

    private lateinit var heartRateListener: (Int) -> Unit
    private lateinit var bleConnectListener: (String) -> Unit
    private lateinit var contactListener: (Int) -> Unit
    private lateinit var bleDisconnectListener: (String) -> Unit
    var isCheckContact = false
    var isSensorCheckShow = false
    var contactWellCount = 0
    var isContactWell = false
    var needToCheckSensor = false

    //    var brainDataList = ArrayList<Int>()
    fun initFlowtimeManager() {
        if (biomoduleBleManager.isConnected()){
            needToCheckSensor = true
        }
        rawListener = fun(bytes: ByteArray) {
            if (enterAffectiveCloudManager?.isInit == true) {
                enterAffectiveCloudManager?.appendEEGData(bytes)
            }
        }
        heartRateListener = fun(heartRate: Int) {
            if (enterAffectiveCloudManager?.isInit == true) {
                enterAffectiveCloudManager?.appendHeartRateData(heartRate)
            }
        }
        bleConnectListener = fun(mac: String) {
            needToCheckSensor = true
        }
        bleDisconnectListener = fun(error:String){
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
        exitMeditation()
    }

    fun saveReport(reportMeditationData: ReportMeditationDataEntity) {
        if (reportMeditationData != null) {
//            Logger.d("reportMeditationData is " + reportMeditationData.toString())
        }
        if (reportMeditationData.isDataSetCompletly()) {
            handlr.removeCallbacks(finishRunnable)
            exitMeditation()
        }
    }

    fun exitMeditation() {
        if (meditationStartTime != null) {
            saveReportFile(reportMeditationData)
            saveMeditationInDB(reportMeditationData)
            saveUserLessonInDB()
        } else {
            saveUserLessonInDB()
        }
        postRecord()
    }

    fun saveReportFile(reportMeditationData: ReportMeditationDataEntity) {
        fragmentBuffer.appendMeditationReport(
            reportMeditationData,
            meditationStartTime!!,
            MeditaionInterruptManager.getInstance().interruptTimestampList
        )
    }

    fun saveMeditationInDB(report: ReportMeditationDataEntity) {
        var meditationDao = MeditationDao(this)
        meditaiton = MeditationEntity()
        meditationId = -System.currentTimeMillis()
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
        meditaiton!!.pressureAvg = report.reportPressureEnitty!!.pressureRec!!.average().toFloat()
        meditaiton!!.user = SettingManager.getInstance().userId
        meditaiton!!.acSessionId = enterAffectiveCloudManager?.mApi?.getSessionId()
        Log.d("#######","session id is ${enterAffectiveCloudManager?.mApi?.getSessionId()}")
        var reportFileUri =
            "${SettingManager.getInstance().userId}/${courseId}/${lessonId}/${fragmentBuffer.fileName}"
        meditaiton!!.meditationFile = reportFileUri
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
        userLessonEntity!!.user = SettingManager.getInstance().userId
        userLessonEntity!!.meditation = meditationId
        userLessonRecordDao.create(userLessonEntity)
    }

    fun toDataActivity() {
        loadingDialog?.dismiss()
        var intent = Intent(this, DataActivity::class.java)
        intent.putExtra(RECORD_ID, mRecordId)
        startActivity(intent)
        finish()
    }

    fun initDataFragment() {
        meditationFragment = MeditationFragment()
        meditationEditFragment = MeditationEditFragment()
        meditationFragment?.setScrollTopListener(object : MeditationFragment.IScrollTopListener {
            override fun isScrollTop(flag: Boolean) {
                scrollLayout.setIsChildListTop(flag)
            }
        })
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        if (meditationFragment != null && meditationEditFragment != null) {
            fragmentTransaction?.add(R.id.container, meditationFragment!!)
            fragmentTransaction?.add(R.id.container, meditationEditFragment!!)
                ?.show(meditationFragment!!)
                ?.hide(meditationEditFragment!!)
                ?.commit()
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


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        meditationFragment?.hideInterruptTip()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        when (event.messageCode) {
            MessageEvent.MESSAGE_CODE_DATA_EDIT -> {
                fragmentTransaction?.hide(meditationFragment!!)?.show(meditationEditFragment!!)
                    ?.commit()
            }
            MessageEvent.MESSAGE_CODE_DATA_EDIT_DONE -> {
                meditationFragment?.refreshMeditationView()
                fragmentTransaction?.show(meditationFragment!!)?.hide(meditationEditFragment!!)
                    ?.commit()
            }
            MessageEvent.MESSAGE_CODE_TO_DEVICE_CONNECT -> {
                if (!biomoduleBleManager.isConnected()){
                    isToConnectDevice = true
                }
                scrollLayout.scrollToOpen()
                if (SettingManager.getInstance().isConnectBefore) {
                    startActivityForResult(Intent(this, DeviceStatusActivity::class.java), 1)
                } else {
                    startActivityForResult(Intent(this, DevicePermissionActivity::class.java), 1)
                }
            }
            MessageEvent.MESSAGE_CODE_TO_NET_RESTORE -> {
                if (enterAffectiveCloudManager!!.isInited()) {
                    enterAffectiveCloudManager?.restore(object : Callback {
                        override fun onError(error: Error?) {

                        }

                        override fun onSuccess() {
                            biomoduleBleManager.startHeartAndBrainCollection()
                        }

                    })
                } else {

                    LogManager.getInstance().logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT)
                    enterAffectiveCloudManager?.init(object : Callback {
                        override fun onError(error: Error?) {
                            LogManager.getInstance()
                                .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT_FAILED)
                        }

                        override fun onSuccess() {
                            LogManager.getInstance()
                                .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_INIT_COMPLETE)
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
                    "<font color='${getColor(R.color.colorDialogTitle)}'>${getString(
                        R.string.dialogExitTitle
                    )}</font>"
                )
            )
            .setMessage(
                Html.fromHtml(
                    "<font color='${getColor(R.color.colorDialogContent)}'>${getString(
                        R.string.dialogExitContent
                    )}</font>"
                )
            )
            .setPositiveButton(
                Html.fromHtml("<font color='${getColor(R.color.colorDialogExit)}'>${getString(R.string.dialogExit)}</font>")
            ) { dialog, which ->
                dialog.dismiss()
                finishMeditation()
            }
            .setNegativeButton(
                Html.fromHtml("<font color='${getColor(R.color.colorDialogCancel)}'>${getString(R.string.dialogCancel)}</font>")
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        dialog.show()
    }

    fun showSensorCheckDialog() {
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
                if (enterAffectiveCloudManager != null && !enterAffectiveCloudManager!!.isInited()){
                    connectWebSocket()
                }
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
        resumeMeditation()
    }

    override fun onBackPressed() {
        if (scrollLayout.currentStatus == ScrollLayout.Status.OPENED || scrollLayout.currentStatus == ScrollLayout.Status.CLOSED) {
            scrollLayout.scrollToExit()
        } else {
            handleEEGQuality()
            showExitView()
        }
    }

    fun handleEEGQuality() {
        if (qualityCollection == null || qualityCollection.size == 0) {
            return
        }
        var goodQuality = 0
        for (quality in qualityCollection) {
            if (quality >= 2) {
                goodQuality++
            }
        }
        goodQualityRate = goodQuality * 1f / qualityCollection.size
    }

    abstract fun showExitView()

    fun initTilte() {
        findViewById<ImageView>(R.id.iv_back).setImageResource(R.mipmap.ic_premium_close)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            //            showDialog()
            handleEEGQuality()
            showExitView()
        }
        findViewById<TextView>(R.id.tv_title).visibility = View.INVISIBLE
        findViewById<RelativeLayout>(R.id.rl_title_bg).setBackgroundColor(Color.TRANSPARENT)
        findViewById<RelativeLayout>(R.id.rl_menu_ic).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.rl_menu_ic).setOnClickListener {
            if (!biomoduleBleManager.isConnected()){
                isToConnectDevice = true
            }
            if (SettingManager.getInstance().isConnectBefore) {
                startActivity(Intent(this, DeviceStatusActivity::class.java))
            } else {
                startActivity(Intent(this, DevicePermissionActivity::class.java))
            }
        }
        dcv_conect_view.visibility = View.VISIBLE
        dcv_conect_view.setType(DeviceConnectView.IconType.WHITE)
        iv_menu_icon.visibility = View.GONE

    }

    var mMainHanlder = Handler()
    override fun onResume() {
        super.onResume()
        if (isToConnectDevice && biomoduleBleManager.isConnected()){
            mMainHanlder.postDelayed(Runnable {
                showSensorCheckDialog()
                isToConnectDevice = false
            }, 1000)
        }
    }

    fun report() {

        LogManager.getInstance()
            .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GETTING)
        enterAffectiveCloudManager?.getBiodataReport(object : Callback2<HashMap<Any, Any?>> {
            override fun onError(error: Error?) {
                LogManager.getInstance()
                    .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GET_FAILED)
            }

            override fun onSuccess(t: HashMap<Any, Any?>?) {
//                Logger.d("report bio is " + t.toString())

                LogManager.getInstance()
                    .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_BIODATA_REPORT_GET_COMPLETE)
                if (t == null) {
                    return
                }
                var reportHRDataEntity = ReportHRDataEntity()
                var hrMap = t!!["hr"] as Map<Any, Any?>
                if (hrMap!!.containsKey("hr_avg")) {
                    reportHRDataEntity.hrAvg = hrMap["hr_avg"] as Double
                }
                if (hrMap!!.containsKey("hr_max")) {
                    reportHRDataEntity.hrMax = hrMap["hr_max"] as Double
                }
                if (hrMap!!.containsKey("hr_min")) {
                    reportHRDataEntity.hrMin = hrMap["hr_min"] as Double
                }
                if (hrMap!!.containsKey("hr_rec")) {
                    reportHRDataEntity.hrRec = hrMap["hr_rec"] as ArrayList<Double>
                }
                if (hrMap!!.containsKey("hrv_rec")) {
                    reportHRDataEntity.hrvRec = hrMap["hrv_rec"] as ArrayList<Double>
                }
                if (hrMap!!.containsKey("hrv_avg")) {
                    reportHRDataEntity.hrvAvg = hrMap["hrv_avg"] as Double
                }
                reportMeditationData.reportHRDataEntity = reportHRDataEntity
                var reportEEGDataEntity = ReportEEGDataEntity()
                var eegMap = t!!["eeg"] as Map<Any, Any?>
                if (eegMap!!.containsKey("eeg_alpha_curve")) {
                    reportEEGDataEntity.alphaCurve = eegMap["eeg_alpha_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_beta_curve")) {
                    reportEEGDataEntity.betaCurve = eegMap["eeg_beta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_theta_curve")) {
                    reportEEGDataEntity.thetaCurve = eegMap["eeg_theta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_delta_curve")) {
                    reportEEGDataEntity.deltaCurve = eegMap["eeg_delta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_gamma_curve")) {
                    reportEEGDataEntity.gammaCurve = eegMap["eeg_gamma_curve"] as ArrayList<Double>
                }
                reportMeditationData.reportEEGDataEntity = reportEEGDataEntity

                LogManager.getInstance()
                    .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GETTING)
                enterAffectiveCloudManager?.getAffectiveDataReport(object :
                    Callback2<HashMap<Any, Any?>> {
                    override fun onError(error: Error?) {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GET_FAILED)
                    }

                    override fun onSuccess(t: HashMap<Any, Any?>?) {
                        LogManager.getInstance()
                            .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_AFFECTIVE_DATA_REPORT_GET_COMPLETE)
//                        Logger.d("report affectve is " + t.toString())
                        if (t == null) {
                            return
                        }
                        var reportAttentionEnitty = ReportAttentionEnitty()
                        var attentionMap = t["attention"] as Map<Any, Any?>
                        if (attentionMap!!.containsKey("attention_avg")) {
                            Logger.d("attention avg is " + attentionMap["attention_avg"] as Double)
                            reportAttentionEnitty.attentionAvg =
                                attentionMap["attention_avg"] as Double
                        }
                        if (attentionMap!!.containsKey("attention_rec")) {
                            reportAttentionEnitty.attentionRec =
                                attentionMap["attention_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportAttentionEnitty = reportAttentionEnitty
                        var reportRelxationEntity = ReportRelaxationEnitty()
                        var relaxationMap = t["relaxation"] as Map<Any, Any?>
                        if (relaxationMap!!.containsKey("relaxation_avg")) {
                            reportRelxationEntity.relaxationAvg =
                                relaxationMap["relaxation_avg"] as Double
                        }
                        if (relaxationMap!!.containsKey("relaxation_rec")) {
                            reportRelxationEntity.relaxationRec =
                                relaxationMap["relaxation_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportRelaxationEnitty = reportRelxationEntity

                        var reportPressureEnitty = ReportPressureEnitty()

                        var pressureMap = t["pressure"] as Map<Any, Any?>
                        if (pressureMap!!.containsKey("pressure_avg")) {
                            reportPressureEnitty.pressureAvg = pressureMap["pressure_avg"] as Double
                        }
                        if (pressureMap!!.containsKey("pressure_rec")) {
                            reportPressureEnitty.pressureRec =
                                pressureMap["pressure_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportPressureEnitty = reportPressureEnitty

                        var reportPleasureEnitty = ReportPleasureEnitty()
                        var pleasureMap = t["pleasure"] as Map<Any, Any?>
                        if (pleasureMap!!.containsKey("pleasure_avg")) {
                            reportPleasureEnitty.pleasureAvg = pleasureMap["pleasure_avg"] as Double
                        }
                        if (pleasureMap!!.containsKey("pleasure_rec")) {
                            reportPleasureEnitty.pleasureRec =
                                pleasureMap["pleasure_rec"] as ArrayList<Double>
                        }

                        reportMeditationData.reportPleasureEnitty = reportPleasureEnitty

                        var reportCoherenceEnitty = ReportCoherenceEnitty()
                        var coherenceMap = t["coherence"] as Map<Any, Any?>
                        if (coherenceMap!!.containsKey("coherence_avg")) {
                            reportCoherenceEnitty.coherenceAvg = coherenceMap["coherence_avg"] as Double
                        }
                        if (coherenceMap!!.containsKey("coherence_rec")) {
                            reportCoherenceEnitty.coherenceRec =
                                coherenceMap["coherence_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportCoherenceEnitty = reportCoherenceEnitty

                        saveReport(reportMeditationData)
                    }

                })
            }

        })
    }

    fun finishMeditation() {
        if (mediaPlayer == null || (!isMediaPlayMultipleTime && mediaPlayer!!.currentPosition * 1f / mediaPlayer!!.duration < 0.75)) {
            ToastUtil.toastShort(this, "Meditation didn't finish, no report generate.")
            finish()
        } else {
            startFinishTimer()
            reportMeditationData = ReportMeditationDataEntity()
            meditationEndTime = getCurrentTimeFormat()
            biomoduleBleManager.stopHeartAndBrainCollection()
            biomoduleBleManager.stopBrainCollection()
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_FINISH_EEG)
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_BLE_FINISH_HR)
            if (meditationStartTime == null) {
                saveUserLessonInDB()
                postRecord()
            } else {
                report()
            }
        }
    }

    fun postRecord() {
        SyncManager.getInstance().uploadRecord(userLessonEntity!!, meditaiton, fun() {
            var messageEvent = MessageEvent()
            messageEvent.messageCode = MessageEvent.MESSAGE_CODE_TO_REFRESH_RECORD
            messageEvent.message = "refreshRecord"
            EventBus.getDefault().post(messageEvent)
        })
        toDataActivity()
    }

    fun startFinishTimer() {
        handlr.postDelayed({
            finishRunnable
        }, 10000)
    }

    override fun onDestroy() {
        handlr?.removeCallbacks(finishRunnable)
        sessionId = null
        biomoduleBleManager.stopHeartAndBrainCollection()
        biomoduleBleManager.stopBrainCollection()
        biomoduleBleManager.removeRawDataListener(rawListener)
        biomoduleBleManager.removeHeartRateListener(heartRateListener)
        biomoduleBleManager.removeContactListener(contactListener)
        biomoduleBleManager.removeDisConnectListener(bleDisconnectListener)
        if (enterAffectiveCloudManager!!.isInit) {
            LogManager.getInstance().logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_FINISHING)
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {
                    LogManager.getInstance()
                        .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_FINISH_FAILED)
                }

                override fun onSuccess() {
                    LogManager.getInstance()
                        .logPost(LogManager.LOG_EVENT_AFFECTIVE_CLOUD_FINISH_COMPLETE)
                }

            })
        }
        dcv_conect_view.release()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
