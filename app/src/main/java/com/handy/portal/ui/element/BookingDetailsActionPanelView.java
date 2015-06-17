package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_action_button)
    protected Button actionButton;

    @InjectView(R.id.booking_details_action_disclaimer_1_text)
    protected TextView disclaimer1Text;

    @InjectView(R.id.booking_details_action_disclaimer_2_text)
    protected TextView disclaimer2Text;

    //TODO: Support multiple action buttons in a panel, ex , "Check In" and "Update Customer on Arrival Time"

    public Button getActionButton()
    {
        return actionButton;
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        System.out.println("Action panel booking status : " + bookingStatus);

        initButtonDisplayForStatus(actionButton, bookingStatus, booking);
        initDisclaimerText(booking, bookingStatus);
    }

    private void initDisclaimerText(Booking booking, BookingStatus bookingStatus)
    {
        disclaimer1Text.setVisibility(View.INVISIBLE);
        disclaimer2Text.setVisibility(View.INVISIBLE);

        switch(bookingStatus)
        {
            case AVAILABLE:
            {
                //disclaimer turned off for next week
                /*
                if(booking.getFrequency() > 0)
                {
                    disclaimer1Text.setText(activity.getString(R.string.disclaimer_series));
                    disclaimer1Text.setVisibility(View.VISIBLE);
                }

                disclaimer2Text.setText(activity.getString(R.string.disclaimer_cancel));
                disclaimer2Text.setVisibility(View.VISIBLE);
                */
            }
            break;
            case CLAIMED:
            case CLAIMED_WITHIN_DAY:
            {
                disclaimer1Text.setText(activity.getString(R.string.disclaimer_on_my_way));
                disclaimer1Text.setVisibility(View.VISIBLE);
            }
            break;
        }
    }


    private void initButtonDisplayForStatus(Button button, final BookingStatus bookingStatus, Booking booking)
    {
        button.setText(getDisplayTextForBookingStatus(bookingStatus, booking));
        //TODO: more stuff like color and functionality changes

        //Color
        switch(bookingStatus)
        {
            case AVAILABLE:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_green_round));
            }
            break;
            case CLAIMED:
            case CLAIMED_WITHIN_DAY:
            case CLAIMED_PAST:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(false);
            }
            break;
            case CLAIMED_WITHIN_HOUR:
            case CLAIMED_IN_PROGRESS:
            case CLAIMED_IN_PROGRESS_CHECKED_IN:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(true);
            }
            break;
        }
    }

    private String getDisplayTextForBookingStatus(BookingStatus bookingStatus, Booking booking)
    {
        switch(bookingStatus)
        {
            case AVAILABLE:
            {
                if(booking.isRecurring())
                {
                    return activity.getString(R.string.claim_series);
                }
                return activity.getString(R.string.claim_job);
            }
            case CLAIMED:
            case CLAIMED_WITHIN_DAY:
            case CLAIMED_WITHIN_HOUR:
            {
                return activity.getString(R.string.on_my_way);
            }

            case CLAIMED_IN_PROGRESS:
            {
                return activity.getString(R.string.check_in);
            }

            case CLAIMED_IN_PROGRESS_CHECKED_IN:
            {
                return activity.getString(R.string.check_out);
            }

            case CLAIMED_PAST:
            {
                return activity.getString(R.string.check_out);
            }

        }
        return "";
    }
}
