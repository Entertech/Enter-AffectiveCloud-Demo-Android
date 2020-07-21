package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.PurchaseEntity

interface PurchaseView : View {
    fun onCreateSuccess(purchaseEntity: PurchaseEntity?)
    fun onCreateError(error: String?)
    fun onGetSuccess(purchaseEntity: List<PurchaseEntity.PurchaseData>?)
    fun onGetError(error: String?)
}