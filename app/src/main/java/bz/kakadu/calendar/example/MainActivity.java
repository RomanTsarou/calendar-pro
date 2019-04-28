package bz.kakadu.calendar.example;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import bz.kakadu.calendar.CalendarTheme;
import bz.kakadu.calendar.CalendarViewPager;
import bz.kakadu.calendar.Day;
import bz.kakadu.calendar.RangeCellDecoration;

public class MainActivity extends AppCompatActivity {
    RangeCellDecoration rangeCellDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarViewPager monthView = findViewById(R.id.monthView);
        rangeCellDecoration = new RangeCellDecoration();
        Day day1 = Day.today().add(-10);
        Day day2 = Day.today().add(50);
        rangeCellDecoration.setBoundsRange(day1, day2);
//        int color = DrawUtils.getAttrColor(this, R.attr.colorPrimary);
//        rangeCellDecoration.rangeEdgesColor = color;
//        rangeCellDecoration.rangeColor = ColorUtils.setAlphaComponent(color, 127);
        monthView.setCurrentMonth(day1, false);
        monthView.addDecoration(rangeCellDecoration);
        monthView.onDayClickListener = rangeCellDecoration;
        CalendarTheme ct = new CalendarTheme(this);
    }

    public void dialog(View view) {
        CalendarViewPager monthView = new CalendarViewPager(this);
        monthView.onDayClickListener = rangeCellDecoration;
        monthView.addDecoration(rangeCellDecoration);
        monthView.setCurrentMonth(Day.today(), false);
        new AlertDialog.Builder(this)
                .setView(monthView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
