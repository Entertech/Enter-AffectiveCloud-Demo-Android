package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.CollectionEntity


interface CollectionView : View {
    fun onSuccess(collectionEntity: CollectionEntity)
    fun onError(error: String)
}