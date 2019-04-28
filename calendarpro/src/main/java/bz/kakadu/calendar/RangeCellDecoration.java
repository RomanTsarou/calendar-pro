package bz.kakadu.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.view.SoundEffectConstants;
import android.view.View;

public class RangeCellDecoration extends SimpleCellDecoration implements OnDayClickListener {
    public enum Mode {RANGE, SINGLE}

    public Mode mode = Mode.RANGE;
    private Integer minRangeDay = null;
    private Integer maxRangeDay = null;
    private Integer fromDay = null;
    private Integer toDay = null;
    private final Paint paintRangeEdges;
    private final Paint paintRangeEdgesText;
    private final Paint paintRangeText;
    private int paintRangeColor;


    public RangeCellDecoration() {
        super();
        paintRangeEdges = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRangeEdgesText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRangeEdgesText.setTextAlign(Paint.Align.CENTER);
        paintRangeText = new Paint(paintRangeEdgesText);
    }

    @Override
    public void setCalendarTheme(@NonNull CalendarTheme theme) {
        super.setCalendarTheme(theme);
        paintRangeEdges.setColor(theme.rangeEdgesColor);
        paintRangeColor = ColorUtils.setAlphaComponent(theme.rangeColor, (int) (255 * theme.rangeAlpha));
        paintRangeEdgesText.setColor(theme.rangeEdgesTextColor);
        paintRangeText.setColor(theme.rangeTextColor);
        paintRangeEdgesText.setFakeBoldText(paintDay.isFakeBoldText());
        paintRangeText.setFakeBoldText(paintDay.isFakeBoldText());

    }

    @Override
    public void onPreDraw(View parent, Canvas canvas, float cellWidth, float cellHeight) {
        super.onPreDraw(parent, canvas, cellWidth, cellHeight);
        paintRangeEdgesText.setTextSize(paintDay.getTextSize());
        paintRangeText.setTextSize(paintDay.getTextSize());
    }

    @Override
    public void onCellDraw(View parent, Cell cell, Canvas canvas, RectF bounds) {
        int dayHash = cell.day.hashCode();
        int rangeType = getRangeType(dayHash);
        if (rangeType != -1) {
            DrawUtils.drawPeriod(rangeType, paintRangeColor, canvas, bounds, theme.rangeScale);
        }
        final boolean isEdge = (fromDay != null && cell.day.hashCode() == fromDay)
                || (toDay != null && cell.day.hashCode() == toDay);
        if (isEdge) {
            if (theme.rangeEdgesShow) {
                DrawUtils.drawCircle(theme.rangeEdgesColor, canvas, bounds, theme.rangeEdgesScale);
            }
        }
        super.onCellDraw(parent, cell, canvas, bounds);
    }

    @Override
    protected Paint getDayTextPaint(Day day) {
        final boolean isEdge = (fromDay != null && day.hashCode() == fromDay)
                || (toDay != null && day.hashCode() == toDay);
        if (isEdge && theme.rangeEdgesShow) {
            if (theme.todayShow && today.equals(day)) {
                paintToday.setColor(paintRangeEdgesText.getColor());
                return paintToday;
            } else {
                return paintRangeEdgesText;
            }
        }
        final int rangeType = getRangeType(day.hashCode());
        if (rangeType != -1) {
            if (theme.todayShow && today.equals(day)) {
                paintToday.setColor(paintRangeText.getColor());
                return paintToday;
            } else {
                return paintRangeText;
            }
        }
        return super.getDayTextPaint(day);
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

    @Override
    protected boolean isEnabled(Day day) {
        if (minRangeDay != null && day.hashCode() < minRangeDay) return false;
        if (maxRangeDay != null && day.hashCode() > maxRangeDay) return false;
        return true;
    }

    private int getRangeType(int day) {
        if (fromDay == null) return -1;
        if (fromDay == day && (mode == Mode.SINGLE || toDay == null)) return DrawUtils.SINGLE;
        if (toDay == null) return -1;
        if (fromDay == day) return DrawUtils.START;
        if (toDay == day) return DrawUtils.END;
        if (day > fromDay && day < toDay) return DrawUtils.MIDDLE;
        return -1;
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
        if (!isEnabled(day)) return;
        int dayHash = day.hashCode();
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
