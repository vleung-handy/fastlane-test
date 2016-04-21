package com.handy.portal.model.onboarding;

import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingsWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a view model that bridges the gap between a list of bookings vs a set of bookings
 * grouped by date. This is used in the onboarding work flow to claim initial jobs
 * <p/>
 * Created by jtse on 4/18/16.
 */
public class BookingsWrapperViewModel
{
    public List<BookingViewModel> mBookingViewModels;

    public String sanitizedDate;

    public BookingsWrapperViewModel(BookingsWrapper bookings)
    {
        mBookingViewModels = new ArrayList<>();

        for (Booking b : bookings.getBookings())
        {
            mBookingViewModels.add(new BookingViewModel(b));
        }

        sanitizedDate = bookings.getSanitizedDate();
    }

    public String getSanitizedDate()
    {
        return sanitizedDate;
    }
}
