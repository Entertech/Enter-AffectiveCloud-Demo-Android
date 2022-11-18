package cn.entertech.flowtimezh.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.utils.CustomLineChart;
import cn.entertech.flowtimezh.utils.CustomXAxisRendererHorizontalLineChart;
import cn.entertech.flowtimezh.utils.CustomYAxisRendererHorizontalLineChart;
import cn.entertech.flowtimezh.utils.ScreenUtil;
import cn.entertech.flowtimezh.utils.reportfileutils.BrainDataUnit;
import cn.entertech.flowtimezh.utils.reportfileutils.FileProtocol;
import cn.entertech.flowtimezh.utils.reportfileutils.MeditationReportDataAnalyzed;

public class SleepChartLayout extends LinearLayout {
    private Context mContext;
    private ChartCardView ccvSleepCard;
    private FileProtocol<? extends BrainDataUnit> mSleepFileData;
    private short yValueSober;
    private short yValueBlur;
    private short yValueSleep;
    private float sleepPoint;
    private short alarmPoint;
    private List<Double> valueList;
    private TreeMap<Integer, String> needShowPoint;
    private CustomLineChart customLineChart;
    private LinearLayout llSleepTag;
    private LinearLayout llAwakeTag;
    private RelativeLayout.LayoutParams awakelayoutParams;
    private LinearLayout llChartView;
    private RelativeLayout rlChartLabel;
    private TextView tvSleepTime;
    private TextView tvAwakeTime;
    private RelativeLayout.LayoutParams sleeplayoutParams;
    private NapProgressView npvProgressLineOne;
    private NapProgressView npvProgressLineTwo;
    private NapProgressView npvProgressLineThree;
    private short wearQuality;
    private short clockPoint;
    private List<Integer> valueListSleep;
    private int wakePoint = 0;
    private MeditationReportDataAnalyzed meditationReportDataAnalyzed;
    private FileProtocol fileProtocol;

    public SleepChartLayout(Context context) {
        this(context, null);
    }

    public SleepChartLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepChartLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_chart, null);
        ccvSleepCard = (ChartCardView) view.findViewById(R.id.ccv_sleep_card);
        ccvSleepCard.setTitleText(getContext().getString(R.string.sleep_chart_title));
        ccvSleepCard.setMenuClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                postButtonEvent(mContext,"0203","统计页面 了解更多");
//                Intent intent;
//                if (Constants.isSleepRecord) {
//                    intent = new Intent(mContext, WebActivity.class).putExtra(WEB_TITLE, mContext.getString(R.string.report_sleep_state));
//                    intent.putExtra(ExtraKey.EXTRA_URL, mContext.getString(R.string.url_sleep_know_more));
//                } else {
//                    intent = new Intent(mContext, WebActivity.class).putExtra(WEB_TITLE, mContext.getString(R.string.report_nap_state));
//                    intent.putExtra(ExtraKey.EXTRA_URL, mContext.getString(R.string.url_nap_know_more));
//                }
//                mContext.startActivity(intent);
            }
        });
        addView(view);
    }

