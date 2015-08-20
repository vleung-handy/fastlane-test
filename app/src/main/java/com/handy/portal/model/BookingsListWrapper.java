package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingsListWrapper
{
    @SerializedName("bookings_days")
    private List<BookingsWrapper> bookingsWrappers;

    public final List<BookingsWrapper> getBookingsWrappers()
    {
        return bookingsWrappers;
    }
}
