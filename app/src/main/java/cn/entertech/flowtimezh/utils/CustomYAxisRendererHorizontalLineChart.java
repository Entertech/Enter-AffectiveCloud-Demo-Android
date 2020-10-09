package cn.entertech.flowtimezh.utils;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomYAxisRendererHorizontalLineChart extends YAxisRenderer {
    private int mLabelCount;
    private boolean mIsDrawYAxisScale;

    public CustomYAxisRendererHorizontalLineChart(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans, int labelCount) {
        super(viewPortHandler, yAxis, trans);
        this.mLabelCount = labelCount;
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        float yMin = min;
        float yMax = max;

        int labelCount = mLabelCount; // This is the only change
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0 || Double.isInfinite(range)) {
            mAxis.mEntries = new float[]{};
            mAxis.mCenteredEntries = new float[]{};
            mAxis.mEntryCount = 0;
            return;
        }

        // Find out how much spacing (in y value space) between axis values
        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled())
            interval = interval < mAxis.getGranularity() ? mAxis.getGranularity() : interval;

        // Normalize interval
        double intervalMagnitude = Utils.roundToNextSignificant(Math.pow(10, (int) Math.log10(interval)));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        int n = mAxis.isCenterAxisLabelsEnabled() ? 1 : 0;

        // force label count
        if (mAxis.isForceLabelsEnabled()) {

            interval = (float) range / (float) (labelCount - 1);
            mAxis.mEntryCount = labelCount;

            if (mAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mAxis.mEntries[i] = v;
                v += interval;
            }

            n = labelCount;

            // no forced count
        } else {

            double first = interval == 0.0 ? 0.0 : Math.ceil(yMin / interval) * interval;
            if (mAxis.isCenterAxisLabelsEnabled()) {
                first -= interval;
            }

            double last = interval == 0.0 ? 0.0 : Utils.nextUp(Math.floor(yMax / interval) * interval);

            double f;
            int i;

            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }

            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {

                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0;

                mAxis.mEntries[i] = (float) f;
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mAxis.mDecimals = 0;
        }

        if (mAxis.isCenterAxisLabelsEnabled()) {

            if (mAxis.mCenteredEntries.length < n) {
                mAxis.mCenteredEntries = new float[n];
            }

            float offset = (float) interval / 2f;

            for (int i = 0; i < n; i++) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset;
            }
        }

//        computeSize();
    }
//
    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled()
                ? mYAxis.mEntryCount
                : (mYAxis.mEntryCount - 1);

        String lastText = "";
        // draw
        for (int i = from; i < to; i++) {
            String text = mYAxis.getFormattedLabel(i);
            mAxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
            Paint.FontMetrics fontMetrics = mAxisLabelPaint.getFontMetrics();
            float textHeight = Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom);
            if (lastText.equals(text)){
                continue;
            }
            if (mIsDrawYAxisScale && !"".equals(text)){
                c.drawLine(mViewPortHandler.contentLeft(),positions[i * 2 + 1],
                        mViewPortHandler.contentLeft()+ Utils.convertDpToPixel(5),positions[i * 2 + 1],
                        mAxisLinePaint);
            }
            if (text.contains("\n")) {
//                Log.d("###","draw y label..."+text);
                c.drawText(text.split("\n")[0], fixedPosition,
                        positions[i * 2 + 1] + offset, mAxisLabelPaint);
                c.drawText(text.split("\n")[1], fixedPosition,
                        positions[i * 2 + 1] + offset + textHeight, mAxisLabelPaint);
            }else{
                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
            lastText = text;
        }
    }

    public void isDrawYAxisScale(boolean enable){
        this.mIsDrawYAxisScale = enable;
    }

//
//    boolean isFirstDrawAxisLine = true;
//    @Override
//    public void renderAxisLine(Canvas c) {
//        if (!mYAxis.isEnabled() || !mYAxis.isDrawAxisLineEnabled())
//            return;
////
////        mAxisLinePaint.setColor(Color.parseColor("#6665ff"));
////        mAxisLinePaint.setStrokeWidth(Utils.convertDpToPixel(1));
//
//        Log.d("###","drawAxisLine.."+mViewPortHandler.contentBottom());
//        if (mYAxis.getAxisDependency() == YAxis.AxisDependency.LEFT) {
//            c.drawLine(mViewPortHandler.contentLeft(), mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
//                    mViewPortHandler.contentBottom(), mAxisLinePaint);
//        } else {
//            c.drawLine(mViewPortHandler.contentRight(), mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
//                    mViewPortHandler.contentBottom(), mAxisLinePaint);
//        }
//        isFirstDrawAxisLine = false;
//    }

}
