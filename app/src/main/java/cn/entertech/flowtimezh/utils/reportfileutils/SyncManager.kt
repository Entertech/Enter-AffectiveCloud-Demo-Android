package cn.entertech.flowtimezh.utils.reportfileutils

import android.util.Log
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.database.MeditationDao
import cn.entertech.flowtimezh.database.UserLessonRecordDao
import cn.entertech.flowtimezh.mvp.model.AWSFileUrlEntity
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.mvp.presenter.AWFUrlGetPresenter
import cn.entertech.flowtimezh.mvp.presenter.GetUserLessonPresenter
import cn.entertech.flowtimezh.mvp.presenter.PostUserLessonPresenter
import cn.entertech.flowtimezh.mvp.presenter.ReportFilePostPresenter
import cn.entertech.flowtimezh.mvp.view.AWSUrlGetView
import cn.entertech.flowtimezh.mvp.view.GetUserLessonView
import cn.entertech.flowtimezh.mvp.view.PostUserLessonView
import cn.entertech.flowtimezh.mvp.view.ReportFilePostView
import cn.entertech.flowtimezh.utils.ToastUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.orhanobut.logger.Logger
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

class SyncManager {
    private var fileDownloadFlag = false
    private var fileDownloadStart: (() -> Unit)? = null
    private var fileDownloadEnd: (() -> Unit)? = null
    private var fileDownloadCount = 0
    private var postUserLessonSuccess: (() -> Unit)? = null
    var getUserLessonView = object : GetUserLessonView {
        override fun onGetUserLessonsSuccess(userLessons: List<UserLessonEntity>) {

//            Logger.d("upload report onGetUserLessonsSuccess")
            for (record in userLessons) {
                record.lessonId = record.lesson.id
                record.courseId = record.course.id
                record.lessonName = record.lesson.name
                record.courseName = record.course.name
                record.courseImage = record.course.image
                record.startTime = record.startTime.replace("T", " ").replace("Z", "")
                record.isSampleData = false
                userLessonRecordDao!!.create(record)
                if (record.meditation > 0) {
                    var meditation = meditationDao!!.findMeditationById(record.meditation)
                    if (meditation == null || !meditation.isFileGet) {
                        getUserLessonPresenter?.getMeditationById(record.meditation.toInt())
                    }
                }
            }
            getRecordsSuccessCallback?.invoke()
        }

        override fun onGetUserLessonsFailed(error: String) {
        }

        override fun onGetMeditationSuccess(meditationEntity: MeditationEntity) {

//            Logger.d("upload report onGetMeditationSuccess")
            meditationDao!!.create(meditationEntity)
            if (meditationEntity.meditationFile != null) {
                awsFileUrlPresenter?.requestAwsGetFileUrl(
                    meditationEntity.meditationFile,
                    meditationEntity.id
                )
            }
        }

        override fun onGetMeditationFailed(error: String) {
        }

    }

    var postUserLessonView = object : PostUserLessonView {
        override fun onPostMeditationSuccess(meditationEntity: MeditationEntity?) {
//            Logger.d("upload report onPostMeditationSuccess")
            if (meditationEntity == null) {
                return
            }
            var startTime = meditationEntity.startTime.replace("T", " ").replace("Z", "")
            var meditation =
                meditationDao!!.findMeditationByStartTime(meditationEntity.user, startTime)
            var userLesson =
                userLessonRecordDao!!.findRecordByMeditationId(meditationEntity.user, meditation.id)
            if (userLesson == null) {
                userLesson = userLessonRecordDao!!.findRecordByMeditationId(
                    meditationEntity.user,
                    meditationEntity.id
                )
            }
            if (userLesson == null) {
                return
            }
            meditationDao!!.updateMeditationId(
                meditationEntity.user,
                startTime,
                meditationEntity.id
            )
            userLessonRecordDao!!.updateMeditationId(userLesson.id, meditationEntity.id)
            meditationEntity?.startTime = startTime
//            Log.d("######","meditation id is ${meditationEntity.id} ,session id is ${meditation.acSessionId}")
            meditationEntity?.acSessionId = meditation.acSessionId
            meditationEntity?.meditationFile = meditation.meditationFile
            meditationDao!!.create(meditationEntity)
            var reportFileUri =
                "${SettingManager.getInstance().userId}/${userLesson!!.courseId}/${userLesson!!.lessonId}/${meditation.startTime}"
            awsFileUrlPresenter?.requestAwsPostFileUrl(reportFileUri, meditationEntity.id)
        }

        override fun onPostMeditationFailed(error: String) {
        }

        override fun onPutMeditaionSuccess(meditationEntity: MeditationEntity?) {

//            Logger.d("upload report onPutMeditaionSuccess")
            if (meditationEntity == null) {
                return
            }
            meditationEntity.isFileUpload = true
            meditationDao!!.create(meditationEntity)
            var userLesson = userLessonRecordDao!!.findRecordByMeditationId(
                meditationEntity.user,
                meditationEntity.id
            )
            postUserLesson(userLesson, meditationEntity.id.toInt())
        }

        override fun onPutMeditationFailed(error: String) {
        }

        override fun onPostUserLessonSuccess(userLessonEntity: UserLessonEntity) {
//            Logger.d("upload report onPostUserLessonSuccess")
            userLessonRecordDao!!.updateRecordId(
                userLessonEntity.user,
                userLessonEntity.startTime,
                userLessonEntity.id
            )
            userLessonEntity.lessonName = userLessonEntity.lesson.name
            userLessonEntity.courseName = userLessonEntity.course.name
            userLessonEntity.courseImage = userLessonEntity.course.image
            userLessonRecordDao!!.create(userLessonEntity)
            postUserLessonSuccess?.invoke()
        }

        override fun onPostUserLessonFailed(error: String) {
        }

    }

