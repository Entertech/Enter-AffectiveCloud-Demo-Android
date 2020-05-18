package cn.entertech.affectiveclouddemo.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.utils.ScreenUtil
import cn.entertech.bleuisdk.utils.getOpacityColor

class RealtimePleasureAndArousalView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {
    private var mArousal: Float = 0f
    private var mPleasure: Float = 0f
    private lateinit var mPointPaint: Paint
    private var pointBitmap: Bitmap? = null
    private lateinit var mTextPaint: Paint
    private lateinit var mLinePaint: Paint
    private lateinit var mBgPaint: Paint
    private var pointX: Float = 100f
    private var pointY: Float = -100f

    init {
        initPaint()
        initPointBitmap()
    }

    fun initPointBitmap() {
        pointBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.pic_realtime_arousal_pleasure_point
        )
    }

    private fun initPaint() {
        mBgPaint = Paint()
        mBgPaint.color = Color.parseColor("#1E2443")
        mLinePaint = Paint()
        mLinePaint.strokeWidth = 2f
        mLinePaint.color = getOpacityColor(Color.parseColor("#9B9B9B"), 0.5f)
        mTextPaint = Paint()
        mTextPaint.textSize = ScreenUtil.dip2px(context, 12f).toFloat()
        mTextPaint.color = getOpacityColor(Color.WHITE, 0.4F)
        mPointPaint = Paint()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(0f, height.toFloat())
        onDrawBg(canvas)
        onDrawBorder(canvas)
        onDrawText(canvas)
        drawPoint(canvas)
    }

    fun onDrawBg(canvas: Canvas?) {
        canvas?.drawColor(Color.parseColor("#1E2443"))
    }

    fun onDrawBorder(canvas: Canvas?) {
        canvas?.drawLine(0f, 0F, width.toFloat(), 0F, mLinePaint)
        canvas?.drawLine(0f, 0F, 0F, -height.toFloat(), mLinePaint)
        mLinePaint.color = Color.parseColor("#CDCDCD")
        mLinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        canvas?.drawLine(0f, -height.toFloat(), width.toFloat(), -height.toFloat(), mLinePaint)
        canvas?.drawLine(width.toFloat(), 0F, width.toFloat(), -height.toFloat(), mLinePaint)
    }

    fun onDrawText(canvas: Canvas?) {
        mTextPaint.textAlign = Paint.Align.LEFT
        canvas?.drawText("郁闷", 10f, -20f, mTextPaint)
        canvas?.drawText("烦躁", 10f, -height + 50f, mTextPaint)
        mTextPaint.textAlign = Paint.Align.RIGHT
        canvas?.drawText("愉悦", width - 10f, -20f, mTextPaint)
        canvas?.drawText("兴奋", width - 10f, -height + 50f, mTextPaint)
    }

    fun drawPoint(canvas: Canvas?) {
        pointX = width.toFloat() / 100f * mPleasure
        pointY = -height.toFloat() / 100f * mArousal
        if (pointX <= 0) {
            pointX = pointBitmap!!.width / 2f
        }
        if (pointY >= 0) {
            pointY = -pointBitmap!!.width / 2f
        }
        if (pointX >= width.toFloat()) {
            pointX = width.toFloat() - pointBitmap!!.width / 2f
        }
        if (pointY <= -height.toFloat()) {
            pointY = -height.toFloat() + pointBitmap!!.width/2f
        }
        canvas?.drawBitmap(
            pointBitmap!!,
            pointX - pointBitmap!!.width / 2f,
            pointY - pointBitmap!!.width / 2f,
            mPointPaint
        )
    }

    fun setArousal(arousal: Double) {
        this.mArousal = arousal.toFloat()
        invalidate()
    }

    fun setPleasure(pleasure: Double) {
        this.mPleasure = pleasure.toFloat()
        invalidate()
    }
}