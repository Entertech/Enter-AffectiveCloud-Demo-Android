package cn.entertech.affectiveclouddemo.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.entertech.affectiveclouddemo.R

class ReportAboutView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    LinearLayout(context, attributeSet, def) {
    var mLearnMoreUrl: String? = null
    private var mContentText: String? = null
    private var mTitleIcon: Drawable? = null
    var mSelfView: View = LayoutInflater.from(context).inflate(R.layout.layout_report_about, null)

    init {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.ReportAboutView)
        mTitleIcon = typeArray.getDrawable(R.styleable.ReportAboutView_rav_titleIcon)
        mContentText = typeArray.getString(R.styleable.ReportAboutView_rav_contentText)
        mLearnMoreUrl = typeArray.getString(R.styleable.ReportAboutView_rav_learnMoreUrl)
        var layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mSelfView.layoutParams = layoutParams
        addView(mSelfView)
        initView()
    }

    private fun initView() {
        mSelfView.findViewById<ImageView>(R.id.iv_icon).setImageDrawable(mTitleIcon)
        mSelfView.findViewById<TextView>(R.id.tv_content).text = mContentText
        mSelfView.findViewById<TextView>(R.id.tv_learn_more).setOnClickListener {
            if (mLearnMoreUrl != null) {
                var uri = Uri.parse(mLearnMoreUrl)
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }

}