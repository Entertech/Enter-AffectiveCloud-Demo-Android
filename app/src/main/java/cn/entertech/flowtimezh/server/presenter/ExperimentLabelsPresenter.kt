package cn.entertech.flowtimezh.server.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import cn.entertech.flowtime.mvp.RetrofitHelper
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.entity.AuthEntity
import cn.entertech.flowtimezh.entity.LabelsEntity
import cn.entertech.flowtimezh.server.view.AuthView
import cn.entertech.flowtimezh.server.view.ExperimentLabelsView
import cn.entertech.flowtimezh.server.view.View
import cn.entertech.flowtimezh.ui.activity.AuthActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ExperimentLabelsPresenter(var context: Context) : Presneter {
    lateinit var mCompositeDisposable: CompositeDisposable
    var mExperimentLabelsView: ExperimentLabelsView? = null
    var response: Response<List<LabelsEntity>>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {

    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mExperimentLabelsView = view as ExperimentLabelsView
    }

    fun getExperimentLabels() {
        RetrofitHelper.getInstance(context).getServer()
            .getExperimentLabels("JWT ${SettingManager.getInstance().token}")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<LabelsEntity>>> {
                override fun onComplete() {
                    if (response != null && response!!.code() == 200) {
                        mExperimentLabelsView?.onSuccess(response!!.body())
                    } else if (response != null && response!!.code() == 401) {
                        context.startActivity(Intent(context, AuthActivity::class.java))
                        (context as Activity).finish()
                        mExperimentLabelsView?.onError("Token失效，请重新登录")
                    } else {
                        mExperimentLabelsView?.onError("${response?.code()}:${response?.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<LabelsEntity>>) {
                    response = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mExperimentLabelsView?.onError(e.toString())
                }

            })
    }
}