package com.smorodya.hobbymanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.smorodya.hobbymanager.R;
import com.google.android.material.color.MaterialColors;

public class DonutChartView extends View {

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF oval = new RectF();

    private int done = 0;
    private int total = 0;

    public DonutChartView(Context context) { super(context); init(); }
    public DonutChartView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }
    public DonutChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        int onSurface = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface);

        int green = ContextCompat.getColor(getContext(), R.color.green);

        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(dp(14));
        bgPaint.setColor(0x22000000);

        fgPaint.setStyle(Paint.Style.STROKE);
        fgPaint.setStrokeWidth(dp(14));
        fgPaint.setStrokeCap(Paint.Cap.ROUND);
        fgPaint.setColor(green);

        textPaint.setColor(onSurface);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp(18));
        textPaint.setFakeBoldText(true);
    }

    public void setData(int done, int total) {
        this.done = Math.max(0, done);
        this.total = Math.max(0, total);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        float pad = dp(18);
        oval.set(pad, pad, getWidth() - pad, getHeight() - pad);

        canvas.drawArc(oval, 0, 360, false, bgPaint);

        float percent = (total == 0) ? 0f : (done * 1f / total);
        float sweep = 360f * percent;

        canvas.drawArc(oval, -90, sweep, false, fgPaint);

        int pct = Math.round(percent * 100f);
        canvas.drawText(pct + "%", cx, cy + dp(6), textPaint);
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
