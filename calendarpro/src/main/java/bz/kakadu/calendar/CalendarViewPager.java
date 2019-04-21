package bz.kakadu.calendar;

import android.content.Context;
import android.os.Build;
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
    private final SimpleDateFormat titleFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    private final ViewPager viewPager;
    private final View header;
    private final View prevBtn;
    private final View nextBtn;
    public OnDayClickListener onDayClickListener;
    private List<ICellDecoration> decorations = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    public CalendarViewPager(@NonNull Context context) {
        this(context, null);
    }

    public CalendarViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_calendar_pager, this);
        viewPager = findViewById(R.id.view_pager);
        header = findViewById(R.id.month_page_header);
        prevBtn = header.findViewById(R.id.prev_month_btn);
        nextBtn = header.findViewById(R.id.next_month_btn);
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
            MonthView monthView = page.findViewById(R.id.month_view);
            TextView title = page.findViewById(R.id.month_title);
            title.setText(getPageTitle(position));
            monthView.setMonth(day);
            monthView.alwaysSixWeekRows = true;
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
            String title;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG_STANDALONE, Locale.getDefault());
                title = name + ", " + calendar.get(Calendar.YEAR);
            } else {
                title = titleFormat.format(calendar.getTime());
            }
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            return title;
        }
    }
}
