package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

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

    @Override
    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        super.constructViewFromBooking(booking, allowedActions, arguments);
        Booking.User bookingUser = booking.getUser();
        profileText.setText(bookingUser.getAbbreviatedName());
    }

    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.ActionButtonData> allowedActions, Booking.BookingStatus bookingStatus)
    {
        return super.shouldRemoveSection(booking, allowedActions, bookingStatus) || (bookingStatus != Booking.BookingStatus.CLAIMED);
    }

    @Override
    protected ImmutableList<Booking.ButtonActionType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    protected final ImmutableList<Booking.ButtonActionType> associatedButtonActionTypes =
            ImmutableList.of(
                    Booking.ButtonActionType.CONTACT_PHONE,
                    Booking.ButtonActionType.CONTACT_TEXT
            );
}
