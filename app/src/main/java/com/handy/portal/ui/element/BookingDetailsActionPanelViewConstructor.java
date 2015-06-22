package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_action_disclaimer_1_text)
    protected TextView disclaimer1Text;

    @InjectView(R.id.booking_details_action_disclaimer_2_text)
    protected TextView disclaimer2Text;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    protected void constructViewFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        initDisclaimerText(booking, bookingStatus);
    }

    protected void initDisclaimerText(Booking booking, BookingStatus bookingStatus)
    {
        disclaimer1Text.setVisibility(View.GONE);
        disclaimer2Text.setVisibility(View.GONE);
    }
}
