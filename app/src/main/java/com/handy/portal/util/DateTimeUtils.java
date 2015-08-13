package com.handy.portal.util;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateTimeUtils
{
    //TODO: refactor code throughout the app to put date formats here
    private static SimpleDateFormat CLOCK_FORMATTER_12HR = new SimpleDateFormat("h:mm a");

    public final static int MILLISECONDS_IN_MINUTE = 60000;
    public final static long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE*60;

    public static String formatDateTo12HourClock(Date date)
    {
        if (date == null) return null;
        return CLOCK_FORMATTER_12HR.format(date);
    }

    public static boolean equalCalendarDates(final Date date1, final Date date2)
    {
        final Time time = new Time();
        time.set(date1.getTime());

        final int thenYear = time.year;
        final int thenMonth = time.month;
        final int thenMonthDay = time.monthDay;

        time.set(date2.getTime());

        return (thenYear == time.year) && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static Date getDateWithoutTime(final Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
