
/*
 * Copyright (c) 2019 Roman Tsarou
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bz.kakadu.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import static bz.kakadu.calendar.CalendarTheme.getColor;

/**
 * Created on 09.11.2016
 *
 * @author Roman Tsarou
 */
public class SimpleCellDecoration implements ICellDecoration {
    protected final Day today = Day.today();
    protected final Paint paintDay;
    protected final Paint paintToday;
    protected final Paint paintHeader;
    protected final Paint paintTodayCircle;
    protected CalendarTheme theme;

    public SimpleCellDecoration() {
        paintDay = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDay.setTextAlign(Paint.Align.CENTER);
        paintToday = new Paint(paintDay);
        paintHeader = new Paint(paintDay);
        paintTodayCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTodayCircle.setStyle(Paint.Style.STROKE);
    }


    @Override
    public void setCalendarTheme(@NonNull CalendarTheme theme) {
        this.theme = theme;
        paintHeader.setColor(theme.headerTextColor);
        paintTodayCircle.setStrokeWidth(theme.todayCircleStrokeWidth);
        paintToday.setFakeBoldText(theme.todayBold);
        paintDay.setFakeBoldText(theme.dateTextBold);
        paintHeader.setFakeBoldText(theme.headerTextBold);
    }

    @Override
    @CallSuper
    public void onPreDraw(View parent, Canvas canvas, float cellWidth, float cellHeight) {
        if (theme.todayShow) {
            today.set(Day.todayHash());
        }
        final float minSize = Math.min(cellHeight, cellWidth);
        paintDay.setTextSize(minSize * theme.dateTextScale);
        paintToday.setTextSize(paintDay.getTextSize());
        paintHeader.setTextSize(minSize * theme.headerTextScale);
    }

    @Override
    public void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds) {
        if (cell.day.hashCode() == 0) {
            DrawUtils.drawText(cell.getValue(), paintHeader, canvas, bounds);
        } else {
            final boolean isEnabled = isEnabled(cell.day);
            if (theme.todayShow && today.equals(cell.getDay())) {
                paintTodayCircle.setColor(getColor(theme.todayCircleStrokeColor, isEnabled));
                DrawUtils.drawCircle(paintTodayCircle, canvas, bounds, theme.todayCircleScale);
            }
            DrawUtils.drawText(cell.getValue(), getDayTextPaint(cell.day), canvas, bounds);
        }
    }

    protected Paint getDayTextPaint(Day day) {
        if (today.equals(day) && theme.todayShow) {
            paintToday.setColor(getColor(theme.todayTextColor, isEnabled(day)));
            return paintToday;
        }
        paintDay.setColor(getColor(theme.dateTextColor, isEnabled(day)));
        return paintDay;
    }

    protected boolean isEnabled(Day day) {
        return true;
    }

}