//    public void setChartAlarmTag() {
//        if (mRecord.getClockMode() == 2) {
//            wakePoint = clockPoint;
//            llAwakeTag.setBackground(ContextCompat.getDrawable(mContext
//                    , R.drawable.shape_sleep_chart_clock_label));
//        } else if (mRecord.getClockMode() == 1) {
//            wakePoint = alarmPoint;
//            llAwakeTag.setBackground(ContextCompat.getDrawable(mContext
//                    , R.drawable.shape_sleep_chart_alarm_label));
//        }
//    }

    public void initChartTag() {
        if (fileProtocol != null) {
            if (wakePoint == 0) {
                llAwakeTag.setVisibility(INVISIBLE);
            } else {
                llAwakeTag.setVisibility(VISIBLE);
                awakelayoutParams = (RelativeLayout.LayoutParams) llAwakeTag.getLayoutParams();
                awakelayoutParams.leftMargin = (int) (wakePoint * 1.0 /
                        valueList.size() *
                        (customLineChart.getMeasuredWidth() - Utils.convertDpToPixel(35)) + Utils.convertDpToPixel(8));
                if (awakelayoutParams.leftMargin >= rlChartLabel.getWidth() - llAwakeTag.getWidth()) {
                    awakelayoutParams.leftMargin = (rlChartLabel.getWidth() - llAwakeTag.getWidth());
                }
                llAwakeTag.setLayoutParams(awakelayoutParams);
                tvAwakeTime.setText("(" + getTimeByIndex(mSleepFileData, wakePoint).split(" ")[0] + ")");
            }

            if (sleepPoint == 0) {
                llSleepTag.setVisibility(INVISIBLE);
            } else {
                llSleepTag.setVisibility(VISIBLE);
                sleeplayoutParams
                        = (RelativeLayout.LayoutParams) llSleepTag.getLayoutParams();
                sleeplayoutParams.leftMargin = (int) (sleepPoint * 1.0 /
                        valueList.size() *
                        (customLineChart.getMeasuredWidth() - Utils.convertDpToPixel(35) + Utils.convertDpToPixel(8)));
                if (wakePoint != 0) {
                    if (sleeplayoutParams.leftMargin + llSleepTag.getWidth() <
                            awakelayoutParams.leftMargin + llAwakeTag.getWidth() &&
                            sleeplayoutParams.leftMargin + llSleepTag.getWidth() > awakelayoutParams.leftMargin) {
                        sleeplayoutParams.leftMargin = awakelayoutParams.leftMargin - llAwakeTag.getWidth();
                    }
                }
                if (sleeplayoutParams.leftMargin >= rlChartLabel.getWidth() - llSleepTag.getWidth()) {
                    sleeplayoutParams.leftMargin = rlChartLabel.getWidth() - llSleepTag.getWidth();
                }
                llSleepTag.setLayoutParams(sleeplayoutParams);
                tvSleepTime.setText("(" + getTimeByIndex(fileProtocol, (int) sleepPoint).split(" ")[0] + ")");
            }
        }
    }

    public void setSourceData(FileProtocol fileProtocol) {
        if (fileProtocol == null){
            return;
        }
        this.fileProtocol = fileProtocol;
        if (fileProtocol != null && fileProtocol.getList().size() > 0) {
            this.meditationReportDataAnalyzed = (MeditationReportDataAnalyzed) fileProtocol.getList().get(0);
        }
        valueList = meditationReportDataAnalyzed.getSleepCurve();
        sleepPoint = meditationReportDataAnalyzed.getSleepPoint();
        if (valueList != null) {
            this.needShowPoint = getNeedShowPoint(fileProtocol);
            View chartHeadView = getChartHeadView();
//            setChartAlarmTag();
            customLineChart = new CustomLineChart(mContext);
            customLineChart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    initChartTag();
                }
            });
            ccvSleepCard.addChartHeadView(chartHeadView);
            ccvSleepCard.addChartView(customLineChart);
            ccvSleepCard.addChartFootView(getChartFootView());
            LayoutParams layoutParams = (LayoutParams) customLineChart.getLayoutParams();
            layoutParams.height = (int) ScreenUtil.dip2px(mContext, 200);
            customLineChart.setLayoutParams(layoutParams);
            initChartView(customLineChart);
        }
    }

    public List<Double> converIntegerToDouble(List<Integer> list) {
        List<Double> newList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            newList.add((double) list.get(i));
        }
        return newList;
    }

    private void initSleepProgressView(View napProgressView) {
        int soberTime = (int) (meditationReportDataAnalyzed.getSoberDuration() / 60);
        int blurTime = (int) (meditationReportDataAnalyzed.getLightDuration() / 60);
        int sleepTime = (int) (meditationReportDataAnalyzed.getDeepDuration() / 60);
        int totalTime = soberTime + blurTime + sleepTime;
        npvProgressLineOne = (NapProgressView) napProgressView.findViewById(R.id.npv_progress_line_one);
        npvProgressLineTwo = (NapProgressView) napProgressView.findViewById(R.id.npv_progress_line_two);
        npvProgressLineThree = (NapProgressView) napProgressView.findViewById(R.id.npv_progress_line_three);
        npvProgressLineOne.setValue(soberTime);
        npvProgressLineOne.setMaxValue(totalTime);
        npvProgressLineTwo.setValue(blurTime);
        npvProgressLineTwo.setMaxValue(totalTime);
        npvProgressLineThree.setValue(sleepTime);
        npvProgressLineThree.setMaxValue(totalTime);
        if (npvProgressLineOne.getProgress() + npvProgressLineTwo.getProgress() + npvProgressLineThree.getProgress() > 100) {
            npvProgressLineThree.setProgress(100 - npvProgressLineOne.getProgress() - npvProgressLineTwo.getProgress());
        }
        npvProgressLineOne.setLabel(mContext.getString(R.string.data_sleep_wake_duration));
        npvProgressLineTwo.setLabel(mContext.getString(R.string.data_sleep_light_duration1));
        npvProgressLineThree.setLabel(mContext.getString(R.string.data_sleep_deep_duration));
    }

    public View getChartHeadView() {
        View chartHeadView = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_chart_head_view, null);
        llSleepTag = (LinearLayout) chartHeadView.findViewById(R.id.ll_sleep_tag);
        llAwakeTag = (LinearLayout) chartHeadView.findViewById(R.id.ll_awake_tag);
        llChartView = (LinearLayout) chartHeadView.findViewById(R.id.ll_chart_view);
        rlChartLabel = (RelativeLayout) chartHeadView.findViewById(R.id.rl_chart_label);
        tvSleepTime = (TextView) chartHeadView.findViewById(R.id.tv_sleep_time);
        tvAwakeTime = (TextView) chartHeadView.findViewById(R.id.tv_awake_time);
        return chartHeadView;
    }

    //
    public View getChartFootView() {
        View napProgressView = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_time_progress, null);
        initSleepProgressView(napProgressView);
        return napProgressView;
