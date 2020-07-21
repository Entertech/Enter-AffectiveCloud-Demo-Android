package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.AppVersionEntity
import cn.entertech.flowtimezh.mvp.view.AppUpdateView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class AppUpdatePresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mAppUpdateView: AppUpdateView? = null
    var mAppVersionEntity: List<AppVersionEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mAppUpdateView = view as AppUpdateView
    }

    fun getAppVersionInfo() {
        RetrofitHelper.getInstance(mContext).getServer()
            .getAppVersionInfo(SettingManager.getInstance().token, "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<AppVersionEntity>>> {
                override fun onComplete() {
                    mAppUpdateView?.onSuccess(mAppVersionEntity)
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<AppVersionEntity>>) {
                    if (t.code() == 200) {
                        mAppVersionEntity = t.body() as List<AppVersionEntity>
                    } else {
                        mAppUpdateView?.onError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAppUpdateView?.onError(e.toString())
                }
            })

    }
}