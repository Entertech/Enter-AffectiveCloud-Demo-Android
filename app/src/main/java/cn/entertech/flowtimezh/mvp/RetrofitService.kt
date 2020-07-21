package cn.entertech.flowtimezh.mvp

import cn.entertech.flowtimezh.mvp.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
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

    /**
     * 三方权限验证
     */
    @FormUrlEncoded
    @POST("auth/social/")
    fun authSocial(
        @Field("client_id") clientId: String,
        @Field("social_id") socialId: String,
        @Field("social_name") socialName: String,
        @Field("social_type") socialType: String,
        @Field("social_token") socialToken: String
    ): Observable<Response<AuthSocialEntity>>

    /**
     * 刷新token
     */
    @FormUrlEncoded
    @POST("auth/refresh-token/")
    fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") socialId: String
    ): Observable<Response<AuthSocialEntity>>

    /**
     * 获取专题列表
     */
    @GET("collections/")
    fun getCollectionList(
        @Header("Authorization") authorization: String,
        @Query("limit") pageCount: Int,
        @Query("offset") pageIndex: Int
    ): Observable<CollectionEntity>


    /**
     * 获取课程中的lesson列表
     */
    @GET("courses/{id}/lessons/")
    fun getLessonListByCourseId(
        @Header("Authorization") authorization: String,
        @Path("id") courseId: Int
    ): Observable<List<LessonEntity>>


    /**
     * 获取collection中的course列表
     */
    @GET("collections/{id}/courses/")
    fun getCourseListByCollectionId(
        @Header("Authorization") authorization: String,
        @Path("id") courseId: Int,
        @Query("limit") pageCount: Int,
        @Query("offset") pageIndex: Int
    ): Observable<CourseEntity>


    /**
     * 获取作者信息
     */
    @GET("authors/{id}/")
    fun getAuthorsInfo(
        @Header("Authorization") authorization: String,
        @Path("id") authorId: Int
    ): Observable<AuthorEntity>


    /**
     * 获取用户以上课程
     */
    @GET("user/courses/")
    fun getUserCourse(
        @Header("Authorization") authorization: String,
        @Query("limit") pageCount: Int,
        @Query("offset") pageIndex: Int
    ): Observable<UserCourseEntity>


    /**
     * 获取推荐课程
     */
    @GET("top-courses/")
    fun getTopCourses(
        @Header("Authorization") authorization: String,
        @Query("limit") pageCount: Int,
        @Query("offset") pageIndex: Int
    ): Observable<CourseEntity>

    /**
     * 购买商品
     */
    @FormUrlEncoded
    @POST("api/v0.1/purchase/android/")
    fun createPurchase(
        @Header("Authorization") authorization: String,
        @Field("receipt_data") receipt_data: String,
        @Field("product_id") product_id: String,
        @Field("is_subscription") is_subscription: Int
    ): Observable<Response<PurchaseEntity>>

    /**
     * 获取已购买商品
     */
    @GET("api/v0.1/vip/")
    fun getPurchase(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<PurchaseEntity.PurchaseData>>>

    /**
     * 上传体验数据
     */
    @POST("api/v0.1/meditation/")
    @FormUrlEncoded
    fun postMeditation(
        @Header("Authorization") authorization: String?,
        @Field("start_time") startTime: String?,
        @Field("finish_time") finishTime: String?,
        @Field("attention_avg") attentionAvg: Float?,
        @Field("attention_max") attentionMax: Float?,
        @Field("attention_min") attentionMin: Float?,
        @Field("heart_rate_avg") heartRateAvg: Float?,
        @Field("heart_rate_max") heartRateMax: Float?,
        @Field("heart_rate_min") heartRateMin: Float?,
        @Field("heart_rate_variability_avg") hrvAvg: Float?,
        @Field("relaxation_avg") relaxationAvg: Float?,
        @Field("relaxation_max") relaxationMax: Float?,
        @Field("relaxation_min") realaxationMin: Float?,
        @Field("pressure_avg") pressureAvg: Float?,
        @Field("user") userId: Int,
        @Field("ac_session_id") ac_session_id: String
    ): Observable<Response<MeditationEntity>>

    /**
     * 获取体验详情
     */
    @GET("api/v0.1/meditation/{id}/")
    fun getMeditationDetail(
        @Header("Authorization") authorization: String,
        @Path("id") meditationId: Int
    ): Observable<Response<MeditationEntity>>

    /**
     * 更新体验数据文件
     */

    @FormUrlEncoded
    @PUT("api/v0.1/meditation/{meditationId}/")
    fun putMeditation(
        @Header("Authorization") authorization: String,
        @Path("meditationId") meditationId: Int?,
        @Field("meditation_file") meditationFile: String?,
        @Field("ac_session_id") ac_session_id: String
    ): Observable<Response<MeditationEntity>>

    /**
     * 上传体验数据
     */
    @POST("api/v0.1/userLesson/")
    @FormUrlEncoded
    fun postUserLesson(
        @Header("Authorization") authorization: String?,
        @Field("start_time") startTime: String?,
        @Field("finish_time") finishTime: String?,
        @Field("lesson") lessonId: Int?,
        @Field("course") courseId: Int?,
        @Field("meditation") meditationId: Int?,
        @Field("user") userId: Int?
    ): Observable<Response<UserLessonEntity>>

    /**
     * 更新体验课程
     */
    @PUT("api/v0.1/userLesson/{id}/")
    @FormUrlEncoded
    fun putUserLesson(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int?,
        @Field("meditation") meditation: Int?
    ): Observable<Response<UserLessonEntity>>

    /**
     * 获取用户lessons记录
     */
    @GET("api/v0.1/userLesson/")
    fun getUserLessons(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<UserLessonEntity>>>


    /**
     * 删除用户lessons记录
     */
    @DELETE("api/v0.1/userLesson/{id}/")
    fun deleteUserLessons(
        @Header("Authorization") authorization: String,
        @Path("id") userLessonId: Long
    ): Observable<Response<ResponseBody>>

    /**
     * 获取AWS文件url
     *
     * 上传文件：method = "post",objpath = 体验文件路经
     * 下载文件：method = "get",objpath = 体验文件路经
     */
    @GET("api/v0.1/aliyun_presigned_url")
    fun getAWSPresignedUrl(
        @Header("Authorization") authorization: String,
        @Query("method") method: String,
        @Query("objpath") objpath: String
    ): Observable<Response<AWSFileUrlEntity>>

    /**
     * 获取体验统计数据
     */
    @GET("api/v0.1/statistics/")
    fun getStatistics(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<StatisticsEntity>>>

    /**
     * 获取体验统计数据
     */
    @POST("api/v0.1/use_redeem_code/")
    @FormUrlEncoded
    fun useRedeemCode(
        @Header("Authorization") authorization: String,
        @Field("uid") userId: Int?,
        @Field("redeem_code") redeemCode: String?,
        @Field("platform") platform: String?
    ): Observable<Response<RedeemCodeEntity>>

    /**
     * 上传firebase推送通知token
     *
     * @param authorization String
     * @param userId Int?
     * @param redeemCode String?
     * @return Observable<Response<NotificationTokenEntity>>
     */
    @POST("/api/v0.1/userNotificationToken/")
    @FormUrlEncoded
    fun postNotificationToken(
        @Header("Authorization") authorization: String,
        @Field("user") userId: Int?,
        @Field("token") token: String?
    ): Observable<Response<NotificationTokenEntity>>

    /**
     * 获取用户通知token
     * @param authorization String
     * @return Observable<Response<List<NotificationTokenEntity>>>
     */
    @GET("/api/v0.1/userNotificationToken/")
    fun getNotificationToken(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<NotificationTokenEntity>>>

    /**
     * 更新体验课程
     */
    @PUT("/api/v0.1/userNotificationToken/{id}/")
    @FormUrlEncoded
    fun updateNotificationToken(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int?,
        @Field("user") user: Int?,
        @Field("token") token: String?
    ): Observable<Response<NotificationTokenEntity>>

    /**
     * 上传用户通知时间表
     *
     * @param authorization String
     * @param userId Int?
     * @param message Int?
     * @param enable Boolean?
     * @param days_of_the_week String?
     * @param time String?
     * @return Observable<Response<NotificationTokenEntity>>
     */
    @POST("/api/v0.1/userNotification/")
    @FormUrlEncoded
    fun postReminderSetting(
        @Header("Authorization") authorization: String,
        @Field("user") userId: Int?,
        @Field("message") message: Int?,
        @Field("enable") enable: Boolean?,
        @Field("days_of_the_week") days_of_the_week: String?,
        @Field("time") time: String?
    ): Observable<Response<ReminderSettingEntity>>

    /**
     * 获取通知时间
     * @param authorization String
     * @return Observable<Response<List<ReminderSettingEntity>>>
     */
    @GET("/api/v0.1/userNotification/")
    fun getReminderSetting(
        @Header("Authorization") authorization: String
    ): Observable<Response<List<ReminderSettingEntity>>>

    /**
     * 更新通知时间
     * @param authorization String
     * @param id Int?
     * @param userId Int?
     * @param message Int?
     * @param enable Boolean?
     * @param days_of_the_week String?
     * @param time String?
     * @return Observable<Response<ReminderSettingEntity>>
     */
    @PUT("/api/v0.1/userNotification/{id}/")
    @FormUrlEncoded
    fun updateReminderSetting(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int?,
        @Field("user") userId: Int?,
        @Field("message") message: Int?,
        @Field("enable") enable: Boolean?,
        @Field("days_of_the_week") days_of_the_week: String?,
        @Field("time") time: String?
    ): Observable<Response<ReminderSettingEntity>>

}