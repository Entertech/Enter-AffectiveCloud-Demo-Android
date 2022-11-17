package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.uicomponentsdk.utils.dp

class CommonTextItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    RelativeLayout(context, attributeSet, def) {
    var mArrowIcon: Drawable? = null
        set(value) {
            field = value
            initView()
        }
    var mShowNewFlag: Boolean = false
        set(value) {
            field = value
            initView()
        }
    var mSubText: String? = null
        set(value) {
            field = value
            initView()
        }
    var mTextStyle: Int = 0
        set(value) {
            field = value
            initView()
        }
    var mTextColor: Int = Color.parseColor("#080A0E")
        set(value) {
            field = value
            initView()
        }
    var self: View = LayoutInflater.from(context).inflate(R.layout.view_common_text_item, null)

    var mText: String? = "--"
        set(value) {
            field = value
            initView()
        }
    var mIcon: Drawable? = null
        set(value) {
            field = value
            initView()
        }

    init {
        var typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CommonTextItem)
        mIcon = typedArray.getDrawable(R.styleable.CommonTextItem_cti_icon)
        mText = typedArray.getString(R.styleable.CommonTextItem_cti_text)
        mTextColor = typedArray.getColor(R.styleable.CommonTextItem_cti_textColor, mTextColor)
        mTextStyle = typedArray.getInt(R.styleable.CommonTextItem_cti_textStyle, mTextStyle)
        mSubText = typedArray.getString(R.styleable.CommonTextItem_cti_subText)
        mShowNewFlag = typedArray.getBoolean(R.styleable.CommonTextItem_cti_showNewFlag, false)
        mArrowIcon = typedArray.getDrawable(R.styleable.CommonTextItem_cti_arrowIcon)

        initView()
        var layoutParams = ViewGroup.LayoutParams(MATCH_PARENT,44f.dp().toInt())
        self.layoutParams = layoutParams
        addView(self)
    }

    fun initView() {
        if (mIcon != null) {
            self.findViewById<ImageView>(R.id.iv_icon).visibility = View.VISIBLE
            self.findViewById<ImageView>(R.id.iv_icon).setImageDrawable(mIcon)
        } else {
            self.findViewById<ImageView>(R.id.iv_icon).visibility = View.GONE
        }
        self.findViewById<TextView>(R.id.tv_text).text = mText
        self.findViewById<TextView>(R.id.tv_text).setTextColor(mTextColor)
        if (mTextStyle == 0) {
            self.findViewById<TextView>(R.id.tv_text).typeface = Typeface.DEFAULT
        } else {
            self.findViewById<TextView>(R.id.tv_text).typeface = Typeface.DEFAULT_BOLD
        }
        if (mSubText != null) {
            self.findViewById<TextView>(R.id.tv_sub_text).visibility = View.VISIBLE
            self.findViewById<TextView>(R.id.tv_sub_text).text = mSubText
        } else {
            self.findViewById<TextView>(R.id.tv_sub_text).visibility = View.GONE
        }
        if (mArrowIcon != null) {
            self.findViewById<ImageView>(R.id.iv_icon_arrow).visibility = View.VISIBLE
            self.findViewById<ImageView>(R.id.iv_icon_arrow).setImageDrawable(mArrowIcon)
        } else {
            self.findViewById<ImageView>(R.id.iv_icon_arrow).visibility = View.GONE
        }
        if (mShowNewFlag) {
            self.findViewById<TextView>(R.id.tv_new).visibility = View.VISIBLE
        } else {
            self.findViewById<TextView>(R.id.tv_new).visibility = View.GONE
        }
    }
}
