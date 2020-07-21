package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.ReminderSettingEntity
import cn.entertech.flowtimezh.mvp.view.ReminderSettingView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class ReminderSettingPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mReminderSettingView: ReminderSettingView? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mReminderSettingView = view as ReminderSettingView
    }

    fun getReminderSetting() {
        RetrofitHelper.getInstance(mContext).getServer().getReminderSetting(SettingManager.getInstance().token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<ReminderSettingEntity>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<ReminderSettingEntity>>) {
                    if (t.code() == 200) {
                        mReminderSettingView?.onGetReimderSettingSuccess(t.body() as List<ReminderSettingEntity>)
                    } else {
                        mReminderSettingView?.onGetReimderSettingError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mReminderSettingView?.onGetReimderSettingError("${e.message}")
                }
            })
    }

    fun postReminderSetting(userId: Int, enable: Boolean, days: String, time: String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .postReminderSetting(SettingManager.getInstance().token, userId, 1, enable, days, time)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ReminderSettingEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<ReminderSettingEntity>) {
                    if (t.code() == 200) {
                        mReminderSettingView?.onPostReimderSettingSuccess(t.body() as ReminderSettingEntity)
                    } else {
                        mReminderSettingView?.onPostReimderSettingError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mReminderSettingView?.onPostReimderSettingError("${e.message}")
                }
            })
    }

    fun updateReminderSetting(id: Int, userId: Int, enable: Boolean, days: String, time: String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .updateReminderSetting(SettingManager.getInstance().token, id, userId, 1, enable, days, time)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ReminderSettingEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<ReminderSettingEntity>) {
                    if (t.code() == 200) {
                        mReminderSettingView?.onUpdateReimderSettingSuccess(t.body() as ReminderSettingEntity)
                    } else {
                        mReminderSettingView?.onUpdateReimderSettingError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mReminderSettingView?.onUpdateReimderSettingError("${e.message}")
                }
            })
    }
}