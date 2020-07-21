package cn.entertech.flowtimezh.mvp.presenter

import android.content.Context
import cn.entertech.flowtimezh.mvp.RetrofitHelper
import cn.entertech.flowtimezh.mvp.model.CollectionEntity
import cn.entertech.flowtimezh.mvp.view.CollectionView
import cn.entertech.flowtimezh.mvp.view.View
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class CollectionPresenter(context: Context) : Presneter {
    var mContext = context
    lateinit var mCompositeDisposable: CompositeDisposable
    var mCollectionView: CollectionView? = null
    var collectionEntity: CollectionEntity? = null
    override fun onCreate() {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.dispose()
    }

    override fun attachView(view: View) {
        mCollectionView = view as CollectionView
    }

    fun getCollectionList(auth:String,pageCount:Int = 0,pageIndex:Int = 0) {
        RetrofitHelper.getInstance(mContext).getServer().getCollectionList(auth, pageCount,pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CollectionEntity> {
                override fun onComplete() {
                    if (collectionEntity != null) {
                        mCollectionView?.onSuccess(collectionEntity!!)
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: CollectionEntity) {
                    collectionEntity = t
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mCollectionView?.onError(e.toString())
                }
            })

    }
}