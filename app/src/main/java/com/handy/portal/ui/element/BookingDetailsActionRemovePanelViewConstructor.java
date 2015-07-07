package com.handy.portal.ui.element;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;

import java.util.List;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionRemovePanelViewConstructor extends BookingDetailsActionPanelViewConstructor
{
    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action_remove;
    }

    @Override
    protected boolean shouldRemoveSection(Booking booking, List<Booking.ActionButtonData> allowedActions, BookingStatus bookingStatus)
    {
        boolean removeSection = false;
        removeSection = super.shouldRemoveSection(booking, allowedActions, bookingStatus) || (bookingStatus != BookingStatus.CLAIMED);
        return removeSection;
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
