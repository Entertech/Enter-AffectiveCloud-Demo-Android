package cn.entertech.flowtimezh.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.entertech.flowtimezh.R;

public class NapProgressView extends RelativeLayout {
    private Context mContext;
    private View self;
    private int mValue = 0;
    private int mMaxValue = 100;
    private int mProgress = 0;
    private int mBackgroundColor;
    private int mForegroundColor;
    private String mLabel = "--";
    private String mUnit = "--";

    public NapProgressView(Context context) {
        this(context, null);
    }

    public NapProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NapProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        self = LayoutInflater.from(context).inflate(R.layout.layout_common_progress, null);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NapProgressView);
        mForegroundColor = a.getColor(R.styleable.NapProgressView_npv_foregroundColor, mForegroundColor);
        mBackgroundColor = a.getColor(R.styleable.NapProgressView_npv_backgroundColor, mBackgroundColor);
        mLabel = a.getString(R.styleable.NapProgressView_npv_label);
        mUnit = a.getString(R.styleable.NapProgressView_npv_unit);
        mValue = a.getInt(R.styleable.NapProgressView_npv_value, mValue);
        mMaxValue = a.getInt(R.styleable.NapProgressView_npv_maxValue, mMaxValue);
        initView(self);
        addView(self);
    }

    private void initView(View view) {
        if (!isSetProgressCustom){
            mProgress = Math.round(mValue * 1.0f / mMaxValue * 100);
        }
        ((TextView) view.findViewById(R.id.tv_label)).setText(mLabel);
        ((TextView) view.findViewById(R.id.tv_value)).setText(getSleepDurationFormat(mValue) + "(" + mProgress + "%)");
        ((ProgressBar) view.findViewById(R.id.pb_sleep)).setVisibility(VISIBLE);
        ((LinearLayout) view.findViewById(R.id.ll_custom_view)).setVisibility(GONE);
        ((ProgressBar) view.findViewById(R.id.pb_sleep)).setMax(mMaxValue);
        ((ProgressBar) view.findViewById(R.id.pb_sleep)).setProgress(mValue);
        setProgressForegroundColor(mForegroundColor);
        setProgressBackgroundColor(mBackgroundColor);
    }
    public int getProgress(){
        return mProgress;
    }
    private boolean isSetProgressCustom;
    public void setProgress(int progress){
        isSetProgressCustom = true;
        mProgress = progress;
        if (self != null) {
            initView(self);
        }
    }


    public String getSleepDurationFormat(int totalMin) {
        int hour = totalMin / 60;
        int min = totalMin % 60;
        String result;
        if (min == 0 && hour == 0) {
            result = "0" + mContext.getString(R.string.minute);
        } else if (min == 0 && hour > 0) {
            result = hour + mContext.getString(R.string.hour);
        } else if (hour == 0 && min > 0) {
            result = min + mContext.getString(R.string.minute);
        } else {
            result = hour + mContext.getString(R.string.hour) + min + mContext.getString(R.string.minute);
        }
        return result;
    }


    public void setValueText(String text) {
        if (self != null) {
            ((TextView) self.findViewById(R.id.tv_value)).setText(text);
        }
    }

    public void addCustomView(View view) {
        ((ProgressBar) self.findViewById(R.id.pb_sleep)).setVisibility(GONE);
        ((LinearLayout) self.findViewById(R.id.ll_custom_view)).setVisibility(VISIBLE);
        ((LinearLayout) self.findViewById(R.id.ll_custom_view)).addView(view);
    }


    public void setLabel(String label) {
        mLabel = label;
        if (self != null) {
            initView(self);
        }
    }

    public void setUnit(String unit) {
        mUnit = unit;
        if (self != null) {
            initView(self);
        }
    }

    /**
     * 设置进度条最大值
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        this.mMaxValue = maxValue;
        if (self != null) {
            initView(self);
        }
    }

    /**
     * 设置进度条当前值
     *
     * @param value
     */
    public void setValue(int value) {
        this.mValue = value;
        if (self != null) {
            initView(self);
        }
    }

    /**
     * 设置进度条前景色
     *
     * @param color
     */
    public void setProgressForegroundColor(int color) {
        setProgressColor(R.id.layer_list_progress_foreground, color);
    }

    /**
     * 设置进度条背景色
     *
     * @param color
     */
    public void setProgressBackgroundColor(int color) {
        setProgressColor(R.id.layer_list_progress_background, color);
    }

    /**
     * 设置进度条颜色
     *
     * @param drawableId
     * @param color
     */
    public void setProgressColor(int drawableId, int color) {
        if (self == null) {
            return;
        }
        LayerDrawable ld = (LayerDrawable) ((ProgressBar) self.findViewById(R.id.pb_sleep)).getProgressDrawable();
        GradientDrawable gradientDrawable;
        if (drawableId == R.id.layer_list_progress_foreground) {
            gradientDrawable = (GradientDrawable) ((ScaleDrawable) ld.findDrawableByLayerId(drawableId)).getDrawable();
        } else {
            gradientDrawable = (GradientDrawable) ld.findDrawableByLayerId(drawableId);
        }
        gradientDrawable.setColor(color);
    }

}
