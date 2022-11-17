package cn.entertech.flowtimezh.ui.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cn.entertech.affectivecloudsdk.*
import cn.entertech.affectivecloudsdk.entity.Error
import cn.entertech.affectivecloudsdk.entity.RealtimeAffectiveData
import cn.entertech.affectivecloudsdk.entity.RealtimeBioData
import cn.entertech.affectivecloudsdk.entity.RecData
import cn.entertech.affectivecloudsdk.interfaces.Callback
import cn.entertech.affectivecloudsdk.interfaces.Callback2
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.DEVICE_TYPE_CUSHION
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.entity.meditation.*
import cn.entertech.flowtimezh.utils.ConnectedDevice
import cn.entertech.flowtimezh.utils.ConnectedDeviceHelper
import com.orhanobut.logger.Logger
import java.util.*


internal class AffectiveCloudService : Service() {
    private var enterAffectiveCloudManager: EnterAffectiveCloudManager? = null
    private var affectiveListener: ((RealtimeAffectiveData?) -> Unit)? = null
    private var biodataListener: ((RealtimeBioData?) -> Unit)? = null

    override fun onBind(intent: Intent): IBinder? {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_notification_logo)
            .setContentTitle("Flowtime is working")
            .setContentText("Flowtime is analyzing your meditation in the background.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        createNotificationChannel()
        val notificationManager = NotificationManagerCompat.from(this)

        // notificationId is a unique int for each notification that you must define

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build())
        startForeground(1, builder.build())

