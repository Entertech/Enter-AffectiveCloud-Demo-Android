package cn.entertech.flowtimezh.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import cn.entertech.flowtimezh.R;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

public class WaveLoadingView extends View {
    private Context mContext;
    private float mWaveLength;
    private float mWaveHeight;
    private int mWaveColor = Color.parseColor("#80ffffff");
    private int mBgColor = Color.TRANSPARENT;
    private float mStrokeWidth;
    private int mStrokeColor = Color.parseColor("#4756b0");
    private Paint mWavePaint;
    private int screenWidth;
    private int screenHeight;
    private int mOffset;
    private Paint mBgPaint;
    private Paint mRingPaint;
    private float progressLine;
    private int mWaveCount;
    private int mCenterY;
    private float mProgress;
    private ValueAnimator valueAnimator;

    public WaveLoadingView(Context context) {
        this(context, null);
    }

    public WaveLoadingView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveLoadingView);
        if (a != null) {
            mStrokeColor = a.getColor(R.styleable.WaveLoadingView_strokeColor, mStrokeColor);
            mStrokeWidth = a.getDimension(R.styleable.WaveLoadingView_strokeWidth, ScreenUtil.dip2px(context, 4));
            mBgColor = a.getColor(R.styleable.WaveLoadingView_BgColor, mBgColor);
            mWaveColor = a.getColor(R.styleable.WaveLoadingView_waveColor, mWaveColor);
            mWaveHeight = a.getDimension(R.styleable.WaveLoadingView_waveHeight, ScreenUtil.dip2px(context, 30));
            mWaveLength = a.getDimension(R.styleable.WaveLoadingView_waveLength, ScreenUtil.dip2px(context, 200));
            mWaveColor = a.getColor(R.styleable.WaveLoadingView_waveColor, mWaveColor);
            a.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setAntiAlias(true);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAntiAlias(true);
        mRingPaint = new Paint();
        mRingPaint.setColor(mStrokeColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getWidth();
        screenHeight = getHeight();
        mWaveCount = (int) Math.round(screenWidth / mWaveLength + 1.5);
        mCenterY = screenHeight / 2;
        startAnim();
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


    public void setProgress(float progressPercent) {
        this.mProgress = progressPercent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        progressLine = screenHeight - (screenWidth - mStrokeWidth * 2) * mProgress - (mCenterY - screenWidth / 2);
//        draw stroke
        drawStroke(canvas);
//        draw bg
        drawBg(canvas);
//        draw wave
        drawWave(canvas);
    }

    private void drawStroke(Canvas canvas) {
        canvas.drawCircle(screenWidth / 2, screenHeight / 2, screenWidth / 2 - mStrokeWidth, mRingPaint);
    }

    private void drawBg(Canvas canvas) {
        if (mBgColor != 0) {
            canvas.drawCircle(screenWidth / 2, screenHeight / 2, screenWidth / 2 - mStrokeWidth * 3f / 2, mBgPaint);
        }
    }

    private void drawWave(Canvas canvas) {
        canvas.saveLayer(0, 0, screenWidth, screenHeight, mWavePaint, Canvas.ALL_SAVE_FLAG);
        Bitmap circleBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas circleCanvas = new Canvas(circleBitmap);
        circleCanvas.drawCircle(screenWidth / 2, screenHeight / 2, screenWidth / 2 - mStrokeWidth * 3f / 2, mWavePaint);
        canvas.drawBitmap(circleBitmap, 0, 0, mWavePaint);
        Bitmap waveBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas waveCanvas = new Canvas(waveBitmap);
        Path path = new Path();
        path.moveTo(-mWaveLength + mOffset, progressLine);
        for (int i = 0; i < mWaveCount; i++) {
            path.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset,
                    progressLine + mWaveHeight, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, progressLine);
            path.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset,
                    progressLine - mWaveHeight, i * mWaveLength + mOffset, progressLine);
        }
        path.lineTo(screenWidth, screenHeight);
        path.lineTo(0, screenHeight);
        path.close();
        waveCanvas.drawPath(path, mWavePaint);
        mWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(waveBitmap, 0, 0, mWavePaint);
        mWavePaint.setXfermode(null);
        canvas.restore();
    }

    public void startAnim() {
        valueAnimator = ValueAnimator.ofInt(0, (int) mWaveLength);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (valueAnimator != null){
            valueAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }
}
