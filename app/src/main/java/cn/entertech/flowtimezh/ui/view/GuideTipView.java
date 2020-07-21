package cn.entertech.flowtimezh.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.app.SettingManager;
import cn.entertech.uicomponentsdk.utils.ScreenUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class GuideTipView extends RelativeLayout {
    private String mTipText;
    private Context mContext;
    private TextView tvTipContext;
    private RelativeLayout rvCloseTip;
    private int mType;
    private ViewGroup.LayoutParams layoutParams;
    private View view;

    public GuideTipView(Context context) {
        this(context, null);
    }

    public GuideTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GuideTipView);
        mTipText = a.getString(R.styleable.GuideTipView_gtv_tipText);
        mType = a.getInteger(R.styleable.GuideTipView_gtv_type, 0);
        view = LayoutInflater.from(mContext).inflate(R.layout.view_guide_tip, this, false);
        initView(view);
        if (view.getParent() instanceof LinearLayout) {
            layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            ((LinearLayout.LayoutParams) layoutParams).leftMargin = ScreenUtil.dip2px(context, 16f);
            ((LinearLayout.LayoutParams) layoutParams).rightMargin = ScreenUtil.dip2px(context, 16f);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            ((RelativeLayout.LayoutParams) layoutParams).leftMargin = ScreenUtil.dip2px(context, 16f);
            ((RelativeLayout.LayoutParams) layoutParams).rightMargin = ScreenUtil.dip2px(context, 16f);
        }
        view.setLayoutParams(layoutParams);
        addView(view);
    }

    private void initView(View view) {
        tvTipContext = view.findViewById(R.id.tv_tip_context);
        tvTipContext.setText(mTipText);
        rvCloseTip = view.findViewById(R.id.rv_close_tip);
        rvCloseTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startCloseAnimator();
                switch (mType) {
                    case 0:
                        SettingManager.getInstance().setNextUpTip(false);
                        break;
                    case 1:
                        SettingManager.getInstance().setFlowtimeTip(false);
                        break;
                    case 2:
                        SettingManager.getInstance().setMyCourseTip(false);
                        break;
                    case 3:
                        SettingManager.getInstance().setTopCourseTip(false);
                        break;
                    case 4:
                        SettingManager.getInstance().setJourneyTip(false);
                        break;
                    case 5:
                        SettingManager.getInstance().setUnguideMeditationTip(false);
                        break;
                    case 6:
                        SettingManager.getInstance().setContactTip(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void startCloseAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(GuideTipView.this.getMeasuredHeight(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = GuideTipView.this.getLayoutParams();
                layoutParams.height = (int) (animation.getAnimatedValue());
                GuideTipView.this.setLayoutParams(layoutParams);
                GuideTipView.this.requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                GuideTipView.this.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }
}
