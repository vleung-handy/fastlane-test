package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.util.UIUtils;

import butterknife.InjectView;

public class BookingDetailsLocationPanelViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_location_text)
    protected TextView locationText;

    @InjectView(R.id.booking_details_service_text)
    protected TextView serviceText;

    @InjectView(R.id.booking_details_payment_text)
    protected TextView paymentText;

    @InjectView(R.id.booking_details_cents_text)
    protected TextView centsText;

    @InjectView(R.id.booking_details_payment_bonus_text)
    protected TextView paymentBonusText;

    @InjectView(R.id.booking_details_partner_text)
    protected TextView partnerText;

    @InjectView(R.id.booking_details_requested_indicator_layout)
    protected LinearLayout requestedLayout;

    public BookingDetailsLocationPanelViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_location;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        BookingStatus bookingStatus = (BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);

        if(bookingStatus == BookingStatus.AVAILABLE)
        {
            locationText.setText(booking.getAddress().getShortRegion() + "\n" + booking.getAddress().getZip());
        }
        else
        {
            locationText.setText(booking.getAddress().getStreetAddress() + "\n" + booking.getAddress().getZip());
        }

        Booking.ServiceInfo serviceInfo = booking.getServiceInfo();
        if (serviceInfo.isHomeCleaning())
        {
            UIUtils.setFrequencyInfo(booking, serviceText, getContext());
        }
        else
        {
            serviceText.setText(serviceInfo.getDisplayName());
        }

        UIUtils.setPaymentInfo(paymentText, centsText, booking.getPaymentToProvider(), getContext().getString(R.string.payment_value));
        UIUtils.setPaymentInfo(paymentBonusText, null, booking.getBonusPaymentToProvider(), getContext().getString(R.string.bonus_payment_value));

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

        return true;
    }
}
