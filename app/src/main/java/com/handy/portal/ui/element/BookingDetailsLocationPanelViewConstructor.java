package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.PartnerNames;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;
import com.handy.portal.util.UIUtils;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsLocationPanelViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_location_text)
    protected TextView locationText;

    @InjectView(R.id.booking_details_frequency_text)
    protected TextView frequencyText;

    @InjectView(R.id.booking_details_payment_text)
    protected TextView paymentText;

    @InjectView(R.id.booking_details_payment_bonus_text)
    protected TextView paymentBonusText;

    @InjectView(R.id.booking_details_partner_text)
    protected TextView partnerText;

    @InjectView(R.id.booking_details_requested_indicator_layout)
    protected LinearLayout requestedLayout;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_location;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        if(bookingStatus == BookingStatus.AVAILABLE)
        {
            locationText.setText(booking.getAddress().getShortRegion() + "\n" + booking.getAddress().getZip());
        }
        else
        {
            locationText.setText(booking.getAddress().getStreetAddress() + "\n" + booking.getAddress().getZip());
        }

        UIUtils.setFrequencyInfo(booking, frequencyText, activity);

        UIUtils.setPaymentInfo(paymentText, booking.getPaymentToProvider(), activity.getString(R.string.payment_value));
        UIUtils.setPaymentInfo(paymentBonusText, booking.getBonusPaymentToProvider(), activity.getString(R.string.bonus_payment_value));

        //Partner takes priority over requested
        if(booking.getPartner() != null)
        {
            partnerText.setVisibility(booking.getPartner().equalsIgnoreCase(PartnerNames.AIRBNB) ? View.VISIBLE : View.GONE);
            requestedLayout.setVisibility(View.GONE);
        }
        else if(booking.getIsRequested())
        {
            partnerText.setVisibility(View.GONE);
            requestedLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            partnerText.setVisibility(View.GONE);
            requestedLayout.setVisibility(View.GONE);
        }

    }
}
