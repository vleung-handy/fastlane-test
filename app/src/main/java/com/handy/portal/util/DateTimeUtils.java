package com.handy.portal.util;

import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;

import com.handy.portal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateTimeUtils
{
    //TODO: refactor code throughout the app to put date formats here
    //TODO: rename these fields & methods to something better
    public final static SimpleDateFormat CLOCK_FORMATTER_12HR = new SimpleDateFormat("h:mm a");
    public final static SimpleDateFormat DAY_OF_WEEK_MONTH_DAY_FORMATTER = new SimpleDateFormat("EEE, MMM d");
    public final static SimpleDateFormat MONTH_SHORT_NAME_FORMATTER = new SimpleDateFormat("MMM");
    public final static SimpleDateFormat SUMMARY_DATE_FORMATTER = new SimpleDateFormat("MMM d");
    public final static SimpleDateFormat DETAILED_DATE_FORMATTER = new SimpleDateFormat("EEEE, MMMM d 'at' h:mm a");
    public final static SimpleDateFormat MONTH_DATE_YEAR_DATE_FORMATTER = new SimpleDateFormat("MMMM d, yyyy");

    public final static int HOURS_IN_DAY = 24;
    public final static int DAYS_IN_WEEK = 7;
    public final static int HOURS_IN_SIX_DAYS = HOURS_IN_DAY * 6;
    public final static int HOURS_IN_WEEK = HOURS_IN_DAY * DAYS_IN_WEEK;
    public final static int MILLISECONDS_IN_MINUTE = 60000;
    public final static int MILLISECONDS_IN_SECOND = 1000;
    public final static long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * 60;
    public final static long MILLISECONDS_IN_30_MINS = MILLISECONDS_IN_MINUTE * 30;
    public final static long MILLISECONDS_IN_52_MINS = MILLISECONDS_IN_MINUTE * 52;

    public static boolean isDateWithinXHoursFromNow(Date date, int hours)
    {
        long currentTime = DateTimeUtils.getDateWithoutTime(new Date()).getTime();
        long dateOfBookingsTime = date.getTime();
        long dateDifference = dateOfBookingsTime - currentTime;
        return dateDifference <= DateTimeUtils.MILLISECONDS_IN_HOUR * hours;
    }

    public static String getMonthShortName(Date date)
    {
        if (date == null) { return null; }
        return MONTH_SHORT_NAME_FORMATTER.format(date);

    }

    public static int getDayOfMonth(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static String formatDateTo12HourClock(Date date)
    {
        if (date == null) { return null; }
        return CLOCK_FORMATTER_12HR.format(date);
    }

    public static String formatDateDayOfWeekMonthDay(Date date)
    {
        if (date == null) { return null; }
        return DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(date);
    }

    public static String formatDateMonthDay(Date date)
    {
        if (date == null) { return null; }
        return SUMMARY_DATE_FORMATTER.format(date);
    }

    public static String formatDetailedDate(Date date)
    {
        if (date == null) { return null; }
        return DETAILED_DATE_FORMATTER.format(date);
    }

    public static String formatMonthDateYear(Date date)
    {
        if (date == null) { return null; }
        return MONTH_DATE_YEAR_DATE_FORMATTER.format(date);
    }

    public static String formatDateRange(SimpleDateFormat dateFormat, Date start, Date end)
    {
        if (start == null || end == null) { return null; }
        return dateFormat.format(start) + " – " + dateFormat.format(end);
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

    public static Date getBeginningOfDay(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static boolean isStartOfYear(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY) == 0
                && c.get(Calendar.MINUTE) == 0
                && c.get(Calendar.SECOND) == 0
                && c.get(Calendar.MILLISECOND) == 0
                && c.get(Calendar.DAY_OF_YEAR) == 1;
    }

    // return a string in hh:mm:ss format
    public static String millisecondsToFormattedString(long millis)
    {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static CountDownTimer setCountDownTimer(final TextView textView, long timeRemainMillis)
    {
        return new CountDownTimer(timeRemainMillis, DateTimeUtils.MILLISECONDS_IN_SECOND)
        {
            @Override
            public void onTick(final long millisUntilFinished)
            {
                textView.setText(textView.getContext().getString(R.string.start_timer_formatted,
                        DateTimeUtils.millisecondsToFormattedString(millisUntilFinished)));
            }

            @Override
            public void onFinish() { }
        }.start();
    }
}
