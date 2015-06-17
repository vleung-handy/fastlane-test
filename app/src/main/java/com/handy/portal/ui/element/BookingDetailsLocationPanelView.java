package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.PartnerNames;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.util.UIUtils;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsLocationPanelView extends BookingDetailsView
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

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_location;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingDetailsFragment.BookingStatus bookingStatus = (BookingDetailsFragment.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);

        locationText.setText(booking.getAddress().getShortRegion());

        UIUtils.setFrequencyInfo(booking, frequencyText, activity);

        UIUtils.setPaymentInfo(paymentText, booking.getPaymentToProvider(), activity.getString(R.string.payment_value));
        UIUtils.setPaymentInfo(paymentBonusText, booking.getBonusPaymentToProvider(), activity.getString(R.string.bonus_payment_value));

        partnerText.setVisibility(booking.getPartner() != null && booking.getPartner().equalsIgnoreCase(PartnerNames.AIRBNB) ? View.VISIBLE : View.GONE);

    }
}
