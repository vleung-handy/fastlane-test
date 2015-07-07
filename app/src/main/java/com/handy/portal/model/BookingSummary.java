package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by cdavis on 5/4/15.
 */
public class BookingSummary
{
    @SerializedName("date")
    private Date date;
    @SerializedName("booking_summaries")
    private List<Booking> bookings;

    public final Date getDate()
    {
        return date;
    }

    public final List<Booking> getBookings()
    {
        return Collections.unmodifiableList(bookings);
    }
}
