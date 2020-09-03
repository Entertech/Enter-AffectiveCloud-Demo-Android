package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R

class MeditationInterruptView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {
    private var mErrorMessageListener: ((String,Int) -> Unit)? = null
    var selfView = LayoutInflater.from(context).inflate(R.layout.view_meditation_interrupt, null)
    companion object{
        const val ERROR_TYPE_DEVICE = 0
        const val ERROR_TYPE_NET = 1
        const val ERROR_TYPE_SIGNAL = 2
    }
    var errorType = -1
    init {
        var layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        selfView.layoutParams = layoutParams
        addView(selfView)
    }

    fun toDeviceDisconnect(connectClickListener: (() -> Unit)) {
        errorType = ERROR_TYPE_DEVICE
        selfView.findViewById<ImageView>(R.id.iv_error_icon)
            .setImageResource(R.drawable.vector_drawable_device_bluetooth)
        selfView.findViewById<TextView>(R.id.tv_error_title).text = context.getString(R.string.meditation_error_device_title)
        selfView.findViewById<TextView>(R.id.tv_error_content).text =
            context.getString(R.string.meditation_error_device_tip)
        selfView.findViewById<TextView>(R.id.btn_connect).text = context.getString(R.string.meditation_error_device_handle)
        selfView.findViewById<TextView>(R.id.btn_connect).setOnClickListener {
            connectClickListener.invoke()
        }
        mErrorMessageListener?.invoke(context.getString(R.string.meditation_error_device_title),errorType)
    }

    fun toNetDisconnect(connectClickListener: (() -> Unit)) {
        errorType = ERROR_TYPE_NET
        selfView.findViewById<ImageView>(R.id.iv_error_icon)
            .setImageResource(R.drawable.vector_drawable_net_error)
        selfView.findViewById<TextView>(R.id.tv_error_title).text = context.getString(R.string.meditation_error_net_title)
        selfView.findViewById<TextView>(R.id.tv_error_content).text =
            context.getString(R.string.meditation_error_net_tip)
        selfView.findViewById<TextView>(R.id.btn_connect).text = context.getString(R.string.meditation_error_net_handle)
        selfView.findViewById<TextView>(R.id.btn_connect).setOnClickListener {
            connectClickListener.invoke()
        }
        mErrorMessageListener?.invoke(context.getString(R.string.meditation_error_net_title),errorType)
    }
    
    fun toSignalBad(connectClickListener: (() -> Unit)) {
        errorType = ERROR_TYPE_SIGNAL
        selfView.findViewById<ImageView>(R.id.iv_error_icon)
            .setImageResource(R.drawable.vector_drawable_net_error)
        selfView.findViewById<TextView>(R.id.tv_error_title).text = context.getString(R.string.meditation_error_signal_title)
        selfView.findViewById<TextView>(R.id.tv_error_content).text =
            context.getString(R.string.meditation_error_signal_tip)
        selfView.findViewById<TextView>(R.id.btn_connect).text = context.getString(R.string.activity_sensor_contact_check_sensor_contact_check)
        selfView.findViewById<TextView>(R.id.btn_connect).setOnClickListener {
            connectClickListener.invoke()
        }
        mErrorMessageListener?.invoke(context.getString(R.string.meditation_error_signal_title),errorType)
    }

    fun addErrorMessageListener(listener:((String,Int)->Unit)?){
        this.mErrorMessageListener = listener
    }
}