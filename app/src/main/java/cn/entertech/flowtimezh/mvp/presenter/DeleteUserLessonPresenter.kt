package cn.entertech.flowtimezh.mvp.presenter
import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.view.DeleteUserLessonView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response


class DeleteUserLessonPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mDeleteUserLessonView: DeleteUserLessonView? = null
    var mResult: Response<ResponseBody>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mDeleteUserLessonView = view as DeleteUserLessonView
    }

    fun deleteUserLesson(id: Long) {
        RetrofitHelper.getInstance(mContext).getServer()
            .deleteUserLessons(SettingManager.getInstance().token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onComplete() {
                    if (mResult != null && mResult!!.code() == 204) {
                        mDeleteUserLessonView?.onSuccess(mResult!!.body(),id)
                    } else {
                        mDeleteUserLessonView?.onError("delete fail")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<ResponseBody>) {
                    mResult = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mDeleteUserLessonView?.onError(e.toString())
                }

            })

    }
}