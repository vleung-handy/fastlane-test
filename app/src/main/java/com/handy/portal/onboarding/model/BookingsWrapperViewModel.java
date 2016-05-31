package com.handy.portal.onboarding.model;


import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a view model that bridges the gap between a list of bookings vs a set of bookings
 * grouped by date. This is used in the onboarding work flow to claim initial jobs
 * <p/>
 */
public class BookingsWrapperViewModel
{
    private final List<BookingViewModel> mBookingViewModels;
    private final String mSanitizedDate;

    public BookingsWrapperViewModel(BookingsWrapper bookings)
    {
        mBookingViewModels = new ArrayList<>();

        for (Booking b : bookings.getBookings())
        {
            mBookingViewModels.add(new BookingViewModel(b));
        }

        mSanitizedDate = bookings.getSanitizedDate();
    }

    public List<BookingViewModel> getBookingViewModels()
    {
        return mBookingViewModels;
    }

    public String getSanitizedDate()
    {
        return mSanitizedDate;
    }
}
