package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.NotificationTokenEntity
import cn.entertech.flowtimezh.mvp.view.UserNotificationTokenView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class UserNotificationTokenPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mUserNotificationTokenView: UserNotificationTokenView? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mUserNotificationTokenView = view as UserNotificationTokenView
    }

    fun getNotificationToken() {
        RetrofitHelper.getInstance(mContext).getServer().getNotificationToken(SettingManager.getInstance().token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<NotificationTokenEntity>>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<NotificationTokenEntity>>) {
                    if (t.code() == 200) {
                        mUserNotificationTokenView?.onGetTokenSuccess(t.body() as List<NotificationTokenEntity>)
                    } else {
                        mUserNotificationTokenView?.onGeteTokenError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mUserNotificationTokenView?.onGeteTokenError("${e.message}")
                }
            })
    }

    fun postNotificationToken(userId: Int, notificationToken: String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .postNotificationToken(SettingManager.getInstance().token, userId, notificationToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<NotificationTokenEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<NotificationTokenEntity>) {
                    if (t.code() == 200) {
                        mUserNotificationTokenView?.onPostTokenSuccess(t.body() as NotificationTokenEntity)
                    } else {
                        mUserNotificationTokenView?.onPostTokenError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mUserNotificationTokenView?.onPostTokenError("${e.message}")
                }
            })
    }

    fun updateNotificationToken(tokenId: Int, userId: Int, notificationToken: String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .updateNotificationToken(SettingManager.getInstance().token, tokenId, userId, notificationToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<NotificationTokenEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<NotificationTokenEntity>) {
                    if (t.code() == 200) {
                        mUserNotificationTokenView?.onUpdateTokenSuccess(t.body() as NotificationTokenEntity)
                    } else {
                        mUserNotificationTokenView?.onUpdateTokenError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mUserNotificationTokenView?.onUpdateTokenError("${e.message}")
                }
            })
    }
}