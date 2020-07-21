package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.mvp.view.GetUserLessonView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class GetUserLessonPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mGetUserLessonView: GetUserLessonView? = null
    var mUserLessons: List<UserLessonEntity>? = null
    var mMeditationEntity: MeditationEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mGetUserLessonView = view as GetUserLessonView
    }

    fun getUserLessons() {
        RetrofitHelper.getInstance(mContext).getServer()
            .getUserLessons(
                SettingManager.getInstance().token
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<UserLessonEntity>>> {
                override fun onComplete() {
                    if (mUserLessons != null) {
                        mGetUserLessonView?.onGetUserLessonsSuccess(mUserLessons!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<UserLessonEntity>>) {
                    if (t.code() != 200) {
                        mGetUserLessonView?.onGetUserLessonsFailed("${t.code()}:${t.message()}")
                    } else {
                        mUserLessons = t.body()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mGetUserLessonView?.onGetUserLessonsFailed(e.toString())
                }

            })
    }

    fun getMeditationById(meditationId: Int) {
        RetrofitHelper.getInstance(mContext).getServer()
            .getMeditationDetail(
                SettingManager.getInstance().token
                , meditationId
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<MeditationEntity>> {
                override fun onComplete() {
                    if (mMeditationEntity != null) {
                        mGetUserLessonView?.onGetMeditationSuccess(mMeditationEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<MeditationEntity>) {
                    if (t.code() != 200) {
                        mGetUserLessonView?.onGetMeditationFailed("${t.code()}:${t.message()}")
                    } else {
                        mMeditationEntity = t.body()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mGetUserLessonView?.onGetMeditationFailed(e.toString())
                }

            })
    }
}