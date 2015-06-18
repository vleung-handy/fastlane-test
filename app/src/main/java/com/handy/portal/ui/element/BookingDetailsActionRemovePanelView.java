package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionRemovePanelView extends BookingDetailsActionPanelView
{
    @Override
    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        if(bookingStatus == BookingStatus.AVAILABLE || bookingStatus == BookingStatus.UNAVAILABLE)
        {
            removeView();
        }
        else
        {
            initButtonDisplayForStatus(actionButton, bookingStatus, booking);
            initDisclaimerText(booking, bookingStatus);
        }
    }

    @Override
    protected void initDisclaimerText(Booking booking, BookingStatus bookingStatus)
    {
        disclaimer1Text.setVisibility(View.GONE);
        disclaimer2Text.setVisibility(View.GONE);
    }

    @Override
    protected void initButtonDisplayForStatus(Button button, final BookingStatus bookingStatus, Booking booking)
    {
        button.setText(getDisplayTextForBookingStatus(bookingStatus, booking));
        button.setBackground(activity.getResources().getDrawable(R.drawable.button_red_round));
    }

    @Override
    protected String getDisplayTextForBookingStatus(BookingStatus bookingStatus, Booking booking)
    {
        return activity.getString(R.string.remove_job);
    }
}
