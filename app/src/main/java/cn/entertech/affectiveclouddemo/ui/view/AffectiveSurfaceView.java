package cn.entertech.affectiveclouddemo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import cn.entertech.uicomponentsdk.utils.ScreenUtil;

public class AffectiveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Context mContext;
    private float mLineWidth;
    private float mRightPadding;
    private float mLeftPadding;
    private int mGridLineCount = 4;
    private int mGridLineColor = Color.parseColor("#E9EBF1");
    private int mYAxisColor = Color.parseColor("#383838");
    private int mLineColor = Color.parseColor("#ff6682");
    private int mBgColor = Color.parseColor("#ffffff");
    private Paint mCruvePaint;
    private int attentionColor = Color.parseColor("#5E75FE");
    private int relaxationColor = Color.parseColor("#52A27C");
    private int pressureColor = Color.parseColor("#FF6682");
    private int pleasureColor = Color.parseColor("#6648FF");
    private int arousalColor = Color.parseColor("#FFC56F");
    private List<Float> mAttentionValues = new ArrayList<>();
    private List<Float> mRelaxationValues = new ArrayList<>();
    private List<Float> mPressureValues = new ArrayList<>();
    private List<Float> mArousalValues = new ArrayList<>();
    private List<Float> mPleasureValues = new ArrayList<>();
    List<Float> drawData = new ArrayList<>();
    List<Float> sampleData = new ArrayList<>();
    private boolean isViewActivity;
    private SurfaceHolder mSurfaceHolder;
    public static int BRAIN_QUEUE_LENGTH = 200;
    public static int BRAIN_BUFFER_LENGTH = 100;
    private Paint mStartLinePaint;
    private Paint mGridLinePaint;
    private Paint mBgPaint;
    private boolean isShowSampleData = false;
    private int mMaxValue = 100;
    private Paint mYAxisLabelPaint;
    private int mYAxisMargin;

    public AffectiveSurfaceView(Context context) {
        this(context, null);
    }

    public AffectiveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AffectiveSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView);
        if (typedArray != null) {
            mLineColor = typedArray.getColor(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_lineColor, mLineColor);
            mBgColor = typedArray.getColor(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_bgColor, mBgColor);
            mYAxisColor = typedArray.getColor(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_yAxisColor, mYAxisColor);
            mGridLineColor = typedArray.getColor(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_gridLineColor, mGridLineColor);
            mGridLineCount = typedArray.getInteger(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_gridLineCount, mGridLineCount);
            mLeftPadding = typedArray.getDimension(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_leftPadding, ScreenUtil.dip2px(context, 5));
            mRightPadding = typedArray.getDimension(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_rightPadding, ScreenUtil.dip2px(context, 5));
            mLineWidth = typedArray.getDimension(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_lineWidth, 3);
            mMaxValue = typedArray.getInteger(cn.entertech.uicomponentsdk.R.styleable.BreathCoherenceSurfaceView_hrvsf_maxValue, mMaxValue);
        }
        initPaint();
    }

    private void initPaint() {

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mCruvePaint = new Paint();
        mCruvePaint.setDither(true);
        mCruvePaint.setStyle(Paint.Style.STROKE);
        mCruvePaint.setAntiAlias(true);
        mCruvePaint.setStrokeWidth(mLineWidth);
        CornerPathEffect pathEffect = new CornerPathEffect(25);
        mCruvePaint.setPathEffect(pathEffect);
        mCruvePaint.setColor(mLineColor);
        mStartLinePaint = new Paint();
        mStartLinePaint.setStyle(Paint.Style.STROKE);
        mStartLinePaint.setColor(mYAxisColor);
        mStartLinePaint.setStrokeWidth(1f);

        mGridLinePaint = new Paint();
        mGridLinePaint.setStyle(Paint.Style.STROKE);
        mGridLinePaint.setColor(mGridLineColor);
        mGridLinePaint.setStrokeWidth(3);
        initData();
        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);

        mYAxisLabelPaint = new Paint();
        mYAxisLabelPaint.setColor(Color.parseColor("#9AA1A9"));
        mYAxisLabelPaint.setTextSize(ScreenUtil.dip2px(mContext, 12));
        mYAxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
