package cn.entertech.flowtimezh.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R

@SuppressLint("Recycle")
class DeviceTypeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : RelativeLayout(context, attributeSet, def) {


    private var deviceName: String? = null
    private var deviceTip: String? = null
    var isSelect:Boolean = false
    set(value) {
        field = value
    }

    @SuppressLint("InflateParams")
    val selfView: View = LayoutInflater.from(context).inflate(R.layout.layout_device_type, null)

    init {
        val typeArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.DeviceTypeView, def, 0
        )
        deviceName = typeArray.getString(R.styleable.DeviceTypeView_dtv_name)
        deviceTip = typeArray.getString(R.styleable.DeviceTypeView_dtv_tip)

        val layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        selfView.layoutParams = layoutParams
        addView(selfView)
    }

    fun initView() {
        selfView.findViewById<TextView>(R.id.tv_device_name).text = deviceName
        selfView.findViewById<TextView>(R.id.tv_device_tip).text = deviceTip
    }


}