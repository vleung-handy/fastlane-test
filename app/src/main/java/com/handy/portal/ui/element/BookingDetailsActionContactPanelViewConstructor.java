package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.consts.BookingActionButtonType;
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
        profileText.setText(bookingUser.getFullName());
    }

    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.ActionButtonData> allowedActions, Booking.BookingStatus bookingStatus)
    {
        return super.shouldRemoveSection(booking, allowedActions, bookingStatus) || (bookingStatus != Booking.BookingStatus.CLAIMED);
    }

    @Override
    protected ImmutableList<BookingActionButtonType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    protected final ImmutableList<BookingActionButtonType> associatedButtonActionTypes =
            ImmutableList.of(
                    BookingActionButtonType.CONTACT_PHONE,
                    BookingActionButtonType.CONTACT_TEXT
            );
}
