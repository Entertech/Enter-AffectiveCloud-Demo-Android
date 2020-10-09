package cn.entertech.flowtimezh.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import cn.entertech.flowtimezh.R;


public class ChartCardView extends LinearLayout {
    private Context mContext;
    private int mTitleIconResId;
    private String mTitleText;
    private String mMenuText;
    private int mTitleTextColor;
    private int mMenuTextColor;
    private float mCornerRadius;
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvMenu;
    //    private CustomLineChart clcSleep;
    private CardView cardBg;
    private LinearLayout llChartView;
    private LinearLayout llChartFootView;
    private LinearLayout llChartHeadView;

    public ChartCardView(Context context) {
        this(context, null);
    }

    public ChartCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartCardView);
        mTitleIconResId = a.getResourceId(R.styleable.ChartCardView_cc_titleIcon, -1);
        mTitleText = a.getString(R.styleable.ChartCardView_cc_titleText);
        mMenuText = a.getString(R.styleable.ChartCardView_cc_menuText);
        mTitleTextColor = a.getColor(R.styleable.ChartCardView_cc_titleTextColor,
                ContextCompat.getColor(context, R.color.colorReportAboutTitle));
        mMenuTextColor = a.getColor(R.styleable.ChartCardView_cc_menuTextColor,
                ContextCompat.getColor(context, R.color.colorMusicLocal));
        mCornerRadius = a.getDimension(R.styleable.ChartCardView_cc_cornerRadius, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_common_chart_card, null);
        initView(view);
        addView(view);
    }

    private void initView(View view) {
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvMenu = (TextView) view.findViewById(R.id.tv_menu);
        llChartView = (LinearLayout) view.findViewById(R.id.ll_chart_view);
        llChartFootView = (LinearLayout) view.findViewById(R.id.ll_chart_foot_view);
        llChartHeadView = (LinearLayout) view.findViewById(R.id.ll_chart_head_view);
        cardBg = (CardView) view.findViewById(R.id.cd_bg);
        if (mTitleIconResId != -1) {
            ivIcon.setImageResource(mTitleIconResId);
        }
        if (mTitleText != null) {
            tvTitle.setText(mTitleText);
        }
        tvTitle.setTextColor(mTitleTextColor);
        if (mMenuText != null) {
            tvMenu.setText(mMenuText);
        }
        tvMenu.setTextColor(mMenuTextColor);
        cardBg.setRadius(mCornerRadius);
    }

    public void setMenuVisibility(int visiable){
        tvMenu.setVisibility(visiable);
    }

    public void setMenuClickListener(View.OnClickListener listener){
        tvMenu.setOnClickListener(listener);
    }
    public void setTitleIcon(int resId){
        ivIcon.setImageResource(resId);
    }
    public void setTitleText(String text) {
        mTitleText = text;
        tvTitle.setText(mTitleText);
    }

    public void addChartFootView(View view) {
        llChartFootView.addView(view);
    }

    public void addChartHeadView(View view) {
        llChartHeadView.setVisibility(VISIBLE);
        llChartHeadView.addView(view);
    }

    public void addChartView(View view){
        llChartView.addView(view);
    }

}
