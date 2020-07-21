package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.LessonEntity
import cn.entertech.flowtimezh.mvp.view.LessonListView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LessonListPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mLessonListView: LessonListView? = null
    var mLessonListEntity: List<LessonEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mLessonListView = view as LessonListView
    }

    fun getLessonList(courseId:Int) {
        RetrofitHelper.getInstance(mContext).getServer().getLessonListByCourseId(SettingManager.getInstance().token, courseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<LessonEntity>> {
                override fun onComplete() {
                    if (mLessonListEntity != null) {
                        mLessonListView?.onSuccess(mLessonListEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: List<LessonEntity>) {
                    mLessonListEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mLessonListView?.onError(e.toString())
                }

            })

    }
}