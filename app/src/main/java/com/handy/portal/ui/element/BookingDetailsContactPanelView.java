package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsContactPanelView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_contact_profile_text)
    protected TextView profileText;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_contact;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        if(bookingStatus != BookingStatus.CLAIMED)
        {
            removeView();
        }
        else
        {
            Booking.User bookingUser = booking.getUser();
            profileText.setText(bookingUser.getAbbreviatedName());
        }
    }
}
