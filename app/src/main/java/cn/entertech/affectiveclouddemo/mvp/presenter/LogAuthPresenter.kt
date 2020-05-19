package cn.entertech.affectiveclouddemo.mvp.presenter

import android.content.Context
import cn.entertech.affectiveclouddemo.mvp.RetrofitHelper
import cn.entertech.affectiveclouddemo.mvp.view.LogAuthView
import cn.entertech.affectiveclouddemo.mvp.view.View
import cn.entertech.flowtime.mvp.model.LogAuthResult
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class LogAuthPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mLogAuthView: LogAuthView? = null
    var mLogAuthResultResponse: Response<LogAuthResult>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mLogAuthView = view as LogAuthView
    }

    fun logAuth(useName: String, password: String) {
        RetrofitHelper.getInstance(mContext).getLogServer()
            .logAuth(useName, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<LogAuthResult>> {
                override fun onComplete() {
                    if (mLogAuthResultResponse != null && mLogAuthResultResponse!!.code() >= 200 && mLogAuthResultResponse!!.code() < 300) {
                        mLogAuthView?.onSuccess(mLogAuthResultResponse!!.body())
                    } else {
                        mLogAuthView?.onError("${mLogAuthResultResponse!!.code()}:${mLogAuthResultResponse!!.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<LogAuthResult>) {
                    mLogAuthResultResponse = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mLogAuthView?.onError(e.toString())
                }
            })

    }
}