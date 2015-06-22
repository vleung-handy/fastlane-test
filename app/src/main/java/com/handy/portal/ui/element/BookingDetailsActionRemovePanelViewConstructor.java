package com.handy.portal.ui.element;

import android.os.Bundle;

import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionRemovePanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @Override
    protected void constructViewFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        if(bookingStatus != BookingStatus.CLAIMED)
        {
            removeView();
        }
    }
}
