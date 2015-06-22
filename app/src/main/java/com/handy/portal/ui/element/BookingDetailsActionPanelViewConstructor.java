package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
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
        initHelperText(booking, allowedActions, bookingStatus);
    }

    protected void initHelperText(Booking booking, List<Booking.ActionButtonData> allowedActions, BookingStatus bookingStatus)
    {
        String helperContent = "";
        for (Booking.ActionButtonData actionButtonData : allowedActions)
        {
            if(getAssociatedButtonActionTypes().contains(actionButtonData.getAssociatedActionType()))
            {
                if(actionButtonData.getHelperText() != null && !actionButtonData.getHelperText().isEmpty())
                {
                    //allow accumulation of helper text, it will all display below the buttons instead of below each button
                    if(!helperContent.isEmpty())
                    {
                        helperContent += "\n";
                    }
                    helperContent += actionButtonData.getHelperText();
                }
            }
        }

        if(!helperContent.isEmpty())
        {
            helperText.setVisibility(View.VISIBLE);
            helperText.setText(helperContent);
        }
        else
        {
            helperText.setVisibility(View.GONE);
        }
    }

    protected  ImmutableList<Booking.ButtonActionType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }

    private final ImmutableList<Booking.ButtonActionType> associatedButtonActionTypes =
            ImmutableList.of(Booking.ButtonActionType.CLAIM, Booking.ButtonActionType.ON_MY_WAY, Booking.ButtonActionType.CHECK_IN, Booking.ButtonActionType.ETA);
}
