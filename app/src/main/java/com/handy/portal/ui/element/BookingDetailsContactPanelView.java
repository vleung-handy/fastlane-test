package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;

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

    @InjectView(R.id.booking_details_contact_profile_image)
    protected ImageView profileImage;

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
        BookingDetailsFragment.BookingStatus bookingStatus = (BookingDetailsFragment.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        Booking.User bookingUser = booking.getUser();
        profileText.setText(bookingUser.getAbbreviatedName());

        //TODO: How will we populate this image?
        //profileImage

        initButtonDisplayForStatus(callButton, bookingStatus, booking);
        initButtonDisplayForStatus(textButton, bookingStatus, booking);
    }

    private void initButtonDisplayForStatus(Button button, final BookingDetailsFragment.BookingStatus bookingStatus, Booking booking)
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
