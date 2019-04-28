package bz.kakadu.calendar;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created on 09.11.2016
 *
 * @author Roman Tsarou
 */
public interface ICellDecoration {
    void setCalendarTheme(@NonNull CalendarTheme theme);

    void onPreDraw(View parent, Canvas canvas, float cellWidth, float cellHeight);

    void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds);
}
