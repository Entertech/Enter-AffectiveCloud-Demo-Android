package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.entertech.flowtimezh.R

class DeviceContactTipView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : LinearLayout(context, attributeSet, def) {
    private var mDrawable: Drawable?
    private var mTip: String?
    private var mTitle: String? = ""
    var self: View = LayoutInflater.from(context).inflate(R.layout.view_device_contact_tip, null)

    init {
        var typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DeviceContactTipView)
        mTitle = typedArray.getString(R.styleable.DeviceContactTipView_dctv_title)
        mTip = typedArray.getString(R.styleable.DeviceContactTipView_dctv_tip)
        mDrawable = typedArray.getDrawable(R.styleable.DeviceContactTipView_dctv_image)
        var layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        self.layoutParams = layoutParams
        addView(self)
        initView()
    }

    fun initView() {
        initTitle()
        initImageView()
        self.findViewById<TextView>(R.id.tv_text).text = mTip
    }

    fun initImageView() {
        var imageView = self.findViewById<ImageView>(R.id.iv_image)
        imageView.viewTreeObserver.addOnGlobalLayoutListener {
            var width = imageView.measuredWidth
            var height = 160 / 311f * width
            var layoutParams = imageView.layoutParams
            layoutParams.height = height.toInt()
            imageView.layoutParams = layoutParams
            imageView.requestLayout()
            imageView.setBackgroundDrawable(mDrawable)
        }
    }

    fun initTitle() {
        self.findViewById<TextView>(R.id.tv_title).text = mTitle
        self.findViewById<TextView>(R.id.tv_title).setTextColor(
            ContextCompat.getColor(context,R.color.common_text_lv1_base_color_light)
        )
        self.findViewById<ImageView>(R.id.iv_icon).visibility = View.VISIBLE
        self.findViewById<ImageView>(R.id.iv_menu).visibility = View.GONE
        self.findViewById<ImageView>(R.id.iv_icon)
            .setImageResource(R.mipmap.ic_device_contact_title_icon)
    }

}