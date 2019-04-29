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

    /**
     * Call before draw all cells
     *
     * @param parent     MonthView
     * @param canvas     of parent view for draw
     * @param cellWidth  width of each cell
     * @param cellHeight height of each cell
     */
    void onPreDraw(View parent, Canvas canvas, float cellWidth, float cellHeight);

    /**
     * Call for each cell in MonthView
     *
     * @param parent MonthView
     * @param cell   data
     * @param canvas of parent view for draw
     * @param bounds contains size and position this cell
     */
    void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds);
}
