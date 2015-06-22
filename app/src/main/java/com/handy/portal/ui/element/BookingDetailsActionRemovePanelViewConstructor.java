package com.handy.portal.ui.element;

import android.os.Bundle;

import com.google.common.collect.ImmutableList;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.List;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionRemovePanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @Override
    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        if(bookingStatus != BookingStatus.CLAIMED)
        {
            removeView();
        }
        else
        {
            initHelperText(booking, allowedActions, bookingStatus);
        }
    }

    @Override
    protected ImmutableList<Booking.ButtonActionType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    private final ImmutableList<Booking.ButtonActionType> associatedButtonActionTypes =
            ImmutableList.of(Booking.ButtonActionType.REMOVE);

}
