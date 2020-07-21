package cn.entertech.flowtimezh.ui.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SensorCheckBlinkView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private var mWidth: Int = 0
    private var mPaint: Paint

    var radius: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = Color.parseColor("#807AE1C0")
        mPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = measuredWidth
        radius = mWidth / 2f
        startAnim()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)
        canvas.drawCircle(0f, 0f, radius, mPaint)
    }

    fun startAnim() {
        var anim = ObjectAnimator.ofFloat(this, "radius", mWidth / 4f, mWidth / 2f)
        anim.duration = 1000
        anim.repeatMode = REVERSE
        anim.repeatCount = INFINITE
        anim.start()
    }
}