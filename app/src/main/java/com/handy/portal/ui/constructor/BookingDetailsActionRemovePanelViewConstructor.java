package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;

import java.util.List;

public class BookingDetailsActionRemovePanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    public BookingDetailsActionRemovePanelViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action_remove;
    }

    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.Action> allowedActions, BookingStatus bookingStatus)
    {
        return super.shouldRemoveSection(booking, allowedActions, bookingStatus) || (bookingStatus != BookingStatus.CLAIMED);
    }

    @Override
    protected ImmutableList<BookingActionButtonType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    private final ImmutableList<BookingActionButtonType> associatedButtonActionTypes =
            ImmutableList.of(
                    BookingActionButtonType.REMOVE
            );

}