    var awsFileUrlView = object : AWSUrlGetView {
        override fun onRequestPostUrlError(error: String) {

        }

        override fun onRequestGetUrlSuccess(
            awsFileUrlEntity: AWSFileUrlEntity,
            meditationId: Long?
        ) {

//            Logger.d("upload report onRequestGetUrlSuccess")
            var downloadlistener = object : FileDownloadListener() {
                override fun warn(task: BaseDownloadTask?) {
//                    Log.d("####", "download warn " + task?.path)
                }

                override fun completed(task: BaseDownloadTask?) {
                    fileDownloadCount++
                    var meditation = meditationDao!!.findMeditationById(meditationId!!)
                    meditation.isFileGet = true
                    meditationDao!!.create(meditation)
                    if (fileDownloadCount > 4 && fileDownloadFlag) {
                        fileDownloadFlag = false
                        fileDownloadEnd?.invoke()
                    }
                }

                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
//                    Log.d("####", "download pending " + task?.path)
                }

                override fun error(task: BaseDownloadTask?, exception: Throwable?) {
//                    Log.d("####", "download audio failed!:" + exception.toString())
//                    ToastUtil.toastShort(activity, "download file failed!:" + exception.toString())
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    var percent = soFarBytes * 1f / totalBytes
//                    Log.d("####", "download percent is " + percent)
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
//                    Log.d("####", "download paused " + task?.path)
                }
            }
            if (meditationId == null) {
                return
            }
            var meditation = meditationDao!!.findMeditationById(meditationId.toLong())
            if (meditation.meditationFile == null) {
                return
            }
            var meditationFileFields = meditation.meditationFile.split("/")
            var startTime = meditationFileFields[meditationFileFields.size - 1]
            var filePath =
                FileUtil.getMeditationReportDir() + File.separator + startTime + FileUtil.getMeditationReportExtention()
            var data = awsFileUrlEntity.data as Map<String, String>
            if (data != null) {
                if (data.containsKey("url") && data["url"] != null) {
                    fileDownloadStart?.invoke()
                    var fileUrl = data["url"]
                    FileDownloader.setup(Application.getInstance())
                    FileDownloader.getImpl().create(fileUrl)
                        .setPath(filePath)
                        .setListener(downloadlistener)
                        .start()
                }
            }
        }

        override fun onRequestGetUrlError(error: String) {
        }

