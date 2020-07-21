package cn.entertech.flowtimezh.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

class SensorCheckAnimateLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ViewGroup(context, attrs, defStyleAttr) {
    var offsetX = 0
        set(value) {
            field = value
            requestLayout()
        }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var mChildWidth = 0
        var childLeft = 0
        for (i in 0 until childCount) {
            var child = getChildAt(i)
            var lp = child.layoutParams as MarginLayoutParams
            if (child.visibility != View.GONE) {
                var childWidth = child.measuredWidth
                var childHeight = child.measuredHeight
                mChildWidth = childWidth
                child.layout(
                    childLeft + offsetX + lp.leftMargin,
                    0 + lp.topMargin,
                    childLeft + childWidth + offsetX - lp.rightMargin,
                    childHeight - lp.bottomMargin
                )
                childLeft += mChildWidth
            }

        }
    }

    var leftMargin = 0
    var rightMargin = 0
    var topMargin = 0
    var bottomMargin = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        for (i in 0 until childCount) {
            var childView = getChildAt(i)
            var lp = childView.layoutParams as MarginLayoutParams
            leftMargin += lp.leftMargin
            rightMargin += lp.rightMargin
            topMargin += lp.topMargin
            bottomMargin += lp.bottomMargin
            measureChild(
                getChildAt(i),
                widthMeasureSpec,
                heightMeasureSpec
            )
        }
        setMeasuredDimension(
            widthSize + leftMargin + rightMargin,
            heightSize + topMargin + bottomMargin
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    fun toSecondPage() {
        var animator =
            ObjectAnimator.ofInt(this, "offsetX", 0, -(measuredWidth - leftMargin - rightMargin))
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    fun toFirstPage() {
        var animator = ObjectAnimator.ofInt(this, "offsetX", -(measuredWidth - leftMargin - rightMargin), 0)
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

}
