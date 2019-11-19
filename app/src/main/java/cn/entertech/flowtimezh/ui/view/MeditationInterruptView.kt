package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R

class MeditationInterruptView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {
    var selfView = LayoutInflater.from(context).inflate(R.layout.card_meditation_interrupt, null)

    init {
        addView(selfView)
    }

    fun toDeviceDisconnect(connectClickListener: (() -> Unit)) {
        selfView.findViewById<ImageView>(R.id.iv_error_icon)
            .setImageResource(R.drawable.vector_drawable_device_bluetooth)
        selfView.findViewById<TextView>(R.id.tv_error_title).text = "Device Not Connect"
        selfView.findViewById<TextView>(R.id.tv_error_content).text =
            "Connect device to show real-time biodata during meditation."
        selfView.findViewById<TextView>(R.id.btn_connect).setOnClickListener {
            connectClickListener.invoke()
        }
    }

    fun toNetDisconnect(connectClickListener: (() -> Unit)) {
        selfView.findViewById<ImageView>(R.id.iv_error_icon)
            .setImageResource(R.drawable.vector_drawable_net_error)
        selfView.findViewById<TextView>(R.id.tv_error_title).text = "Network Error"
        selfView.findViewById<TextView>(R.id.tv_error_content).text =
            "The connection has timed out and the analysis  has suspended. Please check the connection and try to restore analysis."
        selfView.findViewById<TextView>(R.id.btn_connect).text = "Restore"
        selfView.findViewById<TextView>(R.id.btn_connect).setOnClickListener {
            connectClickListener.invoke()
        }
    }
}