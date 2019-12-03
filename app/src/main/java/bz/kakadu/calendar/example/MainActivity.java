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

package bz.kakadu.calendar.example;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
        rangeCellDecoration.setRangeBounds(
                Day.today().add(-1),
                Day.today().add(14)
        );
        rangeCellDecoration.setRange(
                Day.today(),
                Day.today().add(3));
        monthView.setCurrentMonth(Day.today(), false);
        monthView.addDecoration(rangeCellDecoration);
        monthView.onDayClickListener = rangeCellDecoration;
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
