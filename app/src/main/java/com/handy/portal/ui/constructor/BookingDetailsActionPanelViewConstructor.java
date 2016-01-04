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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.util.UIUtils;

import java.util.List;

import butterknife.Bind;

public class BookingDetailsActionPanelViewConstructor extends BookingDetailsViewConstructor
{
    @Bind(R.id.booking_details_action_text)
    protected TextView helperText;

    public BookingDetailsActionPanelViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        BookingStatus bookingStatus = (BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        boolean removeSection = shouldRemoveSection(booking, allowedActions, bookingStatus);
        if (removeSection)
        {
            container.setVisibility(View.GONE);
            return false;
        }
        else
        {
            initHelperText(allowedActions);
            return true;
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
                    BookingActionButtonType.HELP
            );
}
