package com.handy.portal.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.handy.portal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateTimeUtils
{
    //TODO: refactor code throughout the app to put date formats here
    //TODO: rename these fields & methods to something better

    public final static SimpleDateFormat CLOCK_FORMATTER_12HR =
            new SimpleDateFormat("h:mm a", Locale.getDefault());
    public final static SimpleDateFormat DAY_OF_WEEK_MONTH_DAY_FORMATTER =
            new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
    public final static SimpleDateFormat MONTH_SHORT_NAME_FORMATTER =
            new SimpleDateFormat("MMM", Locale.getDefault());
    public final static SimpleDateFormat SUMMARY_DATE_FORMATTER =
            new SimpleDateFormat("MMM d", Locale.getDefault());
    public final static SimpleDateFormat DETAILED_DATE_FORMATTER =
            new SimpleDateFormat("EEEE, MMMM d 'at' h:mm a", Locale.getDefault());
    public final static SimpleDateFormat MONTH_DATE_FORMATTER =
            new SimpleDateFormat("MMMM d", Locale.getDefault());
    public final static SimpleDateFormat MONTH_DATE_YEAR_FORMATTER =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    public final static SimpleDateFormat DAY_OF_WEEK_MONTH_DATE_YEAR_FORMATTER =
            new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
    public final static SimpleDateFormat YEAR_FORMATTER = new SimpleDateFormat("yyyy", Locale.getDefault());
    public final static SimpleDateFormat MONTH_YEAR_FORMATTER =
            new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    public final static SimpleDateFormat ISO8601_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    public final static SimpleDateFormat LOCAL_TIME_12_HOURS =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public final static SimpleDateFormat NUMERIC_YEAR_MONTH_DATE_FORMATTER
            = new SimpleDateFormat("yyyyMMdd");

    public final static int HOURS_IN_DAY = 24;
    public final static int HOURS_IN_SIX_DAYS = HOURS_IN_DAY * 6;
    public final static String UTC_TIMEZONE = "UTC";

    public static boolean isDateWithinXHoursFromNow(Date date, int hours)
    {
        long currentTime = DateTimeUtils.getDateWithoutTime(new Date()).getTime();
        long dateOfBookingsTime = date.getTime();
        long dateDifference = dateOfBookingsTime - currentTime;
        return dateDifference <= DateUtils.HOUR_IN_MILLIS * hours;
    }

    public static boolean isTimeWithinXMillisecondsFromNow(Date date, long milliSec)
    {
        long timeDifference = date.getTime() - System.currentTimeMillis();
        return timeDifference >= 0 && timeDifference <= milliSec;
    }

    @Nullable
    public static String getMonthShortName(Date date)
    {
        if (date == null) { return null; }
        return getMonthShortNameFormatter().format(date);
    }

    @Nullable
    public static String getYear(Date date)
    {
        if (date == null) { return null; }
        return getYearFormatter().format(date);
    }

    @Nullable
    public static String getMonthAndYear(Date date)
    {
        if (date == null) { return null; }
        return getMonthYearFormatter().format(date);
    }

    @Nullable
    public static Integer getYearInt(Date date)
    {
        if (date == null) { return null; }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    public static int getDayOfMonth(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    @Nullable
    public static String formatDateTo12HourClock(Date date)
    {
        if (date == null) { return null; }
        return getClockFormatter12hr().format(date);
    }

    @Nullable
    public static String formatDateDayOfWeekMonthDay(Date date)
    {
        if (date == null) { return null; }
        return getDayOfWeekMonthDayFormatter().format(date);
    }

    @Nullable
    public static String formatDateMonthDay(Date date)
    {
        if (date == null) { return null; }
        return getSummaryDateFormatter().format(date);
    }

    @Nullable
    public static String formatDetailedDate(Date date)
    {
        if (date == null) { return null; }
        return getDetailedDateFormatter().format(date);
    }

    @Nullable
    public static String formatMonthDateYear(Date date)
    {
        if (date == null) { return null; }
        return getMonthDateYearFormatter().format(date);
    }

    @Nullable
    public static String formatDayOfWeekMonthDateYear(Date date)
    {
        if (date == null) { return null; }
        return getDayOfWeekMonthDateYearFormatter().format(date);
    }

    @Nullable
    public static String formatIso8601(Date date)
    {
        if (date == null) { return null; }
        return getIso8601Formatter().format(date);
    }

    @Nullable
    public static String formatDateRange(SimpleDateFormat dateFormat, Date start, Date end)
    {
        if (start == null || end == null) { return null; }
        return dateFormat.format(start) + " – " + dateFormat.format(end);
    }

    public static boolean equalCalendarDates(final Date date1, final Date date2)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date1);

        final int thenYear = c.get(Calendar.YEAR);
        final int thenMonth = c.get(Calendar.MONTH);
        final int thenMonthDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(date2);

        return (thenYear == c.get(Calendar.YEAR)) && (thenMonth == c.get(Calendar.MONTH))
                && (thenMonthDay == c.get(Calendar.DAY_OF_MONTH));
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

    public static boolean isToday(Date date)
    {
        Calendar today = Calendar.getInstance();
        today.setTime(getBeginningOfDay(new Date()));

        Calendar dayToCompare = Calendar.getInstance();
        dayToCompare.setTime(getBeginningOfDay(date));

        return daysBetween(today.getTime(), dayToCompare.getTime()) == 0;
    }

    // return a string in hh:mm:ss format
    public static String millisecondsToFormattedString(long millis)
    {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getTimeWithoutDate(final Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getLocalTime12HoursFormatter().format(cal.getTime());
    }

    public static CountDownTimer setCountDownTimer(final TextView textView, long timeRemainMillis)
    {
        return new CountDownTimer(timeRemainMillis, DateUtils.SECOND_IN_MILLIS)
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

    public static CountDownTimer setActionBarCountdownTimer(
            final Context context, final ActionBar actionBar, long timeRemainMillis, final int stringId)
    {
        return new CountDownTimer(timeRemainMillis, DateUtils.SECOND_IN_MILLIS)
        {
            @Override
            public void onTick(final long millisUntilFinished)
            {
                actionBar.setTitle(context.getString(stringId, DateTimeUtils.millisecondsToFormattedString(millisUntilFinished)));
            }

            @Override
            public void onFinish() { }
        }.start();
    }

    public static String dayDifferenceInWords(final Date date)
    {
        Calendar today = Calendar.getInstance();
        today.setTime(getBeginningOfDay(new Date()));

        Calendar dayToCompare = Calendar.getInstance();
        dayToCompare.setTime(getBeginningOfDay(date));

        int daysBetween = daysBetween(today.getTime(), dayToCompare.getTime());

        String dayDifferenceInWords;

        if (daysBetween == 0)
        {
            dayDifferenceInWords = "Today";
        }
        else if (daysBetween == 1)
        {
            dayDifferenceInWords = "Tomorrow";
        }
        else if (daysBetween == -1)
        {
            dayDifferenceInWords = "Yesterday";
        }
        else if (daysBetween > 1)
        {
            dayDifferenceInWords = String.valueOf(Math.abs(daysBetween)) + " days from now";
        }
        else
        {
            dayDifferenceInWords = String.valueOf(Math.abs(daysBetween)) + " days ago";
        }

        return dayDifferenceInWords;
    }

    public static int daysBetween(Date d1, Date d2)
    {
        return Math.round((d2.getTime() - d1.getTime()) / (float) (DateUtils.HOUR_IN_MILLIS * HOURS_IN_DAY));
    }

    private static SimpleDateFormat getClockFormatter12hr()
    {
        CLOCK_FORMATTER_12HR.setTimeZone(TimeZone.getDefault());
        return CLOCK_FORMATTER_12HR;
    }

    private static SimpleDateFormat getDayOfWeekMonthDayFormatter()
    {
        DAY_OF_WEEK_MONTH_DAY_FORMATTER.setTimeZone(TimeZone.getDefault());
        return DAY_OF_WEEK_MONTH_DAY_FORMATTER;
    }

    private static SimpleDateFormat getMonthShortNameFormatter()
    {
        MONTH_SHORT_NAME_FORMATTER.setTimeZone(TimeZone.getDefault());
        return MONTH_SHORT_NAME_FORMATTER;
    }

    private static SimpleDateFormat getSummaryDateFormatter()
    {
        SUMMARY_DATE_FORMATTER.setTimeZone(TimeZone.getDefault());
        return SUMMARY_DATE_FORMATTER;
    }

    private static SimpleDateFormat getDetailedDateFormatter()
    {
        DETAILED_DATE_FORMATTER.setTimeZone(TimeZone.getDefault());
        return DETAILED_DATE_FORMATTER;
    }

    private static SimpleDateFormat getMonthDateFormatter()
    {
        MONTH_DATE_FORMATTER.setTimeZone(TimeZone.getDefault());
        return MONTH_DATE_FORMATTER;
    }

    private static SimpleDateFormat getMonthDateYearFormatter()
    {
        MONTH_DATE_YEAR_FORMATTER.setTimeZone(TimeZone.getDefault());
        return MONTH_DATE_YEAR_FORMATTER;
    }

    private static SimpleDateFormat getDayOfWeekMonthDateYearFormatter()
    {
        DAY_OF_WEEK_MONTH_DATE_YEAR_FORMATTER.setTimeZone(TimeZone.getDefault());
        return DAY_OF_WEEK_MONTH_DATE_YEAR_FORMATTER;
    }

    private static SimpleDateFormat getYearFormatter()
    {
        YEAR_FORMATTER.setTimeZone(TimeZone.getDefault());
        return YEAR_FORMATTER;
    }

    private static SimpleDateFormat getMonthYearFormatter()
    {
        MONTH_YEAR_FORMATTER.setTimeZone(TimeZone.getDefault());
        return MONTH_YEAR_FORMATTER;
    }

    private static SimpleDateFormat getIso8601Formatter()
    {
        ISO8601_FORMATTER.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
        return ISO8601_FORMATTER;
    }

    private static SimpleDateFormat getLocalTime12HoursFormatter()
    {
        LOCAL_TIME_12_HOURS.setTimeZone(TimeZone.getDefault());
        return LOCAL_TIME_12_HOURS;
    }

}
