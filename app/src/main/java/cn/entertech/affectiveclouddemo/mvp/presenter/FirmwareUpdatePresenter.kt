package cn.entertech.affectiveclouddemo.mvp.presenter

import android.content.Context
import cn.entertech.affectiveclouddemo.mvp.RetrofitHelper
import cn.entertech.affectiveclouddemo.mvp.view.FirmwareUpdateView
import cn.entertech.affectiveclouddemo.mvp.view.View
import cn.entertech.flowtime.mvp.model.FirmwareVersionEntity
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class FirmwareUpdatePresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mFirmwareUpdateView: FirmwareUpdateView? = null
    var mFirmwareVersionEntity: List<FirmwareVersionEntity>? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mFirmwareUpdateView = view as FirmwareUpdateView
    }

    fun getFirmwareVersion() {
        RetrofitHelper.getInstance(mContext).getServer().getFirmwareVersion("token???")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<List<FirmwareVersionEntity>>> {
                override fun onComplete() {
                    mFirmwareUpdateView?.onSuccess(mFirmwareVersionEntity)
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: Response<List<FirmwareVersionEntity>>) {
                    if (t.code() == 200) {
                        mFirmwareVersionEntity = t.body() as List<FirmwareVersionEntity>
                    } else {
                        mFirmwareUpdateView?.onError("${t.code()}:${t.message()}")
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mFirmwareUpdateView?.onError(e.toString())
                }
            })

    }
}