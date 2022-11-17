package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.uicomponentsdk.utils.dp

class MaxHeightRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context,attributeSet,defStyleAttr) {
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val height = MeasureSpec.makeMeasureSpec(176f.dp().toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, height)
    }
}