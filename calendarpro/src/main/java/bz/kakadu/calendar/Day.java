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

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.text.format.DateUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import static java.util.Calendar.APRIL;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MAY;
import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;

/**
 * Container for date values: day, month, year.
 * Hash is a single integer, for example 20161110.
 * Created on 10.11.2016
 *
 * @author Roman Tsarou
 */
public class Day implements Comparable<Day>, Cloneable {
    private static final Calendar calendar = Calendar.getInstance();
    private int hash;

    /**
     * @param date  from 1 to 31
     * @param month from {@link Calendar#JANUARY} to {@link Calendar#DECEMBER}
     * @param year  any
     */
    public Day(@IntRange(from = 1, to = 31) int date, @Month int month, int year) {
        set(date, month, year);
    }

    public Day() {
    }

    public Day(Calendar calendar) {
        set(calendar);
    }

    public Day(Day other) {
        hash = other.hash;
    }

    public Day(int hash) {
        set(hash);
    }

    public static Day today() {
        return new Day(todayHash());
    }

    public static int hash(Calendar calendar) {
        return calendar.get(Calendar.DATE) + calendar.get(Calendar.MONTH) * 100 + calendar.get(Calendar.YEAR) * 10000;
    }

    public static int todayHash() {
        synchronized (calendar) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            return hash(calendar);
        }
    }

    public Day add(int dayCount) {
        synchronized (calendar) {
            setTo(calendar);
            calendar.add(Calendar.DATE, dayCount);
            set(calendar);
            return this;
        }
    }

    public int getDate() {
        return hash % 100;
    }

    @Month
    public int getMonth() {
        return hash % 10000 / 100;
    }

    public int getYear() {
        return hash / 10000;
    }

    @Override
    public String toString() {
        return "Day: " + hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * @param date  from 1 to 31
     * @param month from {@link Calendar#JANUARY} to {@link Calendar#DECEMBER}
     * @param year  any
     * @return current object
     */
    public Day set(@IntRange(from = 1, to = 31) int date, @Month int month, int year) {
        hash = date + month * 100 + year * 10000;
        return this;
    }

    public Day set(int hash) {
        this.hash = hash;
        return this;
    }

    public Day set(Calendar calendar) {
        set(calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR));
        return this;
    }

    @Override
    public int compareTo(Day day) {
        return hashCode() - day.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Day && obj.hashCode() == hashCode();
    }

    @Override
    public Day clone() {
        try {
            return (Day) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean between(Day first, Day last) {
        return after(first) && before(last);
    }

    public boolean after(Day other) {
        return hashCode() > other.hashCode();
    }

    public boolean before(Day other) {
        return hashCode() < other.hashCode();
    }

    /**
     * @param target for values (date, month, year) from this day.
     *               Note: other values (time, etc.) will be reset to 0
     */
    public void setTo(Calendar target) {
        target.clear();
        target.set(Calendar.YEAR, getYear());
        target.set(Calendar.MONTH, getMonth());
        target.set(Calendar.DATE, getDate());
    }

    public int offsetDays(Day other) {
        return Math.round(offsetDaysF(other));
    }


    private float offsetDaysF(Day other) {
        synchronized (calendar) {
            setTo(calendar);
            final long time1 = calendar.getTimeInMillis();
            other.setTo(calendar);
            final long time2 = calendar.getTimeInMillis();
            return (1f * (time2 - time1) / DateUtils.DAY_IN_MILLIS);
        }

    }

    public void set(Day other) {
        hash = other.hash;
    }


    @IntDef({
            JANUARY, FEBRUARY, MARCH, APRIL, MAY, JULY, JUNE, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Month {
    }
}
