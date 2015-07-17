package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
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

    protected void constructViewFromBooking(Booking booking, List<Booking.Action> allowedActions, Bundle arguments)
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

    protected boolean shouldRemoveSection(Booking booking, List<Booking.Action> allowedActions, BookingStatus bookingStatus)
    {
        return !hasAllowedAction(allowedActions);
    }

    protected boolean hasAllowedAction(List<Booking.Action> allowedActions)
    {
        boolean hasAnAction = false;
        for (Booking.Action action : allowedActions)
        {
            if (getAssociatedButtonActionTypes().contains(UIUtils.getAssociatedActionType(action)))
            {
                hasAnAction = true;
                break;
            }
        }
        return hasAnAction;
    }

    protected void initHelperText(List<Booking.Action> allowedActions)
    {
        String helperContent = "";
        for (Booking.Action action : allowedActions)
        {
            if (getAssociatedButtonActionTypes().contains(UIUtils.getAssociatedActionType(action)))
            {
                if (action.getHelperText() != null && !action.getHelperText().isEmpty())
                {
                    //allow accumulation of helper text, it will all display below the buttons instead of below each button
                    if (!helperContent.isEmpty())
                    {
                        helperContent += "\n";
                    }
                    helperContent += action.getHelperText();
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
                    BookingActionButtonType.HELP,
                    BookingActionButtonType.RETRACT_NO_SHOW
            );
}
