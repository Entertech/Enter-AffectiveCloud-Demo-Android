package cn.entertech.flowtimezh.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.text.Html
import android.util.Log
import android.view.View
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
import cn.entertech.flowtime.mvp.model.meditation.*
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
import cn.entertech.flowtimezh.ui.activity.BaseActivity
import cn.entertech.flowtimezh.ui.activity.DataActivity
import cn.entertech.flowtimezh.ui.fragment.MeditationFragment
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.ui.view.scrolllayout.ScrollLayout
import cn.entertech.flowtimezh.utils.FileStoreHelper
import cn.entertech.flowtimezh.utils.MeditationTimeManager
import cn.entertech.flowtimezh.utils.ScreenUtil
import cn.entertech.flowtimezh.utils.getCurrentTimeFormat
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
    private var isRecordTime: Boolean = false
    private var userId: String = ""
    private lateinit var biomoduleBleManager: MultipleBiomoduleBleManager
    private var meditaiton: MeditationEntity? = null
    private var userLessonEntity: UserLessonEntity? = null
    var enterAffectiveCloudManager: EnterAffectiveCloudManager? = null
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

    private var endTime: Long? = null
    private var startTime: Long? = null
//    var canExit = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        initFullScreenDisplay()
        setStatusBarLight()
        EventBus.getDefault().register(this)
        userLessonStartTime = getCurrentTimeFormat()
        loadingDialog = LoadingDialog(this)
        initFlowtimeManager()
        initView()
        initAffectiveCloudManager()
    }

    fun initAffectiveCloudManager() {
        var experimentDao = ExperimentDao(this)
        var modeDao = ExperimentModeDao(this)
        var experiment = experimentDao.findExperimentBySelected()
        userId = intent.getStringExtra("userId")
        var sex = intent.getStringExtra("sex")
        var age = intent.getStringExtra("age")
        var storageSettings = StorageSettings.Builder()
            .age(
                if (age == "") {
                    0
                } else {
                    Integer.parseInt(age)
                }
            ).sex(
                if (sex == "m") {
                    StorageSettings.Sex.MALE
                } else {
                    StorageSettings.Sex.FEMALE
                }
            ).case(listOf(experiment.id))
            .mode(modeDao.findModeByExperimentId(experiment.id).map { it.id })
            .build()


        var biodataTolerance = BiodataTolerance.Builder()
            .eeg(4)
            .build()
        var availableAffectiveServices =
            listOf(Service.ATTENTION, Service.PRESSURE, Service.RELAXATION, Service.PLEASURE)
        var availableBioServices = listOf(Service.EEG, Service.HR)
        var biodataSubscribeParams = BiodataSubscribeParams.Builder()
            .requestAllEEGData()
            .requestAllHrData()
            .build()

        var affectiveSubscribeParams = AffectiveSubscribeParams.Builder()
            .requestAllSleepData()
            .requestAttention()
            .requestRelaxation()
            .requestPressure()
            .requestPleasure()
            .build()
        var url = "wss://${SettingManager.getInstance().affectiveCloudServer}/ws/algorithm/v1/"
        var enterAffectiveCloudConfig = EnterAffectiveCloudConfig.Builder(
            SettingManager.getInstance().appKey,
            SettingManager.getInstance().appSecret,
            userId
        )
            .url(url)
            .timeout(10000)
            .availableBiodataServices(availableBioServices)
            .availableAffectiveServices(availableAffectiveServices)
            .biodataSubscribeParams(biodataSubscribeParams!!)
            .affectiveSubscribeParams(affectiveSubscribeParams!!)
            .storageSettings(storageSettings)
            .biodataTolerance(biodataTolerance)
            .build()
        enterAffectiveCloudManager = EnterAffectiveCloudManager(enterAffectiveCloudConfig)

        enterAffectiveCloudManager!!.addBiodataRealtimeListener {
            runOnUiThread {
                //            Logger.d("bio realtime data is " + it.toString())
                if (it != null && it!!.realtimeEEGData != null) {
                    Log.d("####","eeg data:"+it!!.realtimeEEGData?.alphaPower)
                    if (isFirstReceiveData) {
                        MeditationTimeManager.getInstance().timeReset()
                        meditationStartTime = System.currentTimeMillis()
                        meditationId = -System.currentTimeMillis()
                        Log.d("####", "meditation id is " + meditationId)
                        fragmentBuffer.fileName = getCurrentTimeFormat(meditationStartTime!!)
                        FileStoreHelper.getInstance().setPath(
                            MEDITATION_LABEL_RECORD_PATH,
                            getCurrentTimeFormat(meditationStartTime!!)
                        )
                        isFirstReceiveData = false
                    }
                    MeditationTimeManager.getInstance().timeIncrease()
                }
                meditationFragment?.showHeart(it?.realtimeHrData?.hr?.toInt())
                meditationFragment?.showBrain(it?.realtimeEEGData)
            }
        }
        enterAffectiveCloudManager!!.addAffectiveDataRealtimeListener {
            //            Logger.d("affective realtime data is " + it.toString())
            runOnUiThread {
                meditationFragment?.showAttention(it?.realtimeAttentionData?.attention?.toFloat())
                meditationFragment?.showRelaxation(it?.realtimeRelaxationData?.relaxation?.toFloat())
                meditationFragment?.showPressure(it?.realtimePressureData?.pressure?.toFloat())
                meditationFragment?.showMood(it?.realtimePleasureData?.pressure?.toFloat())
            }
        }
        if (biomoduleBleManager.isConnected()) {
            enterAffectiveCloudManager?.init(object : Callback {
                override fun onError(error: Error?) {
//                    Logger.d("affectivecloudmanager init failed:" + error.toString())
                }

                override fun onSuccess() {
//                    Logger.d("affectivecloudmanager init success:")
                    biomoduleBleManager.startHeartAndBrainCollection()
                }
            })
        }
    }

    fun initView() {
        btn_start_record.setOnClickListener {
            isRecordTime = true
            ll_home_layout.visibility = View.GONE
            ll_time_record_layout.visibility = View.VISIBLE
            initTimeRecordView()
//            var intent = Intent(this, MeditationTimeRecordActivity::class.java)
//            intent.putExtra(EXTRA_MEDITATION_ID, meditationId)
//            intent.putExtra(EXTRA_MEDITATION_START_TIME, meditationStartTime)
//            startActivity(intent)
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
    }

    fun initTimeRecordView(){
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
                ll_back.setOnClickListener {
                    ll_back.visibility = View.GONE
                    isRecordTime = false
                    ll_home_layout.visibility = View.VISIBLE
                    ll_time_record_layout.visibility = View.GONE
                }
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

    //    var brainDataList = ArrayList<Int>()
    fun initFlowtimeManager() {
        biomoduleBleManager = DeviceUIConfig.getInstance(this).managers[0]
        rawListener = fun(bytes: ByteArray) {
//            brainDataList.clear()
//            for (byte in bytes) {
//                var brainData = ConvertUtil.converUnchart(byte)
//                brainDataList.add(brainData)
//            }
//            Log.d("######", "firmware fixing brain:" + Arrays.toString(brainDataList.toArray()))
            enterAffectiveCloudManager?.appendEEGData(bytes)
        }
        heartRateListener = fun(heartRate: Int) {
            enterAffectiveCloudManager?.appendHeartRateData(heartRate)
        }
        biomoduleBleManager.addRawDataListener(rawListener)
        biomoduleBleManager.addHeartRateListener(heartRateListener)

    }

    lateinit var reportMeditationData: ReportMeditationDataEntity

    var finishRunnable = {
        exitWithoutMeditation()
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
                scrollLayout.scrollToOpen()
                startActivity(Intent(this, DeviceManagerActivity::class.java))
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
                    enterAffectiveCloudManager?.init(object : Callback {
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
        enterAffectiveCloudManager?.getBiodataReport(object : Callback2<HashMap<Any, Any?>> {
            override fun onError(error: Error?) {
            }

            override fun onSuccess(t: HashMap<Any, Any?>?) {
//                Logger.d("report bio is " + t.toString())
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
                    reportEEGDataEntity.alphaCurve =
                        eegMap["eeg_alpha_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_beta_curve")) {
                    reportEEGDataEntity.betaCurve =
                        eegMap["eeg_beta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_theta_curve")) {
                    reportEEGDataEntity.thetaCurve =
                        eegMap["eeg_theta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_delta_curve")) {
                    reportEEGDataEntity.deltaCurve =
                        eegMap["eeg_delta_curve"] as ArrayList<Double>
                }
                if (eegMap!!.containsKey("eeg_gamma_curve")) {
                    reportEEGDataEntity.gammaCurve =
                        eegMap["eeg_gamma_curve"] as ArrayList<Double>
                }
                reportMeditationData.reportEEGDataEntity = reportEEGDataEntity

                enterAffectiveCloudManager?.getAffectiveDataReport(object :
                    Callback2<HashMap<Any, Any?>> {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess(t: HashMap<Any, Any?>?) {
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
                            reportPressureEnitty.pressureAvg =
                                pressureMap["pressure_avg"] as Double
                        }
                        if (pressureMap!!.containsKey("pressure_rec")) {
                            reportPressureEnitty.pressureRec =
                                pressureMap["pressure_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportPressureEnitty = reportPressureEnitty

                        var reportPleasureEnitty = ReportPleasureEnitty()
                        var pleasureMap = t["pleasure"] as Map<Any, Any?>
                        if (pleasureMap!!.containsKey("pleasure_avg")) {
                            reportPleasureEnitty.pleasureAvg =
                                pleasureMap["pleasure_avg"] as Double
                        }
                        if (pleasureMap!!.containsKey("pleasure_rec")) {
                            reportPleasureEnitty.pleasureRec =
                                pleasureMap["pleasure_rec"] as ArrayList<Double>
                        }
                        reportMeditationData.reportPleasureEnitty = reportPleasureEnitty
                        exitWithMeditation(reportMeditationData)
                    }

                })
            }

        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (isRecordTime){
            resetPage()
        }else{
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
                recData.note = listOf()
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
        if (meditationTimeError() || !enterAffectiveCloudManager!!.isWebSocketOpen()) {
            exitWithoutMeditation()
        } else {
            if (meditationLabels == null || meditationLabels.isEmpty()) {
                getReportAndExit()
            } else {
                loadingDialog?.loading("正在提交数标签...")
                enterAffectiveCloudManager?.submit(recDatas, object : Callback {
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
        recDataRecord.session_id = enterAffectiveCloudManager?.mApi?.getSessionId()
        var json = Gson().toJson(recDataRecord)
        FileStoreHelper.getInstance().writeData(json)
    }


    fun exitWithMeditation(reportMeditationData: ReportMeditationDataEntity) {
        if (reportMeditationData.isDataSetCompletly()) {
            handler.removeCallbacks(finishRunnable)
            saveReportFile(reportMeditationData)
            saveMeditationInDB(reportMeditationData)
            saveUserLessonInDB()
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    toDataActivity()
                }

            })
        }
    }

    fun exitWithoutMeditation() {
//        saveUserLessonInDB()
//        Toast.makeText(activity!!,"体验时间过短！",Toast.LENGTH_SHORT).show()
        if (enterAffectiveCloudManager!!.isWebSocketOpen()) {
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                    finish()
                }

            })
        } else {
            finish()
        }
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

    override fun onDestroy() {
        handler?.removeCallbacks(finishRunnable)
        sessionId = null
        biomoduleBleManager.stopHeartAndBrainCollection()
        biomoduleBleManager.stopBrainCollection()
        biomoduleBleManager.removeRawDataListener(rawListener)
        biomoduleBleManager.removeHeartRateListener(heartRateListener)
        if (enterAffectiveCloudManager!!.isInit) {
            enterAffectiveCloudManager?.release(object : Callback {
                override fun onError(error: Error?) {

                }

                override fun onSuccess() {
                }

            })
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