//        this.setZOrderOnTop(true);
////        this.setZOrderMediaOverlay(true);
//        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }


    private void appendData(List<Float> values, float data) {
        values.add(data);
        if (values.size() > BRAIN_BUFFER_LENGTH) {
            for (int i = 0; i < values.size() - BRAIN_BUFFER_LENGTH; i++) {
                values.remove(0);
            }
        }
    }


    public synchronized void setData(float attention, float relaxation, float pressure, float pleasure, float arousal) {
        appendData(mAttentionValues, attention);
        appendData(mRelaxationValues, relaxation);
        appendData(mPressureValues, pressure);
        appendData(mPleasureValues, pleasure);
        appendData(mArousalValues, arousal);
    }


    private void initData() {
        for (int i = 0; i < BRAIN_BUFFER_LENGTH; i++) {
            mAttentionValues.add(0f);
            mArousalValues.add(0f);
            mRelaxationValues.add(0f);
            mPleasureValues.add(0f);
            mPressureValues.add(0f);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isViewActivity = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isViewActivity = false;
    }

    @Override
    public void run() {
        while (isViewActivity) {
            draw();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void onDrawBg(Canvas canvas) {
        canvas.drawColor(mBgColor);
        float xGridOffset = getWidth()/16f;
        float yGridOffset = getHeight()/4f;
        for (int i = 0; i < 17; i++) {
            canvas.drawLine(0F+i*xGridOffset,0F,0F+i*xGridOffset,getHeight(),mGridLinePaint);
        }
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0f,0F+i*yGridOffset,getWidth(),0F+i*yGridOffset,mGridLinePaint);
        }
    }

    public void onDrawLines(Canvas canvas) {
        canvas.translate(mLeftPadding + mYAxisMargin, getHeight());
        onDrawLine(canvas, mAttentionValues, attentionColor);
        onDrawLine(canvas, mRelaxationValues, relaxationColor);
        onDrawLine(canvas, mPressureValues, pressureColor);
        onDrawLine(canvas, mPleasureValues, pleasureColor);
        onDrawLine(canvas, mArousalValues, arousalColor);
    }

    public void onDrawLine(Canvas canvas, List<Float> values, int color) {
        mCruvePaint.setColor(color);
        float pointOffset = getWidth() * 1f / (values.size() - 1);
        float time = (getHeight() / mMaxValue);
        Path path = new Path();
        for (int i = 0; i < values.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(values.get(i) * time)));
            path.lineTo(i * pointOffset, (float) (-(values.get(i) * time)));

        }
        canvas.drawPath(path, mCruvePaint);
    }

    public void onDrawSampleData(Canvas canvas) {
        float pointOffset = getWidth() * 1f / (sampleData.size() - 1);
        //获得canvas对象
        canvas.translate(mLeftPadding + mYAxisMargin, getHeight());
        float time = (getHeight() / mMaxValue);
        Path path = new Path();
        for (int i = 0; i < sampleData.size(); i++) {
            if (i == 0)
                path.moveTo(i * pointOffset, (float) (-(sampleData.get(i) * time)));
            path.lineTo(i * pointOffset, (float) (-(sampleData.get(i) * time)));

        }
        canvas.drawPath(path, mCruvePaint);
    }

    private synchronized void draw() {
        Canvas mCanvas = null;
        try {
            mCanvas = mSurfaceHolder.lockCanvas(null);
//            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            onDrawBg(mCanvas);
            if (isShowSampleData) {
                onDrawSampleData(mCanvas);
            } else {
                onDrawLines(mCanvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    public void setSampleData(List<Float> sampleData) {
        this.sampleData = sampleData;
        this.isShowSampleData = true;
        invalidate();
    }

    public void setLineColor(int color) {
        this.mLineColor = color;
        mCruvePaint.setColor(mLineColor);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        this.mBgColor = color;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }

    public void hideSampleData() {
        this.isShowSampleData = false;
    }
}
