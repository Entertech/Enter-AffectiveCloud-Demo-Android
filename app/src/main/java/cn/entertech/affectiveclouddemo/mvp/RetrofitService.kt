package cn.entertech.affectiveclouddemo.mvp

import cn.entertech.flowtime.mvp.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    /**
     * 获取固件版本信息
     * @param authorization String
     * @return Observable<Response<List<FirmwareVersionEntity>>>
     */
    @GET("/api/v0.1/firmware_version")
    fun getFirmwareVersion(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<FirmwareVersionEntity>>>

    /**
     * 获取app版本信息
     * @param authorization String
     * @param platform String
     * @return Observable<Response<List<AppVersionEntity>>>
     */
    @GET("/api/v0.1/app_version")
    fun getAppVersionInfo(
        @Header("Authorization") authorization: String,
        @Query("platform") platform: String
    ): Observable<Response<List<AppVersionEntity>>>

}