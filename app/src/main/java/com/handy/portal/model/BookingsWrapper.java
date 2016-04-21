package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class BookingsWrapper
{
    @SerializedName("date")
    private Date date;

    @SerializedName("sanitized_date")
    private String sanitizedDate;

    @SerializedName("jobs")
    private List<Booking> bookings;

    public final Date getDate()
    {
        return date;
    }

    public String getSanitizedDate()
    {
        return sanitizedDate;
    }

    public final List<Booking> getBookings()
    {
        return bookings;
    }
}