//        if (wearQuality == -1) {
//            return napProgressView;
//        }
//        if (Constants.isSleepRecord) {
//            switch (SleepCommentsHelperKt.getWearQualityComment(wearQuality)) {
//                case S1:
//                case S2:
//                    tvWearWarningText.setText(R.string.report_sleep_wear_tip);
//                    return wearWarningView;
//                default:
//                    return napProgressView;
//            }
//        } else {
//            switch (SleepCommentsHelperKt.getWearQualityComment(wearQuality)) {
//                case S1:
//                    tvWearWarningText.setText(R.string.report_nap_wear_tip_0);
//                    return wearWarningView;
//                case S2:
//                    tvWearWarningText.setText(R.string.report_nap_wear_tip_1);
//                    return wearWarningView;
//                default:
//                    return napProgressView;
//            }

    }

    public TreeMap<Integer, String> dealPoints(TreeMap<Integer, String> point, int showPointMaxCount) {
        TreeMap<Integer, String> showPoints = point;
        while (showPoints.size() > showPointMaxCount) {
            List<Integer> keyList = new ArrayList<>();
            Iterator<Integer> integers = showPoints.keySet().iterator();
            while (integers.hasNext()) {
                keyList.add(integers.next().intValue());
            }
            showPoints = new TreeMap<>();
            for (int i = 0; i < keyList.size(); i++) {
                if (i % 2 == 0) {
                    showPoints.put(keyList.get(i).intValue(), point.get(keyList.get(i)));
                }
            }
        }
        return showPoints;
    }

    public TreeMap<Integer, String> getNeedShowPoint(FileProtocol<? extends BrainDataUnit> fileData) {
        TreeMap<Integer, String> allPoints = new TreeMap<>();
        TreeMap<Integer, String> showPoints;
        int lastMin = -1;
        for (int i = 0; i < valueList.size(); i++) {
            String time = getTimeByIndex(fileData, i);
            int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
            if (min == lastMin) {
                continue;
            }
            lastMin = min;
            if (min % 5 == 0) {
                allPoints.put(i, getTimeByIndex(fileData, i).replace(" ", "\n"));
            }
        }
        showPoints = dealPoints(allPoints, 8);
        return showPoints;
    }

    public void initChartView(CustomLineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
//        lineChart.setBorderColor(Color.parseColor("#ebebeb"));
        //是否可以拖动
        lineChart.setDragEnabled(false);
        // 是否有触摸事件
        lineChart.setTouchEnabled(false);
        lineChart.setScaleEnabled(false);
        // 设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);
        lineChart.setExtraBottomOffset(17);
        //  /***XY轴的设置***/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return needShowPoint.get((int) value) == null ? "" : needShowPoint.get((int) value);
            }
        });
        lineChart.getAxisRight().setEnabled(false);
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setDrawZeroLine(false);
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setEnabled(true);
        leftYAxis.setAxisMaximum(100);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setDrawLabels(false);
