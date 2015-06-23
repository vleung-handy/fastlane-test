package com.handy.portal.ui.element;

import com.google.common.collect.ImmutableList;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.List;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionRemovePanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.ActionButtonData> allowedActions, BookingStatus bookingStatus)
    {
        boolean removeSection = false;
        removeSection = super.shouldRemoveSection(booking, allowedActions, bookingStatus) || (bookingStatus != BookingStatus.CLAIMED);
        return removeSection;
    }

    @Override
    protected ImmutableList<Booking.ButtonActionType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    private final ImmutableList<Booking.ButtonActionType> associatedButtonActionTypes =
            ImmutableList.of(
                    Booking.ButtonActionType.REMOVE
            );

}
