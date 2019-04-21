package bz.kakadu.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.view.SoundEffectConstants;
import android.view.View;

public class RangeCellDecoration extends SimpleCellDecoration implements OnDayClickListener {
    public enum Mode {RANGE, SINGLE}

    public Mode mode = Mode.RANGE;
    public int rangeEdgesColor;
    public float rangeEdgesHeightScale = circleTodayScale;
    @ColorInt
    public int rangeColor;
    @ColorInt
    public int disableTextColor;
    @ColorInt
    public int rangeTextColor;
    public float rangeHeightScale = rangeEdgesHeightScale * .9f;
    @ColorInt
    public int headerTextColor;
    public float headerTextScale = textScale * .6f;
    public boolean headerTextBold = true;
    private Integer minRangeDay = null;
    private Integer maxRangeDay = null;
    private Integer fromDay = null;
    private Integer toDay = null;


    public RangeCellDecoration(Context context) {
        super(context);
        disableTextColor = DrawUtils.getPrimaryTextColor(context, false);
        headerTextColor = DrawUtils.getSecondaryTextColor(context, true);
        rangeEdgesColor = DrawUtils.getAttrColor(context, R.attr.colorAccent);
        rangeTextColor = DrawUtils.getAttrColor(context, android.R.attr.textColorPrimaryInverse);
        rangeColor = ColorUtils.setAlphaComponent(rangeEdgesColor, 100);
    }

    @Override
    public void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds) {
        final boolean isHeaderCell = cell.day.hashCode() == 0;
        paintDay.setFakeBoldText(isHeaderCell && headerTextBold);
        paintDay.setTextSize(Math.min(bounds.height(), bounds.width()) * (isHeaderCell ? headerTextScale : textScale));
        final int textColor;
        if (isHeaderCell) {
            textColor = headerTextColor;
        } else {
            textColor = getTextColor(cell.day.hashCode());
        }
        paintDay.setColor(textColor);
        if (textColor == rangeTextColor) {
            final int rangeType = getRangeType(cell.day.hashCode());
            if (rangeType != -1) {
                DrawUtils.drawPeriod(rangeType, rangeColor, canvas, bounds, rangeHeightScale);
            }
            if ((fromDay != null && cell.day.hashCode() == fromDay)
                    || (toDay != null && cell.day.hashCode() == toDay)) {
                DrawUtils.drawCircle(rangeEdgesColor, canvas, bounds, rangeEdgesHeightScale);
            }
        }
        super.onCellDraw(parent, cell, canvas, bounds);
    }

    public void setBoundsRange(@Nullable Day min, @Nullable Day max) {
        minRangeDay = min == null ? null : min.hashCode();
        maxRangeDay = max == null ? null : max.hashCode();
        checkValues();
    }

    public void setRange(@Nullable Day from, @Nullable Day to) {
        fromDay = from == null ? null : from.hashCode();
        toDay = to == null ? null : to.hashCode();
        checkValues();
    }

    private void checkValues() {
        if (minRangeDay != null && maxRangeDay != null) {
            if (maxRangeDay < minRangeDay) {
                int min = maxRangeDay;
                maxRangeDay = minRangeDay;
                minRangeDay = min;
            }
        }
        if (fromDay != null && toDay != null) {
            if (toDay < fromDay) {
                int min = toDay;
                toDay = fromDay;
                fromDay = min;
            }
        }
        if (minRangeDay != null && fromDay != null && fromDay < minRangeDay) {
            fromDay = minRangeDay;
        }
        if (maxRangeDay != null && fromDay != null && fromDay > maxRangeDay) {
            fromDay = maxRangeDay;
        }

        if (maxRangeDay != null && toDay != null && toDay > maxRangeDay) {
            toDay = maxRangeDay.hashCode();
        }
    }

    private int getRangeType(int day) {
        if (fromDay == null || toDay == null || fromDay.equals(toDay) || mode == Mode.SINGLE)
            return -1;
        if (fromDay == day) return DrawUtils.START;
        if (toDay == day) return DrawUtils.END;
        return DrawUtils.MIDDLE;
    }

    private int getTextColor(int day) {
        if (minRangeDay != null && day < minRangeDay) return disableTextColor;
        if (maxRangeDay != null && day > maxRangeDay) return disableTextColor;
        if (fromDay != null && day == fromDay) return rangeTextColor;
        if (toDay != null && day == toDay) return rangeTextColor;
        if (fromDay != null && toDay != null && day > fromDay && day < toDay)
            return rangeTextColor;
        return textColor;
    }

    @Nullable
    public Day getRangeFrom() {
        return new Day(fromDay);
    }

    @Nullable
    public Day getRangeTo() {
        return new Day(toDay);
    }

    @Override
    public void onDayClick(View dayView, Day day) {
        int dayHash = day.hashCode();
        if (minRangeDay != null && (dayHash < minRangeDay)) return;
        if (maxRangeDay != null && (dayHash > maxRangeDay)) return;
        if (fromDay != null && dayHash == fromDay && toDay == null) return;
        if (fromDay != null && dayHash == fromDay && mode == Mode.SINGLE) return;
        if (fromDay == null || mode == Mode.SINGLE) {
            fromDay = dayHash;
        } else if (toDay == null) {
            toDay = dayHash;
            checkValues();
        } else {
            toDay = null;
            fromDay = dayHash;
        }
        dayView.playSoundEffect(SoundEffectConstants.CLICK);
        MonthView.invalidateAll(dayView.getContext());

    }
}
