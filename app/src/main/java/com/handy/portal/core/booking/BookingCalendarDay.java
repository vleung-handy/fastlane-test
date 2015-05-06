package com.handy.portal.core.booking;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by cdavis on 5/6/15.
 */
//Comparing Dates is error prone, this reclassifies based on a given day
public class BookingCalendarDay
{
    public BookingCalendarDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public BookingCalendarDay(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public BookingCalendarDay(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    int year;
    int month;
    int day;

    @Override
    public int hashCode()
    {
        //TODO: use hashbuilder and make this a good hash
        return new Integer(year * 100 + month * 100 + day * 1).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BookingCalendarDay)) {
            return false;
        }

        BookingCalendarDay compare = (BookingCalendarDay) obj;

        if (obj == this) {
            return true;
        }

        if(compare.year == this.year &&
                compare.month == this.month &&
                compare.day == this.day)
        {
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return (Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day));
    }
}
