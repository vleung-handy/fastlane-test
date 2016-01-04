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

import butterknife.Bind;

public class BookingDetailsLocationPanelViewConstructor extends BookingDetailsViewConstructor
{
    @Bind(R.id.booking_details_location_text)
    protected TextView locationText;

    @Bind(R.id.booking_details_service_text)
    protected TextView serviceText;

    @Bind(R.id.booking_details_payment_text)
    protected TextView paymentText;

    @Bind(R.id.booking_details_cents_text)
    protected TextView centsText;

    @Bind(R.id.booking_details_payment_bonus_text)
    protected TextView paymentBonusText;

    @Bind(R.id.booking_details_partner_text)
    protected TextView partnerText;

    @Bind(R.id.booking_details_requested_indicator_layout)
    protected LinearLayout requestedLayout;

    private final boolean isForPayments;

    public BookingDetailsLocationPanelViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
        this.isForPayments = arguments.getBoolean(BundleKeys.IS_FOR_PAYMENTS, false);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_location;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        BookingStatus bookingStatus = this.isForPayments ? BookingStatus.UNAVAILABLE : (BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);

        locationText.setText(booking.getFormattedLocation(bookingStatus));

        Booking.ServiceInfo serviceInfo = booking.getServiceInfo();
        if (serviceInfo.isHomeCleaning())
        {
            UIUtils.setFrequencyInfo(booking, serviceText, getContext());
        }
        else
        {
            serviceText.setText(serviceInfo.getDisplayName());
        }

        if (!this.isForPayments)
        {
            UIUtils.setPaymentInfo(paymentText, centsText, booking.getPaymentToProvider(), getContext().getString(R.string.payment_value));
            UIUtils.setPaymentInfo(paymentBonusText, null, booking.getBonusPaymentToProvider(), getContext().getString(R.string.bonus_payment_value));
        }

        //Partner takes priority over requested
        if (booking.getPartner() != null)
        {
            partnerText.setVisibility(booking.getPartner().equalsIgnoreCase(PartnerNames.AIRBNB) ? View.VISIBLE : View.GONE);
            requestedLayout.setVisibility(View.GONE);
        }
        else if (booking.isRequested() && !this.isForPayments)
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
