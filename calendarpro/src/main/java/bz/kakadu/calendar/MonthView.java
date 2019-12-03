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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;


/**
 * Created on 09.11.2016
 *
 * @author Roman Tsarou
 */
public class MonthView extends View {
    static final String ACTION_INVALIDATE = "invalidateDateViews.ACTION";
    private static final int NONE = -1;
    private final GregorianCalendar mCalendar = new GregorianCalendar();
    private final Cell[] mDayItems;
    private final Cell[] mHeaderItems;
    private final RectF cellBounds = new RectF();
    private final GestureDetectorCompat mDetector;
    private final BroadcastReceiver mInvalidateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidate();
        }
    };
    private int mWeekCount;
    private Set<ICellDecoration> mDecorations = new LinkedHashSet<>();
    private int mMonth;
    private int mYear;
    private OnDayClickListener mOnDayClickListener;
    private boolean isShowHeader = true;
    private boolean alwaysSixWeekRows = false;
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int pos = getCellPosition(e);
            if (pos == NONE || mOnDayClickListener == null) return false;
            mOnDayClickListener.onDayClick(MonthView.this, mDayItems[pos].getDay());
            return true;
        }


        @Override
        public boolean onDown(MotionEvent e) {
            return mOnDayClickListener != null && getCellPosition(e) != NONE;
        }
    };
    private float mCellHeight;

    public MonthView(Context context) {
        this(context, null);
    }

    private CalendarTheme calendarTheme;

    /**
     * @param dayOfWeek from {@link Calendar#SUNDAY} to {@link Calendar#SATURDAY}
     * @return Day name ex: SUN, MON, etc...
     */
    protected String getDayName(int dayOfWeek) {
        mCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase();

    }

    public static void invalidateAll(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_INVALIDATE));
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mInvalidateReceiver, new IntentFilter(ACTION_INVALIDATE));
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mInvalidateReceiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private int getCellPosition(MotionEvent e) {
        int row = (int) (e.getY() / cellBounds.height());
        if (isShowHeader) {
            row--;
        }
        int column = (int) (e.getX() / cellBounds.width());
        int pos = row * 7 + column;
        if (pos >= mDayItems.length || pos < 0) return NONE;
        Cell cell = mDayItems[pos];
        if (cell.isVisible) {
            return pos;
        }
        return NONE;
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDayItems = new Cell[7 * 6];
        for (int i = 0; i < mDayItems.length; i++) {
            mDayItems[i] = new Cell();
        }
        if (isInEditMode()) {
            addDecoration(new SimpleCellDecoration());
            setMonth(mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.YEAR));
        }
        mDetector = new GestureDetectorCompat(context, mOnGestureListener);


        mHeaderItems = new Cell[7];
        for (int i = 0; i < mHeaderItems.length; i++) {
            mHeaderItems[i] = new Cell();
            int dayOfWeek = i + mCalendar.getFirstDayOfWeek();
            if (dayOfWeek > Calendar.SATURDAY) {
                dayOfWeek -= Calendar.SATURDAY;
            }
            mHeaderItems[i].value = getDayName(dayOfWeek);
        }
    }

    public void setCalendarTheme(@NonNull CalendarTheme theme) {
        calendarTheme = theme;
        alwaysSixWeekRows = calendarTheme.alwaysSixWeeks;
        isShowHeader = calendarTheme.headerShow;
        for (ICellDecoration decoration : mDecorations) {
            decoration.setCalendarTheme(theme);
        }
        requestLayout();
        invalidate();
    }

    public void addDecoration(ICellDecoration decoration) {
        if (mDecorations.add(decoration)) {
            if (calendarTheme == null) {
                setCalendarTheme(new CalendarTheme(getContext()));
            } else {
                decoration.setCalendarTheme(calendarTheme);
            }
            invalidate();
        }

    }

    public void removeDecoration(ICellDecoration decoration) {
        if (mDecorations.remove(decoration)) {
            invalidate();
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (ICellDecoration decoration : mDecorations) {
            decoration.onPreDraw(this, canvas, cellBounds.width(), cellBounds.height());
        }
        if (isShowHeader) {
            for (int i = 0; i < mHeaderItems.length; i++) {
                cellBounds.offsetTo(
                        cellBounds.width() * (i % 7),
                        0
                );
                for (ICellDecoration decoration : mDecorations) {
                    decoration.onCellDraw(this, mHeaderItems[i], canvas, cellBounds);
                }
            }
        }
        for (int i = 0; i < mDayItems.length; i++) {
            if (!mDayItems[i].isVisible) continue;
            cellBounds.offsetTo(
                    cellBounds.width() * (i % 7),
                    cellBounds.height() * (i / 7) + (isShowHeader ? cellBounds.height() : 0)
            );
            for (ICellDecoration decoration : mDecorations) {
                decoration.onCellDraw(this, mDayItems[i], canvas, cellBounds);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        int rowCount = (alwaysSixWeekRows ? 6 : mWeekCount) + (isShowHeader ? 1 : 0);
        float cellWidth = width / 7f;
        int wrapContentHeight = (int) ((mCellHeight != 0 ? mCellHeight : cellWidth) * rowCount);
        int height = Math.min(maxHeight, wrapContentHeight);
        float cellHeight = 1f * height / rowCount;
        cellBounds.set(0, 0, cellWidth, cellHeight);
        setMeasuredDimension(width, height);
    }

    public int getWeekCount() {
        return mWeekCount;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public void setMonth(Day day) {
        setMonth(day.getMonth(), day.getYear());
    }

    public void setMonth(int month, int year) {
        if (mMonth == month
                && mYear == year) return;
        mMonth = month;
        mYear = year;
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOffset = mCalendar.get(Calendar.DAY_OF_WEEK) - mCalendar.getFirstDayOfWeek();
        if (firstDayOffset < 0) {
            firstDayOffset = 7 + firstDayOffset;
        }


        mWeekCount = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int daysCount = mCalendar.getActualMaximum(Calendar.DATE);
        resetCells();
        Cell cell;
        for (int i = 0; i < daysCount; i++) {
            mCalendar.set(Calendar.DATE, i + 1);

            cell = mDayItems[i + firstDayOffset];//FIXME
            cell.isVisible = true;
            cell.day.set(mCalendar);
            cell.value = String.valueOf(cell.day.getDate());
        }

        requestLayout();
        invalidate();
    }

    private void resetCells() {
        for (Cell cell : mDayItems) {
            cell.isVisible = false;
        }
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.mOnDayClickListener = onDayClickListener;
    }

    public void setCellHeight(float cellHeight) {
        if (cellHeight != mCellHeight) {
            mCellHeight = cellHeight;
            requestLayout();
            invalidate();
        }
    }

}
