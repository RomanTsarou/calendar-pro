package bz.kakadu.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarViewPager extends FrameLayout {
    private final int startYear = 1900;
    private final int endYear = 2100;
    public final ViewPager viewPager;
    public final View header;
    public final View prevBtn;
    public final View nextBtn;
    public final CalendarTheme theme;
    public OnDayClickListener onDayClickListener;
    private List<ICellDecoration> decorations = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    public SimpleDateFormat titleFormat;

    public CalendarViewPager(@NonNull Context context) {
        this(context, null);
    }

    public CalendarViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        theme = new CalendarTheme(context);
        theme.alwaysSixWeeks = true;
        titleFormat = new SimpleDateFormat(theme.calendarTitleDateFormat, Locale.getDefault());
        inflate(theme.getThemedContext(context), R.layout.calendar_view_pager, this);
        viewPager = findViewById(R.id.view_pager);
        header = findViewById(R.id.calendar_month_title_layout);
        prevBtn = header.findViewById(R.id.calendar_prev_month_btn);
        nextBtn = header.findViewById(R.id.calendar_next_month_btn);
        viewPager.setAdapter(new MonthAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateHeader();
            }
        });
        updateHeader();
        prevBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void updateHeader() {
        int pos = viewPager.getCurrentItem();
        prevBtn.setVisibility(pos == 0 ? INVISIBLE : VISIBLE);
        nextBtn.setVisibility(pos == viewPager.getAdapter().getCount() - 1 ? INVISIBLE : VISIBLE);
    }

    public void addDecoration(@NonNull ICellDecoration decoration) {
        decorations.add(decoration);
    }

    public void setCurrentMonth(Day day, boolean smoothScroll) {
        int pos = (day.getYear() - startYear) * 12 + day.getMonth();
        viewPager.setCurrentItem(pos, smoothScroll);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            int titleHeight = header.getMinimumHeight();
            int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int cellWidth = (width - getPaddingLeft() - getPaddingStart()) / 7;
            int wrapContentHeight = Math.max(getMinimumHeight(),
                    titleHeight + getPaddingTop() + getPaddingBottom() + cellWidth * 6);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(maxHeight, wrapContentHeight), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class MonthAdapter extends PagerAdapter {
        private Day day = new Day();

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            calendar.set(startYear, 0, 1);
            calendar.add(Calendar.MONTH, position);
            day.set(calendar);
            View page = LayoutInflater.from(container.getContext()).inflate(R.layout.calendar_pager_page, container, false);
            TextView title = page.findViewById(R.id.calendar_month_title);
            title.setText(getPageTitle(position));
            MonthView monthView = page.findViewById(R.id.calendar_month_view);
            monthView.setCalendarTheme(theme);
            monthView.setMonth(day);
            monthView.setOnDayClickListener(onDayClickListener);
            for (ICellDecoration decoration : decorations) {
                monthView.addDecoration(decoration);
            }
            container.addView(page, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return page;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return (endYear - startYear) * 12;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            calendar.set(startYear, 0, 1);
            calendar.add(Calendar.MONTH, position);
            return titleFormat.format(calendar.getTime());
        }
    }
}
