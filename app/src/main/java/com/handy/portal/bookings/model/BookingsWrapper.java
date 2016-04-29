package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class BookingsWrapper
{
    @SerializedName("date")
    private Date date;
    @SerializedName("jobs")
    private List<Booking> bookings;

    public final Date getDate()
    {
        return date;
    }

    public final List<Booking> getBookings()
    {
        return bookings;
    }
}
