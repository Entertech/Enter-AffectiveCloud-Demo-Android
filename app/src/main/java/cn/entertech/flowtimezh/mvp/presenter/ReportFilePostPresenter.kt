package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.view.ReportFilePostView
import cn.entertech.flowtimezh.mvp.view.View
import cn.entertech.flowtimezh.utils.reportfileutils.FileUtil
import com.orhanobut.logger.Logger
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.net.URLDecoder
import java.util.*


class ReportFilePostPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mReportFilePostView: ReportFilePostView? = null
    var reponseBody: ResponseBody? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mReportFilePostView = view as ReportFilePostView
    }

    fun postReportFile(
        url: String,
        path: String,
        AWSAccessKeyId: String,
        policy: String,
        signature: String,
        fileName: String, meditationId: Long? = null
    ) {
        var filePath =
            FileUtil.getMeditationReportDir() + File.separator + fileName + FileUtil.getMeditationReportExtention()
        var file = File(filePath)
        if (!file.exists()) {
            Logger.d("upload file failed:file not exist")
        }
        var fileBody = RequestBody.create(null,file)
        var filePart =
            MultipartBody.Part.createFormData(
                "file",
                fileName + FileUtil.getMeditationReportExtention(),
                fileBody
            )
        RetrofitHelper.getInstance(mContext).getOtherServer(url)
            .postReportFile(
                path,
                AWSAccessKeyId,
                policy,
                signature,
                fileBody
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onComplete() {
                    mReportFilePostView?.onSuccess(reponseBody, meditationId, URLDecoder.decode(path,"UTF-8"))
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<ResponseBody>) {
                    if (t.code() != 204) {
                        mReportFilePostView?.onError("${t.code()}:${t.message()}")
                    } else {
                        reponseBody = t.body()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mReportFilePostView?.onError(e.toString())
                }

            })
    }
}