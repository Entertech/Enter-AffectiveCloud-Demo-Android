package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.AuthSocialEntity
import cn.entertech.flowtimezh.mvp.view.AuthSocialView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class AuthSocialPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mAuthSocialView: AuthSocialView? = null
    var authSocialEntity: AuthSocialEntity? = null
    var response: Response<AuthSocialEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mAuthSocialView = view as AuthSocialView
    }

    fun authSocial(clientId: String, socialId: String, socialName:String,socialType: String, socialToken: String) {
        RetrofitHelper.getInstance(mContext).getServer().authSocial(clientId, socialId,socialName, socialType, socialToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<AuthSocialEntity>> {
                override fun onComplete() {
                    if (authSocialEntity != null && response?.code() == 200) {
                        mAuthSocialView?.onSuccess(authSocialEntity!!)
                    } else {
                        mAuthSocialView?.onError("${response?.code()}:${response?.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<AuthSocialEntity>) {
                    response = t
                    authSocialEntity = t.body()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAuthSocialView?.onError(e.toString())
                }

            })
    }
    fun refreshToken(clientId: String, refreshToken: String) {
        RetrofitHelper.getInstance(mContext).getServer().refreshToken(clientId, refreshToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<AuthSocialEntity>> {
                override fun onComplete() {
                    if (authSocialEntity != null && response?.code() == 200) {
                        mAuthSocialView?.onSuccess(authSocialEntity!!)
                    } else {
                        mAuthSocialView?.onError("${response?.code()}:${response?.message()}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<AuthSocialEntity>) {
                    response = t
                    authSocialEntity = t.body()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAuthSocialView?.onError(e.toString())
                }

            })

    }
}