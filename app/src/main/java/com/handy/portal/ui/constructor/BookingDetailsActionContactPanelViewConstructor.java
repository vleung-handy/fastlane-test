package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;

import java.util.List;

import butterknife.Bind;

public class BookingDetailsActionContactPanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @Bind(R.id.booking_details_contact_profile_text)
    protected TextView profileText;

    public BookingDetailsActionContactPanelViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_contact;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        boolean actionPanelExists = super.constructView(container, booking);
        if (actionPanelExists)
        {
            Booking.User bookingUser = booking.getUser();
            profileText.setText(bookingUser.getFullName());
            return true;
        }
        else
        {
            container.setVisibility(View.GONE);
            return false;
        }
    }

    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.Action> allowedActions, Booking.BookingStatus bookingStatus)
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
