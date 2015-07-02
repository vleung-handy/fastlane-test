package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.consts.BookingActionButtonType;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;
import com.handy.portal.util.UIUtils;

import java.util.List;

import butterknife.InjectView;

public class BookingDetailsActionPanelViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_action_text)
    protected TextView helperText;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        boolean removeSection = shouldRemoveSection(booking, allowedActions, bookingStatus);
        if (removeSection)
        {
            removeView();
        }
        else
        {
            initHelperText(allowedActions);
        }
    }

    protected boolean shouldRemoveSection(Booking booking, List<Booking.ActionButtonData> allowedActions, BookingStatus bookingStatus)
    {
        return !hasAllowedAction(allowedActions);
    }

    protected boolean hasAllowedAction(List<Booking.ActionButtonData> allowedActions)
    {
        boolean hasAnAction = false;
        for (Booking.ActionButtonData actionButtonData : allowedActions)
        {
            if (getAssociatedButtonActionTypes().contains(UIUtils.getAssociatedActionType(actionButtonData)))
            {
                hasAnAction = true;
                break;
            }
        }
        return hasAnAction;
    }

    protected void initHelperText(List<Booking.ActionButtonData> allowedActions)
    {
        String helperContent = "";
        for (Booking.ActionButtonData actionButtonData : allowedActions)
        {
            if (getAssociatedButtonActionTypes().contains(UIUtils.getAssociatedActionType(actionButtonData)))
            {
                if (actionButtonData.getHelperText() != null && !actionButtonData.getHelperText().isEmpty())
                {
                    //allow accumulation of helper text, it will all display below the buttons instead of below each button
                    if (!helperContent.isEmpty())
                    {
                        helperContent += "\n";
                    }
                    helperContent += actionButtonData.getHelperText();
                }
            }
        }

        if (!helperContent.isEmpty())
        {
            helperText.setVisibility(View.VISIBLE);
            helperText.setText(helperContent);
        }
        else
        {
            helperText.setVisibility(View.GONE);
        }
    }

    protected ImmutableList<BookingActionButtonType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    private final ImmutableList<BookingActionButtonType> associatedButtonActionTypes =
            ImmutableList.of(
                    BookingActionButtonType.CLAIM,
                    BookingActionButtonType.ON_MY_WAY,
                    BookingActionButtonType.CHECK_IN,
                    BookingActionButtonType.CHECK_OUT,
                    BookingActionButtonType.ETA
            );
}
