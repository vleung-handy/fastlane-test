package com.handy.portal.core.booking;

import java.util.Calendar;
import java.util.Date;

//Comparing Dates is error prone, this reclassifies based on a given day
public class BookingCalendarDay implements Comparable<BookingCalendarDay>
{
    public BookingCalendarDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public final int year;
    public final int month;
    public final int day;

    @Override
    public int hashCode()
    {
        //TODO: use hashbuilder and make this a good hash
        return new Integer(year * 100 + month * 100 + day * 1).hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BookingCalendarDay))
        {
            return false;
        }

        BookingCalendarDay other = (BookingCalendarDay) obj;

        if (other == this)
        {
            return true;
        }

        return other.year == this.year &&
                other.month == this.month &&
                other.day == this.day;

    }

    @Override
    public String toString()
    {
        return (Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day));
    }

    @Override
    public int compareTo(BookingCalendarDay other)
    {
        Calendar calendar = this.toCalendar();
        Calendar otherCalendar = other.toCalendar();
        return calendar.compareTo(otherCalendar);
    }

    public Calendar toCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.year, this.month, this.day);
        return calendar;
    }
}
