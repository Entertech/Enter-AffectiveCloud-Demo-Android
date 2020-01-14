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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.affectiveclouddemo.R
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.bleuisdk.ui.DeviceUIConfig

class MeditationWearStateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {
    private var electrodeView5: TextView? = null
    private var mIsClose: Boolean = false
    private var mBleManager: MultipleBiomoduleBleManager
    var selfView = LayoutInflater.from(context).inflate(R.layout.view_meditation_ble_contact, null)
    var electrodeViewList = ArrayList<TextView>()

    var COLOR_OF_RIGHT = Color.parseColor("#7AFFC0")
    var COLOR_OF_ERROR = Color.parseColor("#FF6682")
    var mMainHander = Handler(Looper.getMainLooper())
    var contactListener = fun(state: Int) {
        mMainHander.post {
            setElectrodeState(state)
        }
    }

    init {
        var layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        selfView.layoutParams = layoutParams
        addView(selfView)
        initElectrodeViewList()
        mBleManager = DeviceUIConfig.getInstance(context).managers[0]
        mBleManager.addContactListener(contactListener)
    }

    fun initElectrodeViewList() {
        var electrodeView1 = selfView.findViewById<TextView>(R.id.tv_electrode_1)
        var electrodeView2 = selfView.findViewById<TextView>(R.id.tv_electrode_2)
        var electrodeView3 = selfView.findViewById<TextView>(R.id.tv_electrode_3)
        var electrodeView4 = selfView.findViewById<TextView>(R.id.tv_electrode_4)
        electrodeView5 = selfView.findViewById<TextView>(R.id.tv_electrode_5)
        electrodeViewList.add(electrodeView3)
        electrodeViewList.add(electrodeView4)
        electrodeViewList.add(electrodeView2)
        electrodeViewList.add(electrodeView1)
    }

    fun toExpand() {
        selfView.findViewById<RelativeLayout>(R.id.rl_title_bg).setBackgroundColor(Color.parseColor("#343854"))
        selfView.findViewById<ImageView>(R.id.iv_expand_icon).visibility = View.VISIBLE
        selfView.findViewById<ImageView>(R.id.iv_expand_icon).setOnClickListener {
            toClose()
        }
        selfView.findViewById<ImageView>(R.id.iv_expand_icon)
            .setImageResource(R.drawable.vector_drawable_arrow_white_up)
        selfView.findViewById<LinearLayout>(R.id.ll_wear_state_tip).visibility = View.VISIBLE
        selfView.findViewById<TextView>(R.id.tv_title).visibility = View.VISIBLE
        selfView.findViewById<TextView>(R.id.tv_normal_tip).visibility = View.GONE
    }

    fun toClose() {
        selfView.findViewById<ImageView>(R.id.iv_expand_icon).visibility = View.VISIBLE
        selfView.findViewById<RelativeLayout>(R.id.rl_title_bg).setBackgroundColor(Color.WHITE)
        selfView.findViewById<ImageView>(R.id.iv_expand_icon).setOnClickListener {
            toExpand()
        }
        selfView.findViewById<ImageView>(R.id.iv_expand_icon)
            .setImageResource(R.drawable.vector_drawable_arrow_blue_down)
        selfView.findViewById<LinearLayout>(R.id.ll_wear_state_tip).visibility = View.GONE
        selfView.findViewById<TextView>(R.id.tv_title).visibility = View.GONE
        selfView.findViewById<TextView>(R.id.tv_normal_tip).visibility = View.VISIBLE
        mIsClose = true
    }

    fun toStateError() {
        toExpand()
        selfView.findViewById<ImageView>(R.id.iv_expand_icon).visibility = View.GONE
        mIsClose = false
    }

    private fun setElectrodeState(state: Int) {
        when (state) {
            0 -> {
                for (view in electrodeViewList) {
                    setViewColor(view, COLOR_OF_RIGHT)
                }
                setViewColor(electrodeView5!!, COLOR_OF_RIGHT)
                if (!mIsClose) {
                    toClose()
                }
            }
            else -> {
                toStateError()
                var stateBinaryString = getStateBinaryString(state).subSequence(1, 5)
                for (i in stateBinaryString.indices) {
                    if (stateBinaryString[i] == '1') {
                        setViewColor(electrodeViewList[i], COLOR_OF_ERROR)
                    } else {
                        setViewColor(electrodeViewList[i], COLOR_OF_RIGHT)
                    }
                }
                if (state == 120) {
                    setViewColor(electrodeView5!!, COLOR_OF_ERROR)
                }
            }
        }
    }

    private fun setViewColor(view: View, color: Int) {
        val gradientDrawable = view.background as GradientDrawable
        gradientDrawable.setColor(color)
        view.background = gradientDrawable
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

    override fun onDetachedFromWindow() {
        mBleManager.removeContactListener(contactListener)
        super.onDetachedFromWindow()
    }

}