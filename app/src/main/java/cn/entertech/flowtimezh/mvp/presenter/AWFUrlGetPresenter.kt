package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.AWSFileUrlEntity
import cn.entertech.flowtimezh.mvp.view.AWSUrlGetView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class AWFUrlGetPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mAWSUrlGetView: AWSUrlGetView? = null
    var awsFileUrlEntity: AWSFileUrlEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mAWSUrlGetView = view as AWSUrlGetView
    }

    fun requestAwsPostFileUrl(objpath: String, meditationId: Long? = null) {
        RetrofitHelper.getInstance(mContext).getServer()
            .getAWSPresignedUrl(SettingManager.getInstance().token, "put", objpath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<AWSFileUrlEntity>> {
                override fun onComplete() {
                    if (awsFileUrlEntity != null) {
                        mAWSUrlGetView?.onRequestPostUrlSuccess(awsFileUrlEntity!!, meditationId)
                    } else {
                        mAWSUrlGetView?.onRequestPostUrlError("aws file url get null")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<AWSFileUrlEntity>) {
                    if (t.code() == 200) {
                        awsFileUrlEntity = t.body()
                    } else {
                        mAWSUrlGetView?.onRequestPostUrlError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAWSUrlGetView?.onRequestPostUrlError(e.toString())
                }
            })

    }

    fun requestAwsGetFileUrl(objpath: String, meditationId: Long? = null) {
        RetrofitHelper.getInstance(mContext).getServer()
            .getAWSPresignedUrl(SettingManager.getInstance().token, "get", objpath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<AWSFileUrlEntity>> {
                override fun onComplete() {
                    if (awsFileUrlEntity != null) {
                        mAWSUrlGetView?.onRequestGetUrlSuccess(awsFileUrlEntity!!, meditationId)
                    } else {
                        mAWSUrlGetView?.onRequestGetUrlError("aws file url get null")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<AWSFileUrlEntity>) {
                    if (t.code() == 200) {
                        awsFileUrlEntity = t.body()
                    } else {
                        mAWSUrlGetView?.onRequestGetUrlError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAWSUrlGetView?.onRequestGetUrlError(e.toString())
                }
            })

    }
}