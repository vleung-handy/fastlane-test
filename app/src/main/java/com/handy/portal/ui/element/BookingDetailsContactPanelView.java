package com.handy.portal.ui.element;

import android.os.Bundle;
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
public class BookingDetailsContactPanelView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_contact_call_button)
    protected Button callButton;

    @InjectView(R.id.booking_details_contact_text_button)
    protected Button textButton;

    @InjectView(R.id.booking_details_contact_profile_text)
    protected TextView profileText;

    public Button getCallButton()
    {
        return callButton;
    }

    public Button getTextButton()
    {
        return textButton;
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_contact;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        if(bookingStatus == Booking.BookingStatus.CLAIMED_WITHIN_DAY
                || bookingStatus == Booking.BookingStatus.CLAIMED_WITHIN_HOUR
                || bookingStatus == Booking.BookingStatus.CLAIMED_IN_PROGRESS)
        {
            Booking.User bookingUser = booking.getUser();
            profileText.setText(bookingUser.getAbbreviatedName());

            initButtonDisplayForStatus(callButton, bookingStatus, booking);
            initButtonDisplayForStatus(textButton, bookingStatus, booking);
        }
        else
        {
            //remove the section
            parentViewGroup.removeAllViews();
        }
    }

    private void initButtonDisplayForStatus(Button button, final BookingStatus bookingStatus, Booking booking)
    {
        switch(bookingStatus)
        {
            case CLAIMED_WITHIN_DAY:
            case CLAIMED_IN_PROGRESS:
            case CLAIMED_IN_PROGRESS_CHECKED_IN:
            {
                button.setEnabled(true);
            }
            break;

            default:
            {
                button.setEnabled(false);
            }
            break;
        }
    }
}
