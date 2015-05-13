package com.handy.portal.ui.element;

import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsActionPanelView extends BookingDetailsView
{
    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_action;
    }

    protected void initFromBooking(Booking booking)
    {
        TextView locationText = (TextView) parentViewGroup.findViewById(R.id.booking_details_location_text);
        TextView frequencyText = (TextView) parentViewGroup.findViewById(R.id.booking_details_frequency_text);
        TextView paymentText = (TextView) parentViewGroup.findViewById(R.id.booking_details_payment_text);
        TextView paymentBonusText = (TextView) parentViewGroup.findViewById(R.id.booking_details_payment_bonus_text);

        locationText.setText(booking.getAddress().getShortRegion());
        frequencyText.setText(Integer.toString(booking.getFrequency()));

        if(booking.getPaymentToProvider() != null)
        {
            Booking.MonetaryAmount money = booking.getPaymentToProvider().getAmount();
            if(money != null)
            {
                paymentText.setText(money.getCurrencySymbol() + Integer.toString(money.getAmount()));
            }
        }

        //generate the appropriate button based on the status and provider status of the booking
        //Button actionButton = generateActionButton(booking);


    }

    private Button generateActionButton(Booking booking)
    {
        String bookingStatus = booking.getStatus();
        return null;
    }
}
