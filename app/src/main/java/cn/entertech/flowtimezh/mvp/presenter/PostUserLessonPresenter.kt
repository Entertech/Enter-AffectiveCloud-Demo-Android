package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.MeditationEntity
import cn.entertech.flowtimezh.mvp.model.UserLessonEntity
import cn.entertech.flowtimezh.mvp.view.PostUserLessonView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class PostUserLessonPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mPostUserLessonView: PostUserLessonView? = null
    var mUserLessonEntity: UserLessonEntity? = null
    var mMeditationEntity: Response<MeditationEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mPostUserLessonView = view as PostUserLessonView
    }

    fun postMeditation(
        startTime: String?,
        finishTime: String?,
        attentionAvg: Float?,
        attentionMax: Float?,
        attentionMin: Float?,
        heartRateAvg: Float?,
        heartRateMax: Float?,
        heartRateMin: Float?,
        hrvAvg: Float?,
        relaxationAvg: Float?,
        relaxationMax: Float?,
        realaxationMin: Float?,
        pressureAvg: Float?,
        userId: Int,
        acSessionId: String
    ) {
        RetrofitHelper.getInstance(mContext).getServer()
            .postMeditation(
                SettingManager.getInstance().token,
                startTime,
                finishTime,
                attentionAvg,
                attentionMax,
                attentionMin,
                heartRateAvg,
                heartRateMax,
                heartRateMin,
                hrvAvg,
                relaxationAvg,
                relaxationMax,
                realaxationMin,
                pressureAvg,
                userId,
                acSessionId
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<MeditationEntity>> {
                override fun onComplete() {
                    if (mMeditationEntity != null && mMeditationEntity!!.code() >= 200 && mMeditationEntity!!.code() < 300) {
                        mPostUserLessonView?.onPostMeditationSuccess(mMeditationEntity!!.body())
                    } else {
                        mPostUserLessonView?.onPostMeditationFailed("${mMeditationEntity?.code()}:${mMeditationEntity?.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<MeditationEntity>) {
                    mMeditationEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mPostUserLessonView?.onPostMeditationFailed(e.toString())
                }

            })
    }

    fun putMeditation(meditationId: Int?, fileUri: String?,sessionId:String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .putMeditation(SettingManager.getInstance().token, meditationId, fileUri,sessionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<MeditationEntity>> {
                override fun onComplete() {
                    if (mMeditationEntity != null && mMeditationEntity!!.code() >= 200 && mMeditationEntity!!.code() < 300) {
                        mPostUserLessonView?.onPutMeditaionSuccess(mMeditationEntity!!.body())
                    } else {
                        mPostUserLessonView?.onPutMeditationFailed("${mMeditationEntity?.code()}:${mMeditationEntity?.message()}")

                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<MeditationEntity>) {
                    mMeditationEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mPostUserLessonView?.onPutMeditationFailed(e.toString())
                }

            })
    }


    fun postUserLesson(
        startTime: String?,
        finishTime: String?,
        lessonId: Int?,
        courseId: Int?,
        meditationId: Int?,
        userId: Int?
    ) {
        RetrofitHelper.getInstance(mContext).getServer()
            .postUserLesson(
                SettingManager.getInstance().token,
                startTime,
                finishTime,
                lessonId,
                courseId,
                meditationId,
                userId
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<UserLessonEntity>> {
                override fun onComplete() {
                    if (mUserLessonEntity != null) {
                        mPostUserLessonView?.onPostUserLessonSuccess(mUserLessonEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<UserLessonEntity>) {
                    if (t.code() != 201) {
                        mPostUserLessonView?.onPostUserLessonFailed("${t.code()}:${t.message()}")
                    } else {
                        mUserLessonEntity = t.body()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mPostUserLessonView?.onPostUserLessonFailed(e.toString())
                }

            })
    }
}