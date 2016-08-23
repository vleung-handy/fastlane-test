package com.handy.portal.onboarding.viewmodel;


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
    private String mSanitizedDate;

    public BookingsWrapperViewModel(BookingsWrapper bookings)
    {
        mBookingViewModels = new ArrayList<>();

        for (Booking b : bookings.getBookings())
        {
            mBookingViewModels.add(new BookingViewModel(b));
        }

        mSanitizedDate = bookings.getSanitizedDate();
    }

    public BookingsWrapperViewModel(final List<Booking> bookings,
                                    final boolean areBookingsSelectedByDefault)
    {
        mBookingViewModels = new ArrayList<>();

        for (Booking b : bookings)
        {
            final BookingViewModel bookingViewModel = new BookingViewModel(b);
            bookingViewModel.setSelected(areBookingsSelectedByDefault);
            mBookingViewModels.add(bookingViewModel);
        }
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
