package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionContactPanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @InjectView(R.id.booking_details_contact_profile_text)
    protected TextView profileText;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_contact;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
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
            initHelperText(booking, allowedActions, bookingStatus);
        }
    }

    @Override
    protected ImmutableList<Booking.ButtonActionType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    protected final ImmutableList<Booking.ButtonActionType> associatedButtonActionTypes =
            ImmutableList.of(Booking.ButtonActionType.CONTACT_PHONE, Booking.ButtonActionType.CONTACT_TEXT);
}
