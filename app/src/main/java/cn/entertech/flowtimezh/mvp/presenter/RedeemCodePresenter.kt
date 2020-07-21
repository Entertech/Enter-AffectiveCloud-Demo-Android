package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.RedeemCodeEntity
import cn.entertech.flowtimezh.mvp.view.ReddemCodeView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class RedeemCodePresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mReddemCodeView: ReddemCodeView? = null
    var mRedeemCodeEntity: RedeemCodeEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mReddemCodeView = view as ReddemCodeView
    }

    fun useRedeemCode(userId: Int, redeemCode: String) {
        RetrofitHelper.getInstance(mContext).getServer()
            .useRedeemCode(SettingManager.getInstance().token, userId, redeemCode, "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<RedeemCodeEntity>> {
                override fun onComplete() {
                    if (mRedeemCodeEntity != null) {
                        mReddemCodeView?.onSuccess(mRedeemCodeEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<RedeemCodeEntity>) {
                    mRedeemCodeEntity = t.body()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mReddemCodeView?.onError(e.toString())
                }
            })

    }
}