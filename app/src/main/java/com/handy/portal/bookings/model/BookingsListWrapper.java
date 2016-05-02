package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingsListWrapper
{
    @SerializedName("job_days")
    private List<BookingsWrapper> bookingsWrappers;

    public final List<BookingsWrapper> getBookingsWrappers()
    {
        return bookingsWrappers;
    }
}
