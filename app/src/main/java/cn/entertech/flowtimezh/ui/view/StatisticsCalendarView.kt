package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import cn.entertech.flowtimezh.utils.ScreenUtil
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.TimeUtils.getDaysOfCurrentMonth

class StatisticsCalendarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    var weeksTitle = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var rects = ArrayList<Rect>()
    var rectIndexs = ArrayList<Int>()
    private var startOffset = weeksTitle.indexOf(TimeUtils.getDayOfWeek())
    private var totalDay = getDaysOfCurrentMonth()
    private var mCheckedColor = Color.parseColor("#6e7ee2")
    private var mUncheckedColor = Color.parseColor("#f2f4fb")
    init {
        initPaint()
    }

    private lateinit var mTextPaint: Paint
    private lateinit var mRectPaint: Paint

    fun initPaint() {
        mTextPaint = Paint()
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.color = Color.parseColor("#999999")
        mTextPaint.textSize = ScreenUtil.dip2px(context, 12f).toFloat()
        mRectPaint = Paint()
        mRectPaint.color = mUncheckedColor
        mRectPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        onDrawWeekTitle(canvas)
        onDrawCalendar(canvas)
        onDrawActiveDay(canvas)
    }

    private var offset: Float = 0f

    private fun onDrawWeekTitle(canvas: Canvas?) {
        canvas?.save()
        var textAccent = Math.abs(mTextPaint.fontMetrics.ascent)
        canvas?.translate(0f, textAccent)
        offset = width * 1f / 7
        for (i in 0..6) {
            canvas?.drawText(weeksTitle[i], offset / 2 + i * offset, 0f, mTextPaint)
        }
        canvas?.restore()
    }


    private fun onDrawCalendar(canvas: Canvas?) {
        rects.clear()
        mRectPaint.color = mUncheckedColor
        var firstLineRectCount = 7 - startOffset
        var remainDays = totalDay - firstLineRectCount
        var totalLines = if (remainDays % 7 == 0) {
            remainDays / 7 + 1
        } else {
            remainDays / 7 + 2
        }
        var rectMargin = ScreenUtil.dip2px(context, 1f).toFloat()
        canvas?.save()
        var textHeight = Math.abs(mTextPaint.fontMetrics.top) + Math.abs(mTextPaint.fontMetrics.bottom)
        var topPadding = ScreenUtil.dip2px(context, 8f).toFloat()
        canvas?.translate(0f, textHeight + topPadding)
        var rectHeight = (height - textHeight - topPadding - rectMargin * (totalLines - 1)) / totalLines
        var rectWidth = (width - rectMargin * 6) / 7
        for (i in 0 until firstLineRectCount) {
            var left = startOffset * rectWidth + startOffset * rectMargin + i * rectWidth + i * rectMargin
            var rect = Rect(left.toInt(), 0, (left + rectWidth).toInt(), rectHeight.toInt())
            rects.add(rect)
            canvas?.drawRect(rect, mRectPaint)
        }
        var lineCount = 0
        while (remainDays >= 7) {
            for (j in 0 until 7) {
                var left = j * rectWidth + j * rectMargin
                var rect = Rect(
                    left.toInt(),
                    ((rectHeight + rectMargin) * (lineCount + 1)).toInt(),
                    (left + rectWidth).toInt(),
                    ((rectHeight + rectMargin) * (lineCount + 1) + rectHeight).toInt()
                )
                rects.add(rect)
                canvas?.drawRect(rect, mRectPaint)
            }
            remainDays -= 7
            lineCount++
        }
        for (i in 0 until remainDays) {
            var left = i * rectWidth + i * rectMargin
            var rect = Rect(
                left.toInt(), ((rectHeight + rectMargin) * (lineCount + 1)).toInt(),
                (left + rectWidth).toInt(),
                ((rectHeight + rectMargin) * (lineCount + 1) + rectHeight).toInt()
            )
            rects.add(rect)
            canvas?.drawRect(rect, mRectPaint)
        }
    }

    private fun onDrawActiveDay(canvas: Canvas?) {
        mRectPaint.color = mCheckedColor
        for (index in rectIndexs) {
            var rect = rects[index]
            canvas?.drawRect(rect, mRectPaint)
        }
        canvas?.restore()
    }


    fun setActiveDays(daysString: String) {
        rectIndexs.clear()
        if (daysString == null) {
            return
        }
        var days = daysString.split(",")
        if (days.isEmpty() && daysString.contains("-")) {
            var dates = daysString.split("-")
            var indexString = dates[dates.size - 1]
            var indexInt = Integer.parseInt(indexString)
            rectIndexs.add(indexInt - 1)
        } else {
            for (i in 0 until days.size) {
                var dates = days[i].split("-")
                var indexString = dates[dates.size - 1]
                var indexInt = Integer.parseInt(indexString)
                rectIndexs.add(indexInt - 1)
            }
        }
        invalidate()
    }

    fun setCheckedColor(color: Int){
        this.mCheckedColor = color
        invalidate()
    }

    fun setUncheckColor(color:Int){
        this.mUncheckedColor = color
        invalidate()
    }
}