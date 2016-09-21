package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BookingsListWrapper
{
    @SerializedName("job_days")
    private ArrayList<BookingsWrapper> mBookingsWrappers;
    @SerializedName("message")
    private String mMessage;

    public ArrayList<BookingsWrapper> getBookingsWrappers()
    {
        return mBookingsWrappers;
    }

    /**
     * Checks to see if there is at least one job. It's tricky, because there could be elements
     * without jobs, so we need to check specifically for the existence of a job.
     */
    public boolean hasBookings()
    {
        if (mBookingsWrappers != null && !mBookingsWrappers.isEmpty())
        {
            // we need to check that it has actual jobs, and not just an empty list
            for (BookingsWrapper booking : mBookingsWrappers)
            {
                if (booking.getBookings() != null && !booking.getBookings().isEmpty())
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMessage()
    {
        return mMessage;
    }
}
