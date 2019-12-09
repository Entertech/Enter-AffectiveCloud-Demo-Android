package cn.entertech.flowtimezh.server.presenter

import android.content.Context
import cn.entertech.flowtime.mvp.RetrofitHelper
import cn.entertech.flowtimezh.entity.AuthEntity
import cn.entertech.flowtimezh.server.view.AuthView
import cn.entertech.flowtimezh.server.view.View
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class AuthPresenter(var context: Context) : Presneter {
    lateinit var mCompositeDisposable: CompositeDisposable
    var mAuthView: AuthView? = null
    var response: Response<AuthEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {

    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mAuthView = view as AuthView
    }


    fun auth(username: String, password: String) {
        RetrofitHelper.getInstance(context).getServer().auth(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<AuthEntity>> {
                override fun onComplete() {
                    if (response != null && response!!.code() == 200) {
                        mAuthView?.onSuccess(response!!.body())
                    } else {
                        mAuthView?.onError("${response?.code()}:${response?.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<AuthEntity>) {
                    response = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAuthView?.onError(e.toString())
                }

            })
    }
}