//        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setGranularity(1f);
        leftYAxis.setTextColor(Color.parseColor("#666666"));
        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                if ((int) value == 85) {
//                    return mContext.getString(R.string.data_state_wake);
//                } else if ((int) value == 50) {
//                    return mContext.getString(R.string.data_state_sleep);
//                } else if ((int) value == 15) {
//                    return mContext.getString(R.string.data_state_deep_sleep);
//                }
                return "";
            }
        });

        LimitLine ll2 = new LimitLine(50, getContext().getString(R.string.data_state_light_sleep));
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll2.setLineColor(Color.TRANSPARENT);
        ll2.setTextColor(R.color.colorGrayText);
        ll2.setLineWidth(0);
        leftYAxis.addLimitLine(ll2);

        LimitLine ll3 = new LimitLine(15, getContext().getString(R.string.data_state_deep_sleep));
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll3.setLineColor(Color.TRANSPARENT);
        ll3.setTextColor(R.color.colorGrayText);
        ll3.setLineWidth(0);
        leftYAxis.addLimitLine(ll3);

        LimitLine ll1 = new LimitLine(85, getContext().getString(R.string.data_state_awake));
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll1.setLineColor(Color.TRANSPARENT);
        ll1.setTextColor(R.color.colorGrayText);
        ll1.setLineWidth(1);
        leftYAxis.addLimitLine(ll1);


        // X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawGridLinesBehindData(false);
        xAxis.setGridLineWidth(1);
        xAxis.setTextColor(Color.parseColor("#999999"));
        for (Map.Entry<Integer, String> entry : needShowPoint.entrySet()) {
            LimitLine llX = new LimitLine(entry.getKey(), "");
            llX.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llX.setLineColor(Color.parseColor("#ebebeb"));
            llX.setTextColor(R.color.colorSleepChartGridLine);
            llX.setLineWidth(1);
            xAxis.addLimitLine(llX);
        }
        if (sleepPoint != -1) {
            LimitLine llX = new LimitLine(sleepPoint, "");
            llX.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llX.setLineColor(R.color.colorSleepChartPoint);
            llX.setTextColor(R.color.colorSleepChartPoint);
            xAxis.addLimitLine(llX);
        }
        if (wakePoint != -1) {
            LimitLine llX = new LimitLine(wakePoint, "");
            llX.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llX.setLineColor(R.color.colorSleepChartPoint);
            llX.setTextColor(R.color.colorSleepChartPoint);
            xAxis.addLimitLine(llX);
        }
        xAxis.setLabelCount(valueList.size());
        xAxis.setGranularity(1f);
        lineChart.setXAxisRenderer(new CustomXAxisRendererHorizontalLineChart(lineChart.getViewPortHandler(), xAxis,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT), valueList.size()));
        lineChart.setRendererLeftYAxis(new CustomYAxisRendererHorizontalLineChart(lineChart.getViewPortHandler(), leftYAxis,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT), valueList.size()));
        // 保证Y轴从0开始，不然会上移一点
//        leftYAxis.setAxisMinimum(0f)
//        rightYaxis.setAxisMinimum(0f)
        //  /***折线图例 标签 设置***/
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        lineChart.setDescription(null);

        //设置曲线数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            Entry entry;
            if (sleepPoint != 0 &&  i == sleepPoint) {
                entry = new Entry(i, valueList.get(i).intValue(),
                        ContextCompat.getDrawable(mContext, R.drawable.shape_sleep_chart_point));
            } else {
                entry = new Entry(i, valueList.get(i).intValue());
            }
            entries.add(entry);
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        initLineDataSet(lineDataSet, Color.parseColor("#7978ff"), LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(lineDataSet);
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(6f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setDrawIcons(true);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_sleep_chart_fill_bg));
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    public static String getTimeByIndex(FileProtocol<? extends BrainDataUnit> fileProtocol,
                                        int index) {
        if (fileProtocol == null || fileProtocol.getList() == null || fileProtocol.getList().size() == 0) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fileProtocol.getTick() * 1000);
        calendar.add(Calendar.SECOND, -(((MeditationReportDataAnalyzed) (fileProtocol.getList().get(0))).getSleepCurve().size() - 1 - index) * 8);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return formatter.format(calendar.getTimeInMillis());
    }

}
