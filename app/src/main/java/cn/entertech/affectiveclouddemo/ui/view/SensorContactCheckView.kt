package cn.entertech.affectiveclouddemo.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.Application
import cn.entertech.affectiveclouddemo.utils.ScreenUtil
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.ble.single.BiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig
import com.airbnb.lottie.LottieAnimationView

class SensorContactCheckView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : LinearLayout(context, attributeSet, def) {
    private var mContactWellListener: (() -> Unit)? = null
    private var mContactBadListener: (() -> Unit)? = null
    private var mBleManager: MultipleBiomoduleBleManager? = null
    var self: View =
        LayoutInflater.from(context).inflate(R.layout.view_sensor_contact_check, null)
    var mMainHander = Handler(Looper.getMainLooper())
    var contactListener = fun(state: Int) {
        mMainHander.post {
            initContactView(state)
        }
    }

    init {
        initView()
        var layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ScreenUtil.dip2px(context, 76f)
        )
        self.layoutParams = layoutParams
        addView(self)

        mBleManager = DeviceUIConfig.getInstance(context).managers[0]
        mBleManager?.addContactListener(contactListener)
    }

    fun initView() {
    }

    var contactWellCount = 0
    var isContactWell = false
    fun initContactView(state: Int) {
        if (state == 0) {
            if (!isContactWell) {
                contactWellCount++
                if (contactWellCount == 5) {
                    isContactWell = true
                    self.findViewById<LinearLayout>(R.id.ll_contact_check).visibility = View.GONE
                    self.findViewById<LinearLayout>(R.id.ll_contact_well).visibility = View.VISIBLE
                    self.findViewById<LottieAnimationView>(R.id.lottie_contact_well).playAnimation()
                    mContactWellListener?.invoke()
                }
            }
        } else {
            isContactWell = false
            contactWellCount = 0
            self.findViewById<LinearLayout>(R.id.ll_contact_check).visibility = View.VISIBLE
            self.findViewById<LinearLayout>(R.id.ll_contact_well).visibility = View.GONE
            mContactBadListener?.invoke()
        }
    }

    private fun getStateBinaryString(state: Int): String {
        var binaryString = Integer.toBinaryString(state)
        if (binaryString.length < 8) {
            var preliminary = ""
            for (i in 1..(8 - binaryString.length)) {
                preliminary += "0"
            }
            binaryString = preliminary + binaryString
        }
        return binaryString
    }

    private fun setViewColor(view: View, color: Int) {
        val gradientDrawable = view.background as GradientDrawable
        gradientDrawable.setColor(color)
        view.background = gradientDrawable
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

    fun addContactListener(contactWellListener: (() -> Unit)?, contactBadListener: (() -> Unit)?) {
        this.mContactWellListener = contactWellListener
        this.mContactBadListener = contactBadListener
    }

    fun release() {
        mBleManager?.removeContactListener(contactListener)
        this.mContactWellListener = null
        this.mContactBadListener = null
    }
}