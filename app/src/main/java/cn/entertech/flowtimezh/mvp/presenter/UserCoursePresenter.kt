package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.UserCourseEntity
import cn.entertech.flowtimezh.mvp.view.UserCourseView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UserCoursePresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mUserCourseView: UserCourseView? = null
    var mUserCourseEntity: UserCourseEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mUserCourseView = view as UserCourseView
    }

    fun getUserCourse(pageCount:Int = 0,pageIndex:Int = 0) {
        RetrofitHelper.getInstance(mContext).getServer().getUserCourse(SettingManager.getInstance().token, pageCount,pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UserCourseEntity> {
                override fun onComplete() {
                    if (mUserCourseEntity != null) {
                        mUserCourseView?.onSuccess(mUserCourseEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: UserCourseEntity) {
                    mUserCourseEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mUserCourseView?.onError(e.toString())
                }
            })

    }
}