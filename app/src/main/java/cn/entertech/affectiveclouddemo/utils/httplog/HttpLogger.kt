package cn.entertech.affectiveclouddemo.utils.httplog;

import com.orhanobut.logger.Logger
import okhttp3.logging.HttpLoggingInterceptor


class HttpLogger : HttpLoggingInterceptor.Logger {
    private val mMessage = StringBuilder()

    override fun log(message: String) {
        var msg = message
        // 请求或者响应开始
        if (msg.startsWith("--> POST") || message.startsWith("--> GET")) {
            mMessage.setLength(0)
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if (msg.startsWith("{") && message.endsWith("}") || message.startsWith("[") && message.endsWith("]")) {
            msg = JsonUtil.formatJson(JsonUtil.decodeUnicode(message))
        }
        mMessage.append(msg + "\n")
        // 响应结束，打印整条日志
        if (msg.startsWith("<-- END HTTP")) {
            Logger.d(mMessage.toString())
        }
    }
}