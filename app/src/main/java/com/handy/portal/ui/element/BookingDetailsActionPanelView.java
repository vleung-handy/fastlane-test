package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.util.UIUtils;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_location_text)
    protected TextView locationText;

    @InjectView(R.id.booking_details_frequency_text)
    protected TextView frequencyText;

    @InjectView(R.id.booking_details_payment_text)
    protected TextView paymentText;

    @InjectView(R.id.booking_details_payment_bonus_text)
    protected TextView paymentBonusText;

    @InjectView(R.id.booking_details_action_button)
    protected Button actionButton;

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

        locationText.setText(booking.getAddress().getShortRegion());

        UIUtils.setFrequencyInfo(booking, frequencyText, activity);

        UIUtils.setPaymentInfo(paymentText, booking.getPaymentToProvider());
        UIUtils.setPaymentInfo(paymentBonusText, booking.getBonusPaymentToProvider());

        initButtonDisplayForStatus(actionButton, bookingStatus);
    }

    private void initButtonDisplayForStatus(Button button, final BookingDetailsFragment.BookingStatus bookingStatus)
    {
        button.setText(getDisplayTextForBookingStatus(bookingStatus));
        //TODO: more stuff like color and functionality changes

        //Color
        switch(bookingStatus)
        {
            case AVAILABLE: { button.setBackgroundColor(activity.getResources().getColor(R.color.handy_green)); } break;
            case CLAIMED: { button.setBackgroundColor(activity.getResources().getColor(R.color.handy_purple)); } break;
        }
    }

    private String getDisplayTextForBookingStatus(BookingDetailsFragment.BookingStatus bookingStatus)
    {
        switch(bookingStatus)
        {
            case AVAILABLE: { return activity.getString(R.string.claim); }
            case CLAIMED: { return activity.getString(R.string.on_my_way); }
        }
        return "";
    }
}
