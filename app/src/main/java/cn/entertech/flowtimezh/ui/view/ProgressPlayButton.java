package cn.entertech.flowtimezh.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import cn.entertech.flowtimezh.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;


/**
 * @author Lloyd
 */
public class ProgressPlayButton extends View implements View.OnClickListener {
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private int mBtnBg = Color.parseColor("#cccccc");
    private int mProgressBarColor = Color.parseColor("#0000ff");
    private float mProgressBarWidth;
    private int mProgressBarBg = Color.parseColor("#4756b0");
    private Context mContext;
    private int screenWidth;
    private int screenHeight;
    private Paint mBgPaint;
    private Paint mRingPaint;
    private Paint mProgressPaintt;
    private boolean isPlay;
    private int mMaxValue;
    private float mProgressValue;
    private float mSweepAngle;
    private IOnButtonStateCallback mOnButtonStateCallback;

    public ProgressPlayButton(Context context) {
        this(context, null);
    }

    public ProgressPlayButton(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressPlayButton(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressPlayButton);
        if (a != null) {
            mProgressBarBg = a.getColor(R.styleable.ProgressPlayButton_progressBarBg, mProgressBarBg);
            mProgressBarWidth = a.getDimension(R.styleable.ProgressPlayButton_progressBarWidth, ScreenUtil.dip2px(context, 4));
            mProgressBarColor = a.getColor(R.styleable.ProgressPlayButton_progressBarColor, mProgressBarColor);
            mBtnBg = a.getColor(R.styleable.ProgressPlayButton_btnBg, mBtnBg);
            mPlayDrawable = a.getDrawable(R.styleable.ProgressPlayButton_playSrc);
            mPauseDrawable = a.getDrawable(R.styleable.ProgressPlayButton_pauseSrc);
            mMaxValue = a.getInt(R.styleable.ProgressPlayButton_max, 100);
            mProgressValue = a.getFloat(R.styleable.ProgressPlayButton_progress, 0f);
            a.recycle();
        }
        initPaint();
        setOnClickListener(this);
    }

    private void initPaint() {
        mBgPaint = new Paint();
        mBgPaint.setColor(mBtnBg);
        mBgPaint.setAntiAlias(true);
        mRingPaint = new Paint();
        mRingPaint.setColor(mProgressBarBg);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mProgressBarWidth);
        mProgressPaintt = new Paint();
        mProgressPaintt.setStyle(Paint.Style.STROKE);
        mProgressPaintt.setAntiAlias(true);
        mProgressPaintt.setColor(mProgressBarColor);
        mProgressPaintt.setStrokeWidth(mProgressBarWidth);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getWidth();
        screenHeight = getHeight();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = ScreenUtil.dip2px(mContext, 200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mMaxValue <= 0) {
            throw new RuntimeException("max value must more than zero");
        }
        mSweepAngle = mProgressValue * 1f / mMaxValue * 360;
        drawProgress(canvas);
        drawBg(canvas);
        drawIcon(canvas);
    }

    public void setProgress(float progress) {
        this.mProgressValue = progress;
        invalidate();
    }

    public void setMax(int max) {
        this.mMaxValue = max;
        invalidate();
    }

    private void drawProgress(Canvas canvas) {
        RectF rectF = new RectF(mProgressBarWidth / 2, (screenHeight - screenWidth) / 2 + mProgressBarWidth / 2,
                screenWidth - mProgressBarWidth / 2, (screenHeight + screenWidth) / 2 - mProgressBarWidth / 2);
        canvas.drawArc(rectF, 0, 360, false, mRingPaint);
        canvas.drawArc(rectF, -90, mSweepAngle, false, mProgressPaintt);
    }

    private void drawBg(Canvas canvas) {
        canvas.drawCircle(screenWidth / 2, screenHeight / 2,
                screenWidth / 2 - mProgressBarWidth, mBgPaint);
    }

    private void drawIcon(Canvas canvas) {
        if (!isPlay) {
            if (mPlayDrawable != null) {
                mPlayDrawable.setBounds(screenWidth / 4, screenHeight / 4,
                        screenWidth * 3 / 4, screenHeight * 3 / 4);
                mPlayDrawable.draw(canvas);
            }
        } else {
            if (mPauseDrawable != null) {
                mPauseDrawable.setBounds(screenWidth / 4, screenHeight / 4,
                        screenWidth * 3 / 4, screenHeight * 3 / 4);
                mPauseDrawable.draw(canvas);
            }
        }
    }

    public void play() {
        isPlay = true;
        invalidate();
    }

    public void pause() {
        isPlay = false;
        invalidate();
    }

    @Override
    public void onClick(View v) {
        isPlay = !isPlay;
        invalidate();
        if (isPlay) {
            mOnButtonStateCallback.onPlay();
        } else {
            mOnButtonStateCallback.onPause();
        }
    }

    public interface IOnButtonStateCallback {
        /**
         * 按钮播放
         */
        void onPlay();
        /**
         * 按钮暂停
         */
        void onPause();
    }

    public void setOnButtonStateCallback(IOnButtonStateCallback callback) {
        this.mOnButtonStateCallback = callback;
    }

}
