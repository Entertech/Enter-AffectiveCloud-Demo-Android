package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class FullScreenVideoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : VideoView(context, attributeSet, defStyle) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
    }
}