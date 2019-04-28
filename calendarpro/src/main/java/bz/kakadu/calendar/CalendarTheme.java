package bz.kakadu.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;

public class CalendarTheme {
    public static final int[] STATE_ENABLED = {android.R.attr.state_enabled};
    public static final int[] STATE_DISABLED = {-android.R.attr.state_enabled};
    private static final int defaultColor = Color.MAGENTA;
    public final int calendarTheme; // @style/CalendarTheme
    public boolean alwaysSixWeeks; // false
    public boolean headerShow; // true
    public boolean headerTextBold; // true
    public int headerTextColor; // ?android:textColorSecondary
    public float headerTextScale; // 20%
    public boolean dateTextBold; // false
    public ColorStateList dateTextColor; // ?android:textColorPrimary
    public float dateTextScale; // 34%
    public int rangeTextColor; // ?android:textColorPrimaryInverse
    public int rangeEdgesColor; // ?colorAccent
    public int rangeEdgesTextColor; // ?android:textColorPrimaryInverse
    public int rangeColor; // ?calendarRangeEdgesColor
    public float rangeAlpha; // 50%
    public boolean rangeEdgesShow; // true
    public float rangeEdgesScale; // ?calendarTodayCircleScale
    public float rangeScale; // 75%
    public boolean todayShow; // true
    public boolean todayBold; // false
    public ColorStateList todayTextColor; // ?android:textColorPrimary
    public ColorStateList todayCircleStrokeColor; // ?android:textColorPrimary
    public float todayCircleStrokeWidth; // 1dp
    public float todayCircleScale; // 80%
    public String calendarTitleDateFormat; // "LLLL, yyyy"

    public CalendarTheme(Context context) {
        TypedArray themeArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.calendarTheme});
        try {
            calendarTheme = themeArray.getResourceId(0, R.style.CalendarTheme);
            themeArray.recycle();
            themeArray = context.obtainStyledAttributes(null, R.styleable.CalendarTheme, R.attr.calendarTheme, R.style.CalendarTheme);
            alwaysSixWeeks = themeArray.getBoolean(R.styleable.CalendarTheme_calendarAlwaysSixWeeks, false);
            headerShow = themeArray.getBoolean(R.styleable.CalendarTheme_calendarHeaderShow, true);
            headerTextBold = themeArray.getBoolean(R.styleable.CalendarTheme_calendarHeaderTextBold, true);
            dateTextBold = themeArray.getBoolean(R.styleable.CalendarTheme_calendarDateTextBold, false);
            rangeEdgesShow = themeArray.getBoolean(R.styleable.CalendarTheme_calendarRangeEdgesShow, true);
            todayShow = themeArray.getBoolean(R.styleable.CalendarTheme_calendarTodayShow, true);
            todayBold = themeArray.getBoolean(R.styleable.CalendarTheme_calendarTodayBold, false);
            dateTextColor = getColorStateList(themeArray, R.styleable.CalendarTheme_calendarDateTextColor);
            rangeTextColor = getColor(themeArray, R.styleable.CalendarTheme_calendarRangeTextColor);
            rangeEdgesTextColor = getColor(themeArray, R.styleable.CalendarTheme_calendarRangeEdgesTextColor);
            todayTextColor = getColorStateList(themeArray, R.styleable.CalendarTheme_calendarTodayTextColor);
            todayCircleStrokeColor = getColorStateList(themeArray, R.styleable.CalendarTheme_calendarTodayCircleStrokeColor);
            headerTextColor = getColor(themeArray, R.styleable.CalendarTheme_calendarHeaderTextColor);
            rangeEdgesColor = getColor(themeArray, R.styleable.CalendarTheme_calendarRangeEdgesColor);
            rangeColor = getColor(themeArray, R.styleable.CalendarTheme_calendarRangeColor);
            todayCircleStrokeWidth = themeArray.getDimension(R.styleable.CalendarTheme_calendarTodayCircleStrokeWidth, 1);
            headerTextScale = themeArray.getFraction(R.styleable.CalendarTheme_calendarHeaderTextScale, 1, 1, 1);
            dateTextScale = themeArray.getFraction(R.styleable.CalendarTheme_calendarDateTextScale, 1, 1, 1);
            rangeAlpha = themeArray.getFraction(R.styleable.CalendarTheme_calendarRangeAlpha, 1, 1, 1);
            rangeEdgesScale = themeArray.getFraction(R.styleable.CalendarTheme_calendarRangeEdgesScale, 1, 1, 1);
            rangeScale = themeArray.getFraction(R.styleable.CalendarTheme_calendarRangeScale, 1, 1, 1);
            todayCircleScale = themeArray.getFraction(R.styleable.CalendarTheme_calendarTodayCircleScale, 1, 1, 1);
            calendarTitleDateFormat = themeArray.getString(R.styleable.CalendarTheme_calendarTitleDateFormat);
        } finally {
            themeArray.recycle();
        }
    }

    public static int getColor(ColorStateList colorStateList, boolean isEnabled) {
        return colorStateList.getColorForState(isEnabled ? STATE_ENABLED : STATE_DISABLED, defaultColor);
    }

    private static ColorStateList getColorStateList(TypedArray themeArray, int attr) {
        ColorStateList stateList = themeArray.getColorStateList(attr);
        if (stateList != null) {
            return stateList;
        } else {
            return ColorStateList.valueOf(themeArray.getColor(attr, defaultColor));
        }
    }

    private static int getColor(TypedArray themeArray, int attr) {
        ColorStateList stateList = themeArray.getColorStateList(attr);
        if (stateList != null) {
            return stateList.getDefaultColor();
        } else {
            return themeArray.getColor(attr, defaultColor);
        }
    }

    Context getThemedContext(Context context) {
        context.getTheme().applyStyle(calendarTheme, false);
        return context;
    }
}
