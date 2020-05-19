package cn.entertech.affectiveclouddemo.mvp

import cn.entertech.flowtime.mvp.model.LogAuthResult
import cn.entertech.flowtime.mvp.model.LogPostResult
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface LogService {
    @POST("auth/token/")
    @FormUrlEncoded
    fun logAuth(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<Response<LogAuthResult>>


    @POST("log/")
    @FormUrlEncoded
    fun logPost(
        @Header("Authorization") authorization: String?,
        @Field("app") app: String,
        @Field("platform") platform: String,
        @Field("version") version: String,
        @Field("user_id") user_id: String,
        @Field("date") date: String,
        @Field("event") event: String,
        @Field("message") message: String
    ): Observable<Response<LogPostResult>>
}