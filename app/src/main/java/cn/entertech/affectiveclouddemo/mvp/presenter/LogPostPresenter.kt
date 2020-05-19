package cn.entertech.affectiveclouddemo.mvp.presenter

import android.content.Context
import cn.entertech.affectiveclouddemo.app.SettingManager
import cn.entertech.affectiveclouddemo.mvp.RetrofitHelper
import cn.entertech.affectiveclouddemo.mvp.view.LogPostView
import cn.entertech.affectiveclouddemo.mvp.view.View
import cn.entertech.flowtime.mvp.model.LogPostResult
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class LogPostPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mLogPostView: LogPostView? = null
    var mLogPostResultResponse: Response<LogPostResult>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mLogPostView = view as LogPostView
    }

    fun logPost(
        app: String,
        platform: String,
        version: String,
        user_id: String,
        date: String,
        event: String,
        message: String
    ) {
        RetrofitHelper.getInstance(mContext).getLogServer()
            .logPost(SettingManager.getInstance().logToken,app, platform, version, user_id, date, event, message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<LogPostResult>> {
                override fun onComplete() {
                    if (mLogPostResultResponse != null && mLogPostResultResponse!!.code() >= 200 && mLogPostResultResponse!!.code() < 300) {
                        mLogPostView?.onSuccess(mLogPostResultResponse!!.body())
                    } else {
                        mLogPostView?.onError("${mLogPostResultResponse!!.code()}:${mLogPostResultResponse!!.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<LogPostResult>) {
                    mLogPostResultResponse = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mLogPostView?.onError(e.toString())
                }
            })

    }
}