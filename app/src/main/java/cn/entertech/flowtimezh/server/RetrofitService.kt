package cn.entertech.flowtime.mvp

import cn.entertech.flowtimezh.entity.AuthEntity
import cn.entertech.flowtimezh.entity.LabelsEntity
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    /**
     * 授权
     */
    @FormUrlEncoded
    @POST("api-token-auth/")
    fun auth(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<Response<AuthEntity>>

    /**
     * 获取实验标签
     */
    @GET("v1/dataLabelCase/")
    fun getExperimentLabels(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<LabelsEntity>>>

}