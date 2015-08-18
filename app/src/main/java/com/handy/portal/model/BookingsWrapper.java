package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingsWrapper
{
    @SerializedName("bookings")
    private List<Booking> bookings;

    public final List<Booking> getBookings()
    {
        return bookings;
    }
}