        override fun onRequestPostUrlSuccess(
            awsFileUrlEntity: AWSFileUrlEntity,
            meditationId: Long?
        ) {
//            Logger.d("upload report onRequestPostUrlSuccess")

            if (awsFileUrlEntity.code == 200) {
                var data = awsFileUrlEntity.data
                if (data != null) {
                    var filePostUrl = data["url"] as String
                    var baseUrl = filePostUrl.split("/")[2]
                    var path = filePostUrl.split("/")[3].split("?")[0]
                    var params = filePostUrl.split("/")[3].split("?")[1]
                    var OSSAccessKeyId = params.split("&")[0].split("=")[1]
                    var Expires = params.split("&")[1].split("=")[1]
                    var Signature = params.split("&")[2].split("=")[1]
                    if (meditationId != null) {
                        var meditaiton = meditationDao?.findMeditationById(meditationId)
                        if (meditaiton != null) {
                            reportFilePostPresenter?.postReportFile(
                                "http://$baseUrl/",
                                path,
                                OSSAccessKeyId,
                                Expires,
                                Signature,
                                meditaiton!!.startTime,
                                meditationId = meditationId
                            )
                        }
                    }
                }
            }
        }
    }


    var reportFilePostView = object : ReportFilePostView {
        override fun onError(error: String) {
            ToastUtil.toastShort(Application.getInstance(), error)
        }

        override fun onSuccess(
            responseBody: okhttp3.ResponseBody?,
            meditationId: Long?,
            uri: String?
        ) {

//            Logger.d("upload report reportFilePostViewonSuccess")
            if (meditationId != null) {
//                ToastUtil.toastShort(Application.getInstance(), "report post success!")
                var sessionId = meditationDao?.findMeditationById(meditationId)?.acSessionId
                if (sessionId != null){
                    postUserLessonPresenter?.putMeditation(meditationId.toInt(), uri,sessionId)
                }
            }
        }
    }

    var postUserLessonPresenter: PostUserLessonPresenter? = null
    var getUserLessonPresenter: GetUserLessonPresenter? = null
    var awsFileUrlPresenter: AWFUrlGetPresenter? = null
    var reportFilePostPresenter: ReportFilePostPresenter? = null
    var meditationDao: MeditationDao? = null
    var userLessonRecordDao: UserLessonRecordDao? = null

    init {
        meditationDao = MeditationDao(Application.getInstance())
        userLessonRecordDao = UserLessonRecordDao(Application.getInstance())
        postUserLessonPresenter = PostUserLessonPresenter(Application.getInstance())
        getUserLessonPresenter = GetUserLessonPresenter(Application.getInstance())
        awsFileUrlPresenter = AWFUrlGetPresenter(Application.getInstance())
        reportFilePostPresenter = ReportFilePostPresenter(Application.getInstance())
        postUserLessonPresenter?.onCreate()
        getUserLessonPresenter?.onCreate()
        awsFileUrlPresenter?.onCreate()
        reportFilePostPresenter?.onCreate()
        postUserLessonPresenter?.attachView(postUserLessonView)
        getUserLessonPresenter?.attachView(getUserLessonView)
        awsFileUrlPresenter?.attachView(awsFileUrlView)
        reportFilePostPresenter?.attachView(reportFilePostView)
    }

    companion object {
        @Volatile
        var mInstance: SyncManager? = null

        fun getInstance(): SyncManager {
            if (mInstance == null) {
                synchronized(SyncManager::class.java) {
                    if (mInstance == null) {
                        mInstance = SyncManager()
                    }
                }
            }
            return mInstance!!
        }
    }

    fun uploadRecord(
        userLesson: UserLessonEntity,
        meditation: MeditationEntity? = null,
        postUserLessonSuccess: (() -> (Unit))? = null
    ) {
        this.postUserLessonSuccess = postUserLessonSuccess
        if (meditation != null) {
            postMeditation(meditation)
        } else {
            postUserLesson(userLesson, null)
        }
    }

    fun uploadRecords() {
        var userLessons = userLessonRecordDao!!.listAll(SettingManager.getInstance().userId)
        for (userLesson in userLessons) {
            if (userLesson.id < 0) {
                if (userLesson.meditation == 0L) {
                    uploadRecord(userLesson, null)
                } else if (userLesson.meditation > 0) {
                    var meditation = meditationDao!!.findMeditationById(userLesson.meditation)
                    if (meditation != null && !meditation.isFileUpload) {
                        uploadRecord(userLesson, meditation)
                    } else {
                        uploadRecord(userLesson)
                    }
                } else {
                    var meditation = meditationDao!!.findMeditationById(userLesson.meditation)
                    uploadRecord(userLesson, meditation)
                }
            }
        }
    }

    var getRecordsSuccessCallback: (() -> (Unit))? = null
    fun getRecords(
        getRecordSuccessCallback: (() -> (Unit))? = null,
        fileDownloadStart: (() -> (Unit))? = null,
        fileDownloadEnd: (() -> (Unit))? = null
    ) {
        this.fileDownloadCount = 0
        fileDownloadFlag = true
        this.getRecordsSuccessCallback = getRecordSuccessCallback
        this.fileDownloadEnd = fileDownloadEnd
        this.fileDownloadStart = fileDownloadStart
        getUserLessonPresenter?.getUserLessons()
    }

    fun postUserLesson(userLesson: UserLessonEntity, meditationId: Int?) {
        postUserLessonPresenter?.postUserLesson(
            userLesson.startTime,
            userLesson.finishTime,
            userLesson.lessonId,
            userLesson.courseId,
            meditationId,
            SettingManager.getInstance().userId
        )
    }

    fun postMeditation(meditation: MeditationEntity) {
        postUserLessonPresenter?.postMeditation(
            meditation.startTime,
            meditation.finishTime,
            meditation.attentionAvg,
            meditation.attentionMax,
            meditation.attentionMin,
            meditation.heartRateAvg,
            meditation.heartRateMax,
            meditation.heartRateMin,
            meditation.heartRateVariabilityAvg,
            meditation.relaxationAvg,
            meditation.relaxationMax,
            meditation.relaxationMin,
            meditation.pressureAvg,
            SettingManager.getInstance().userId,
            meditation.acSessionId
        )
    }
}