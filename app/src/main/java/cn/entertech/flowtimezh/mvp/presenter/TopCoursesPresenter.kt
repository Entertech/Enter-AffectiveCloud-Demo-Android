package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.CourseEntity
import cn.entertech.flowtimezh.mvp.view.TopCoursesView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class TopCoursesPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mTopCoursesView: TopCoursesView? = null
    var mCourseEntity: CourseEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mTopCoursesView = view as TopCoursesView
    }

    fun getTopCourses(pageCount:Int = 0,pageIndex:Int = 0) {
        RetrofitHelper.getInstance(mContext).getServer().getTopCourses(SettingManager.getInstance().token,pageCount,pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CourseEntity> {
                override fun onComplete() {
                    if (mCourseEntity != null) {
                        mTopCoursesView?.onSuccess(mCourseEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: CourseEntity) {
                    mCourseEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mTopCoursesView?.onError(e.toString())
                }

            })
    }
}