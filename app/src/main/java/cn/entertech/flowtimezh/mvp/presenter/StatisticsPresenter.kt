package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.StatisticsEntity
import cn.entertech.flowtimezh.mvp.view.StatisticsView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class StatisticsPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mStatisticsView: StatisticsView? = null
    var mStatisticsEntity: StatisticsEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mStatisticsView = view as StatisticsView
    }

    fun getStatistics() {
        RetrofitHelper.getInstance(mContext).getServer()
            .getStatistics(SettingManager.getInstance().token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<StatisticsEntity>>> {
                override fun onComplete() {
                    mStatisticsView?.onSuccess(mStatisticsEntity)
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<StatisticsEntity>>) {
                    if (t.code() == 200) {
                        if (t.body() != null && (t.body() as List<StatisticsEntity>).isNotEmpty()) {
                            mStatisticsEntity = (t.body() as List<StatisticsEntity>)[0]
                        }
                    } else {
                        mStatisticsView?.onError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mStatisticsView?.onError(e.toString())
                }
            })
    }
}