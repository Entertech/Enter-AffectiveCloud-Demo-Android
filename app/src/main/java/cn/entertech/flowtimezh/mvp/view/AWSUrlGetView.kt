package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.AWSFileUrlEntity


interface AWSUrlGetView : View {
    fun onRequestPostUrlSuccess(awsFileUrlEntity: AWSFileUrlEntity, meditationId:Long?)
    fun onRequestPostUrlError(error: String)
    fun onRequestGetUrlSuccess(awsFileUrlEntity: AWSFileUrlEntity,meditationId:Long?)
    fun onRequestGetUrlError(error: String)
}