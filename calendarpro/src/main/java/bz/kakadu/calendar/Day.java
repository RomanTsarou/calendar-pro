package bz.kakadu.calendar;

import android.support.annotation.IntDef;
import android.text.format.DateUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
 * Created on 10.11.2016
 *
 * @author Roman Tsarou
 */
@SuppressWarnings("WrongConstant")
public class Day implements Comparable<Day>, Cloneable {
    private static final GregorianCalendar calendar = new GregorianCalendar();
    private int hash;

    public Day(int date, @Month int month, int year) {
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
        return  "Day: " + hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public Day set(int date, @Month int month, int year) {
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
    protected Day clone() {
        Day day = null;
        try {
            day = (Day) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return day;
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
