package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.util.UIUtils;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelView extends BookingDetailsView
{
    public Button getActionButton()
    {
        Button actionButton = (Button) parentViewGroup.findViewById(R.id.booking_details_action_button);
        return actionButton;
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingDetailsFragment.BookingStatus bookingStatus = (BookingDetailsFragment.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        TextView locationText = (TextView) parentViewGroup.findViewById(R.id.booking_details_location_text);
        TextView frequencyText = (TextView) parentViewGroup.findViewById(R.id.booking_details_frequency_text);
        TextView paymentText = (TextView) parentViewGroup.findViewById(R.id.booking_details_payment_text);
        TextView paymentBonusText = (TextView) parentViewGroup.findViewById(R.id.booking_details_payment_bonus_text);

        locationText.setText(booking.getAddress().getShortRegion());

        UIUtils.setFrequencyInfo(booking, frequencyText, context);

        UIUtils.setPaymentInfo(paymentText, booking.getPaymentToProvider());
        UIUtils.setPaymentInfo(paymentBonusText, booking.getBonusPaymentToProvider());

        Button actionButton = (Button) parentViewGroup.findViewById(R.id.booking_details_action_button);

        initButtonDisplayForStatus(actionButton, bookingStatus);
    }

    private void initButtonDisplayForStatus(Button button, final BookingDetailsFragment.BookingStatus bookingStatus)
    {
        button.setText(getDisplayTextForBookingStatus(bookingStatus));
        //TODO: more stuff like color and functionality changes

        //Color
        switch(bookingStatus)
        {
            case AVAILABLE: { button.setBackgroundColor(context.getResources().getColor(R.color.handy_green)); } break;
            case CLAIMED: { button.setBackgroundColor(context.getResources().getColor(R.color.handy_purple)); } break;
        }
    }

    private String getDisplayTextForBookingStatus(BookingDetailsFragment.BookingStatus bookingStatus)
    {
        switch(bookingStatus)
        {
            case AVAILABLE: { return context.getString(R.string.claim); }
            case CLAIMED: { return context.getString(R.string.on_my_way); }
        }
        return "";
    }
}
