package cn.entertech.flowtimezh.mvp.view
import okhttp3.ResponseBody

interface ReportFilePostView : View {
    fun onSuccess(responseBody: ResponseBody?,meditationId:Long? = null,uri:String? = null)
    fun onError(error: String)
}