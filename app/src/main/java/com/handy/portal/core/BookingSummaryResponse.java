package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cdavis on 5/4/15.
 */
public class BookingSummaryResponse
{
    @SerializedName("available_bookings_days")
    private List<BookingSummary> bookingSummaries;

    public final List<BookingSummary> getBookingSummaries()
    {
        return bookingSummaries;
    }
}
