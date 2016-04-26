package com.handy.portal.model;

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

    /**
     * Checks to see if there is at least one job. It's tricky, because there could be elements
     * without jobs, so we need to check specifically for the existence of a job
     *
     * @return
     */
    public boolean hasJobs()
    {
        if (bookingsWrappers != null && !bookingsWrappers.isEmpty())
        {
            //we need to check that it has actual jobs, and not just an empty list
            for (BookingsWrapper booking : bookingsWrappers)
            {
                if (booking.getBookings() != null && !booking.getBookings().isEmpty())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
