package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.AuthorEntity
import cn.entertech.flowtimezh.mvp.view.AuthorInfoView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class AuthorPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mAuthorInfoView: AuthorInfoView? = null
    var mAuthorEntity: AuthorEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mAuthorInfoView = view as AuthorInfoView
    }

    fun getAuthorsInfo(authorId: Int) {
        RetrofitHelper.getInstance(mContext).getServer().getAuthorsInfo(SettingManager.getInstance().token, authorId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<AuthorEntity> {
                override fun onComplete() {
                    if (mAuthorEntity != null) {
                        mAuthorInfoView?.onSuccess(mAuthorEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: AuthorEntity) {
                    mAuthorEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mAuthorInfoView?.onError(e.toString())
                }
            })

    }
}