        initAffectiveCloudManager(intent)
        return MyBinder()
    }

    internal inner class MyBinder : Binder() {
        val service: AffectiveCloudService
            get() = this@AffectiveCloudService
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "name"
            val description = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun stopForeground() {
        stopForeground(true)
    }

    fun addListener(
        biodataListener: (RealtimeBioData?) -> Unit,
        affectiveListener: (RealtimeAffectiveData?) -> Unit
    ) {
        this.biodataListener = biodataListener
        this.affectiveListener = affectiveListener
    }

    fun initAffectiveCloudManager(intent:Intent) {
        var userId = intent.getStringExtra("userId")
        var sex = intent.getStringExtra("sex")
        var age = intent.getStringExtra("age")
        var case = intent.getIntArrayExtra("case")
        var mode = intent.getIntArrayExtra("mode")
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
            ).case(case!!.toList())
            .mode(mode!!.toList())
            .build()


        var biodataTolerance = BiodataTolerance.Builder()
            .eeg(2)
            .build()
        var availableAffectiveServices =
            listOf(
                cn.entertech.affectivecloudsdk.entity.Service.ATTENTION,
                cn.entertech.affectivecloudsdk.entity.Service.PRESSURE,
                cn.entertech.affectivecloudsdk.entity.Service.RELAXATION,
                cn.entertech.affectivecloudsdk.entity.Service.PLEASURE,
                cn.entertech.affectivecloudsdk.entity.Service.AROUSAL,
                cn.entertech.affectivecloudsdk.entity.Service.COHERENCE,
                cn.entertech.affectivecloudsdk.entity.Service.SLEEP
            )

        var biodataSubscribeParams: BiodataSubscribeParams? = null
        val availableBioServices: ArrayList<cn.entertech.affectivecloudsdk.entity.Service> = ArrayList()
        val connectedDevice = ConnectedDeviceHelper.currentConnectedDeviceType()
        if (connectedDevice == ConnectedDevice.CUSHION) {
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.PEPR)
            biodataSubscribeParams = BiodataSubscribeParams.Builder()
                .requestPEPR()
                .build()
        } else if (connectedDevice == ConnectedDevice.HEADBAND) {
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.EEG)
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.HR)
            biodataSubscribeParams = BiodataSubscribeParams.Builder()
                .requestEEG()
                .requestHR()
                .build()
        } else if (connectedDevice == ConnectedDevice.HEADBAND_AND_CUSHION) {
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.PEPR)
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.EEG)
            biodataSubscribeParams = BiodataSubscribeParams.Builder()
                .requestEEG()
                .requestPEPR()
                .build()
        } else {
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.EEG)
            availableBioServices.add(cn.entertech.affectivecloudsdk.entity.Service.HR)
            biodataSubscribeParams = BiodataSubscribeParams.Builder()
                .requestEEG()
                .requestHR()
                .build()
        }
        var affectiveSubscribeParams = AffectiveSubscribeParams.Builder()
            .requestSleep()
            .requestAttention()
            .requestRelaxation()
            .requestPressure()
            .requestPleasure()
            .requestArousal()
            .requestCoherence()
            .build()
        var algorithmParamsEEG =
            AlgorithmParamsEEG.Builder()
                .tolerance(AlgorithmParams.Tolerance.LEVEL_2)
                .filterMode(AlgorithmParams.FilterMode.SMART)
                .powerMode(AlgorithmParams.PowerMode.RATE)
                .channelPowerVerbose(false)
                .build()
        var algorithmParams = AlgorithmParams.Builder()
            .eeg(algorithmParamsEEG)
            .build()
        var url = "wss://${SettingManager.getInstance().affectiveCloudServer}/ws/algorithm/v2/"
        var enterAffectiveCloudConfig = EnterAffectiveCloudConfig.Builder(
            SettingManager.getInstance().appKey,
            SettingManager.getInstance().appSecret,
            userId!!
        )
            .url(url)
            .timeout(10000)
            .availableBiodataServices(availableBioServices)
            .availableAffectiveServices(availableAffectiveServices)
            .biodataSubscribeParams(biodataSubscribeParams!!)
            .affectiveSubscribeParams(affectiveSubscribeParams!!)
            .storageSettings(storageSettings)
            .biodataTolerance(biodataTolerance)
            .algorithmParams(algorithmParams)
            .uploadCycle(1)
            .build()
        enterAffectiveCloudManager = EnterAffectiveCloudManager(enterAffectiveCloudConfig)
        enterAffectiveCloudManager!!.addBiodataRealtimeListener {
            biodataListener?.invoke(it)
        }
        enterAffectiveCloudManager!!.addAffectiveDataRealtimeListener {
            //            Logger.d("affective realtime data is " + it.toString())
            affectiveListener?.invoke(it)
        }
    }

    fun connectCloud(callback: Callback) {
        enterAffectiveCloudManager?.init(callback)
    }

    fun restoreCloud(callback: Callback) {
        enterAffectiveCloudManager?.restore(callback)
    }

    fun isInited(): Boolean {
        return enterAffectiveCloudManager != null && enterAffectiveCloudManager!!.isInit
    }

    fun setInit(isInit: Boolean) {
        if (enterAffectiveCloudManager != null) {
            enterAffectiveCloudManager!!.isInit = isInit
        }
    }

    fun getSessionId(): String? {
        if (enterAffectiveCloudManager != null && enterAffectiveCloudManager!!.mApi != null) {
            return enterAffectiveCloudManager!!.mApi!!.getSessionId()
        }
        return null
    }

    fun appendHeartRateData(heartRateData: Int, triggerCount: Int = 2) {
        enterAffectiveCloudManager?.appendHeartRateData(heartRateData)
    }

    fun appendEEGData(brainData: ByteArray, triggerCount: Int = 600) {
        enterAffectiveCloudManager?.appendEEGData(brainData)
    }

    fun appendPEPR(pepr:ByteArray){
        enterAffectiveCloudManager?.appendPEPRData(pepr)
    }

    fun getBiodataReport(listener: (ReportMeditationDataEntity) -> Unit) {
        var reportMeditationData = ReportMeditationDataEntity()
        enterAffectiveCloudManager?.getBiodataReport(object : Callback2<HashMap<Any, Any?>> {
            override fun onError(error: Error?) {
            }

            override fun onSuccess(t: HashMap<Any, Any?>?) {
//                Logger.d("report bio is " + t.toString())
                if (t == null) {
                    return
                }
                val deviceType = SettingManager.getInstance().deviceType
                if (deviceType == DEVICE_TYPE_CUSHION){
                    var peprMap = t!!["pepr"] as Map<Any, Any?>?
                    if (peprMap != null){
                        var reportPEPRDataEntity = ReportPEPRDataEntity()
                        if (peprMap!!.containsKey("hr_avg")) {
                            reportPEPRDataEntity.hrAvg = peprMap["hr_avg"] as Double
                        }
                        if (peprMap!!.containsKey("hr_max")) {
                            reportPEPRDataEntity.hrMax = peprMap["hr_max"] as Double
                        }
                        if (peprMap!!.containsKey("hr_min")) {
                            reportPEPRDataEntity.hrMin = peprMap["hr_min"] as Double
                        }
                        if (peprMap!!.containsKey("hr_rec")) {
                            reportPEPRDataEntity.hrRec = peprMap["hr_rec"] as ArrayList<Double>
                        }
                        if (peprMap!!.containsKey("hrv_rec")) {
                            reportPEPRDataEntity.hrvRec = peprMap["hrv_rec"] as ArrayList<Double>
                        }
                        if (peprMap!!.containsKey("hrv_avg")) {
                            reportPEPRDataEntity.hrvAvg = peprMap["hrv_avg"] as Double
                        }
                        if (peprMap!!.containsKey("rr_rec")) {
                            reportPEPRDataEntity.rrRec = peprMap["rr_rec"] as ArrayList<Double>
                        }
                        if (peprMap!!.containsKey("rr_avg")) {
                            reportPEPRDataEntity.rrAvg = peprMap["rr_avg"] as Double
                        }
                        reportMeditationData.reportPEPRDataEntity = reportPEPRDataEntity
                    }
                }else{
                    var hrMap = t!!["hr-v2"] as Map<Any, Any?>?
                    if (hrMap != null){
                        var reportHRDataEntity = ReportHRDataEntity()
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
                    }
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
                }

                enterAffectiveCloudManager?.getAffectiveDataReport(object :
                    Callback2<HashMap<Any, Any?>> {
                    override fun onError(error: Error?) {
                    }

                    override fun onSuccess(t: HashMap<Any, Any?>?) {
//                        Logger.d("report affectve is " + t.toString())
                        if (t == null) {
                            return
                        }
                        val deviceType = SettingManager.getInstance().deviceType
                        if (deviceType == DEVICE_TYPE_CUSHION){
                            var reportPressureEnitty = ReportPressureEnitty()
                            var pressureMap = t["pressure"]
                            if (pressureMap != null) {
                                pressureMap = pressureMap as Map<Any, Any?>
                                if (pressureMap.containsKey("pressure_avg")) {
                                    reportPressureEnitty.pressureAvg =
                                        pressureMap["pressure_avg"] as Double
                                }
                                if (pressureMap.containsKey("pressure_rec")) {
                                    reportPressureEnitty.pressureRec =
                                        pressureMap["pressure_rec"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportPressureEnitty = reportPressureEnitty
                        }else{
                            var reportAttentionEnitty = ReportAttentionEnitty()
                            var attentionMap = t["attention"]
                            if (attentionMap != null) {
                                attentionMap = attentionMap as Map<Any, Any?>
                                if (attentionMap.containsKey("attention_avg")) {
                                    Logger.d("attention avg is " + attentionMap["attention_avg"] as Double)
                                    reportAttentionEnitty.attentionAvg =
                                        attentionMap["attention_avg"] as Double
                                }
                                if (attentionMap.containsKey("attention_rec")) {
                                    reportAttentionEnitty.attentionRec =
                                        attentionMap["attention_rec"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportAttentionEnitty = reportAttentionEnitty
                            var reportRelxationEntity = ReportRelaxationEnitty()
                            var relaxationMap = t["relaxation"]
                            if (relaxationMap != null) {
                                relaxationMap = relaxationMap as Map<Any, Any?>
                                if (relaxationMap.containsKey("relaxation_avg")) {
                                    reportRelxationEntity.relaxationAvg =
                                        relaxationMap["relaxation_avg"] as Double
                                }
                                if (relaxationMap.containsKey("relaxation_rec")) {
                                    reportRelxationEntity.relaxationRec =
                                        relaxationMap["relaxation_rec"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportRelaxationEnitty = reportRelxationEntity

                            var reportPressureEnitty = ReportPressureEnitty()
                            var pressureMap = t["pressure"]
                            if (pressureMap != null) {
                                pressureMap = pressureMap as Map<Any, Any?>
                                if (pressureMap.containsKey("pressure_avg")) {
                                    reportPressureEnitty.pressureAvg =
                                        pressureMap["pressure_avg"] as Double
                                }
                                if (pressureMap.containsKey("pressure_rec")) {
                                    reportPressureEnitty.pressureRec =
                                        pressureMap["pressure_rec"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportPressureEnitty = reportPressureEnitty

                            var reportPleasureEnitty = ReportPleasureEnitty()
                            var pleasureMap = t["pleasure"]
                            if (pleasureMap != null) {
                                pleasureMap = pleasureMap as Map<Any, Any?>
                                if (pleasureMap.containsKey("pleasure_avg")) {
                                    reportPleasureEnitty.pleasureAvg =
                                        pleasureMap["pleasure_avg"] as Double
                                }
                                if (pleasureMap.containsKey("pleasure_rec")) {
                                    reportPleasureEnitty.pleasureRec =
                                        pleasureMap["pleasure_rec"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportPleasureEnitty = reportPleasureEnitty

                            var reportSleepEntity = ReportSleepEnitty()
                            var sleepMap = t["sleep"]
                            if (sleepMap != null) {
                                sleepMap = sleepMap as Map<Any, Any?>
                                if (sleepMap.containsKey("sleep_point")) {
                                    reportSleepEntity.sleepPoint =
                                        sleepMap["sleep_point"] as Double
                                }
                                if (sleepMap.containsKey("sleep_latency")) {
                                    reportSleepEntity.sleepLatency =
                                        sleepMap["sleep_latency"] as Double
                                }
                                if (sleepMap.containsKey("awake_duration")) {
                                    reportSleepEntity.soberDuration =
                                        sleepMap["awake_duration"] as Double
                                }
                                if (sleepMap.containsKey("light_duration")) {
                                    reportSleepEntity.lightDuration =
                                        sleepMap["light_duration"] as Double
                                }
                                if (sleepMap.containsKey("deep_duration")) {
                                    reportSleepEntity.deepDuration =
                                        sleepMap["deep_duration"] as Double
                                }
                                if (sleepMap.containsKey("sleep_curve")) {
                                    reportSleepEntity.sleepCurve =
                                        sleepMap["sleep_curve"] as ArrayList<Double>
                                }
                            }
                            reportMeditationData.reportSleepEntity = reportSleepEntity

                        }

                        listener.invoke(reportMeditationData)
                    }

                })
            }

        })
    }

    fun submit(remark: List<RecData>, callback: Callback){
        enterAffectiveCloudManager?.submit(remark,callback)
    }

    fun release() {
        enterAffectiveCloudManager?.release(object : Callback {
            override fun onError(error: Error?) {
                enterAffectiveCloudManager?.closeWebSocket()
            }
            override fun onSuccess() {
                enterAffectiveCloudManager?.closeWebSocket()
            }
        })
    }

    fun closeWebSocket(){
        enterAffectiveCloudManager?.closeWebSocket()
    }

    fun isConnected(): Boolean {
        return enterAffectiveCloudManager != null && enterAffectiveCloudManager!!.isWebSocketOpen()
    }

    fun addWebSocketConnectListener(listener: () -> Unit) {
        enterAffectiveCloudManager?.addWebSocketConnectListener(listener)
    }

    fun addWebSocketDisconnectListener(listener: (String) -> Unit) {
        enterAffectiveCloudManager?.addWebSocketDisconnectListener(listener)
    }

    fun removeWebSocketConnectListener(listener: () -> Unit) {
        enterAffectiveCloudManager?.removeWebSocketConnectListener(listener)
    }

    fun removeWebSocketDisconnectListener(listener: (String) -> Unit) {
        enterAffectiveCloudManager?.removeWebSocketDisconnectListener(listener)
    }
}