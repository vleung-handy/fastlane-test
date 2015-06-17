package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_action_button)
    protected Button actionButton;

    @InjectView(R.id.booking_details_action_disclaimer_series_text)
    protected TextView disclaimerSeriesText;

    @InjectView(R.id.booking_details_action_disclaimer_cancel_text)
    protected TextView disclaimerCancelText;

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
        BookingDetailsFragment.BookingStatus bookingStatus = (BookingDetailsFragment.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        initButtonDisplayForStatus(actionButton, bookingStatus, booking);

        //disclaimer turned off for next week
        //disclaimerSeriesText.setVisibility(booking.getFrequency() > 0 ? View.VISIBLE : View.GONE);
        disclaimerSeriesText.setVisibility(View.INVISIBLE);
        disclaimerCancelText.setVisibility(View.INVISIBLE);
    }

    private void initButtonDisplayForStatus(Button button, final BookingDetailsFragment.BookingStatus bookingStatus, Booking booking)
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
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(false);
            }
            break;
            case CLAIMED_WITHIN_HOUR:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(true);
            }
            break;
            case CLAIMED_IN_PROGRESS:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(true);
            }
            break;
            case CLAIMED_IN_PROGRESS_CHECKED_IN:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(false);
            }
            break;
            case CLAIMED_PAST:
            {
                button.setBackground(activity.getResources().getDrawable(R.drawable.button_purple_round));
                //on my way is only available/active starting 1 hour before booking
                button.setEnabled(false);
            }
            break;
        }
    }

    private String getDisplayTextForBookingStatus(BookingDetailsFragment.BookingStatus bookingStatus, Booking booking)
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
            {
                return activity.getString(R.string.on_my_way);
            }
        }
        return "";
    }
}
