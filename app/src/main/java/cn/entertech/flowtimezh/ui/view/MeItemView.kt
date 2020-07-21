package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.uicomponentsdk.utils.ScreenUtil

class MeItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    RelativeLayout(context, attributeSet, def) {
    var self: View = LayoutInflater.from(context).inflate(R.layout.layout_me_item, null)

    private var mText: String?

    private var mLabel: String?

    private var mIsLabelShow: Boolean

    private var mIcon: Int

    init {
        var typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MeItemView)
        mText = typedArray.getString(R.styleable.MeItemView_item_text)
        mLabel = typedArray.getString(R.styleable.MeItemView_item_label)
        mIsLabelShow = typedArray.getBoolean(R.styleable.MeItemView_item_isLabelShow, false)
        mIcon = typedArray.getResourceId(R.styleable.MeItemView_item_icon, 0)
        initView()
        var layoutParams = LayoutParams(MATCH_PARENT, ScreenUtil.dip2px(context, 56f))
        self.layoutParams = layoutParams
        addView(self)
    }

    fun initView() {
        self.findViewById<ImageView>(R.id.iv_icon).setImageResource(mIcon)
        self.findViewById<TextView>(R.id.tv_text).text = mText
        self.findViewById<TextView>(R.id.tv_label).text = mLabel
        if (mIsLabelShow) {
            self.findViewById<TextView>(R.id.tv_label).visibility = View.VISIBLE
        } else {
            self.findViewById<TextView>(R.id.tv_label).visibility = View.GONE
        }
    }

    open fun setOnItemClick(clickListener: OnClickListener) {
        self.findViewById<RelativeLayout>(R.id.bg).setOnClickListener(clickListener)
    }

    open fun setText(text: String) {
        self.findViewById<TextView>(R.id.tv_text).text = text
    }

    open fun setLabel(text: String) {
        self.findViewById<TextView>(R.id.tv_label).text = text
    }
}
