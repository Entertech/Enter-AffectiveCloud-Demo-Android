package cn.entertech.affectiveclouddemo.mvp

import android.content.Context
import cn.entertech.affectiveclouddemo.app.Constant.Companion.BASE_URL
import cn.entertech.affectiveclouddemo.mvp.LogService
import cn.entertech.affectiveclouddemo.utils.httplog.HttpLogger
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitHelper(context: Context) {
    var mContext: Context = context
    var mRetrofit: Retrofit? = null
    var mGsonConverterFactory: GsonConverterFactory? = null
    var mOkHttpClient: OkHttpClient? = null

    init {
        initRetrofit()
    }

    companion object {
        @Volatile
        var mRetrofitHelper: RetrofitHelper? = null

        fun getInstance(context: Context): RetrofitHelper {
            if (mRetrofitHelper == null) {
                synchronized(RetrofitHelper::class.java) {
                    if (mRetrofitHelper == null) {
                        mRetrofitHelper = RetrofitHelper(context)
                    }
                }
            }
            return mRetrofitHelper!!
        }
    }

    fun initRetrofit() {
        mGsonConverterFactory = GsonConverterFactory.create(GsonBuilder().create())
        var httpLoggingInterceptor = HttpLoggingInterceptor(HttpLogger())
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        mOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mOkHttpClient!!)
            .addConverterFactory(mGsonConverterFactory!!)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getServer(): RetrofitService {
        return mRetrofit!!.create(RetrofitService::class.java)
    }

    fun getLogServer(): LogService {
        var mRetrofit = Retrofit.Builder()
            .baseUrl("https://log.entertech.cn")
            .client(mOkHttpClient!!)
            .addConverterFactory(mGsonConverterFactory!!)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return mRetrofit.create(LogService::class.java)
    }
}