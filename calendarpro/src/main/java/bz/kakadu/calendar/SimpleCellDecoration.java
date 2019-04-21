package bz.kakadu.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.view.View;

/**
 * Created on 09.11.2016
 *
 * @author Roman Tsarou
 */
public class SimpleCellDecoration implements ICellDecoration {
    private static final float SCALE_TEXT = .34f;
    private final Day today = Day.today();
    public final Paint paintDay;
    public final Paint paintToday;
    @ColorInt
    public int textColor;
    public boolean isShowToday = true;
    public float circleTodayScale = .8f;
    public float textScale = SCALE_TEXT;

    public SimpleCellDecoration(Context context) {
        paintToday = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintToday.setStyle(Paint.Style.STROKE);
        paintDay = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDay.setTextAlign(Paint.Align.CENTER);
        textColor = DrawUtils.getPrimaryTextColor(context, true);
        paintToday.setStrokeWidth(context.getResources().getDisplayMetrics().density * 1);
    }


    @Override
    @CallSuper
    public void onPreDraw(View parent, Canvas canvas, float cellWidth, float cellHeight) {
        final float minSize = Math.min(cellHeight, cellWidth);
        paintDay.setTextSize(minSize * textScale);
        paintDay.setColor(textColor);
        paintToday.setColor(textColor);
    }

    @Override
    public void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds) {
        DrawUtils.drawText(cell.getValue(), paintDay, canvas, bounds);
        if (isShowToday && today.equals(cell.getDay())) {
            DrawUtils.drawCircle(paintToday, canvas, bounds, circleTodayScale);
        }
    }

    /**
     * May be call {@link MonthView#invalidateAll(Context)} after.
     */
    public void updateToday() {
        today.set(Day.todayHash());
    }

    public int getTodayHash() {
        return today.hashCode();
    }
}
