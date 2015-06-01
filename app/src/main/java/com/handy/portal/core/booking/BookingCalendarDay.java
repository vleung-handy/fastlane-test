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
        this.dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public BookingCalendarDay(Calendar calendar)
    {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    }

    int year;
    int month;
    int day;
    int dayOfYear;

    public int getDayOfYear() {return dayOfYear;}

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

        BookingCalendarDay other = (BookingCalendarDay) obj;

        if (other == this) {
            return true;
        }

        return other.year == this.year &&
                other.month == this.month &&
                other.day == this.day &&
                other.dayOfYear == this.dayOfYear;

    }

    @Override
    public String toString()
    {
        return (Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day));
    }
}
