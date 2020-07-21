package cn.entertech.flowtimezh.mvp

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface OtherService {
    @PUT("{path}")
    fun postReportFile(
        @Path("path",encoded = true) path:String,
        @Query("OSSAccessKeyId",encoded = true) AWSAccessKeyId: String,
        @Query("Expires",encoded = true) policy: String,
        @Query("Signature",encoded = true) signature: String,
        @Body file: RequestBody
    ): Observable<Response<ResponseBody>>
}