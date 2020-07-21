package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.PurchaseEntity
import cn.entertech.flowtimezh.mvp.view.PurchaseView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class PurchasePresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mPurchaseView: PurchaseView? = null
    var mPurchaseEntity: List<PurchaseEntity>? = null

    var response: Response<PurchaseEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mPurchaseView = view as PurchaseView
    }

    fun getPurchase() {
        RetrofitHelper.getInstance(mContext).getServer().getPurchase(SettingManager.getInstance().token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<PurchaseEntity.PurchaseData>>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<PurchaseEntity.PurchaseData>>) {
                    if (t.code() == 200) {
                        mPurchaseView?.onGetSuccess( t.body() as List<PurchaseEntity.PurchaseData>)
                    } else {
                        mPurchaseView?.onGetError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mPurchaseView?.onGetError(e.toString())
                }
            })
    }


    fun createPurchase(purchaseToken: String, productId: String, isSub: Boolean) {
        RetrofitHelper.getInstance(mContext).getServer()
            .createPurchase(SettingManager.getInstance().token, purchaseToken, productId, if (isSub) 1 else 0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<PurchaseEntity>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<PurchaseEntity>) {
                    if (t.code() == 200) {
                        mPurchaseView?.onCreateSuccess(t.body() as PurchaseEntity)
                    } else {
                        mPurchaseView?.onCreateError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mPurchaseView?.onCreateError(e.toString())
                }
            })
    }
}