package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.RedeemCodeEntity


interface ReddemCodeView : View {
    fun onSuccess(userRedeemCodeEntity: RedeemCodeEntity)
    fun onError(error: String)
}