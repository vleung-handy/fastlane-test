package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Booking;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsTitleView extends FrameLayout
{
    @Bind(R.id.booking_details_location_text)
    TextView mLocationText;
    @Bind(R.id.booking_details_service_text)
    TextView mServiceText;
    @Bind(R.id.booking_details_payment_text)
    TextView mPaymentText;
    @Bind(R.id.booking_details_cents_text)
    TextView mCentsText;
    @Bind(R.id.booking_details_payment_bonus_text)
    TextView mPaymentBonusText;
    @Bind(R.id.booking_details_partner_text)
    TextView mPartnerText;
    @Bind(R.id.booking_details_requested_indicator_layout)
    LinearLayout mRequestedLayout;

    public BookingDetailsTitleView(final Context context, Booking booking,
                                   boolean isFromPayments, Booking.BookingStatus status)
    {
        super(context);
        init(booking, isFromPayments, status);
    }

    public BookingDetailsTitleView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsTitleView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BookingDetailsTitleView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(@NonNull final Booking booking, boolean isFromPayments,
                     @NonNull Booking.BookingStatus status)
    {
        inflate(getContext(), R.layout.element_booking_details_location, this);
        ButterKnife.bind(this);

        Booking.BookingStatus bookingStatus = isFromPayments ? Booking.BookingStatus.UNAVAILABLE : status;

        mLocationText.setText(booking.getFormattedLocation(bookingStatus));

        Booking.ServiceInfo serviceInfo = booking.getServiceInfo();
        if (serviceInfo.isHomeCleaning())
        {
            UIUtils.setFrequencyInfo(booking, mServiceText, getContext());
        }
        else
        {
            mServiceText.setText(serviceInfo.getDisplayName());
        }

        if (!isFromPayments)
        {
            UIUtils.setPaymentInfo(mPaymentText, mCentsText, booking.getPaymentToProvider(),
                    getContext().getString(R.string.payment_value));
            UIUtils.setPaymentInfo(mPaymentBonusText, null, booking.getBonusPaymentToProvider(),
                    getContext().getString(R.string.bonus_payment_value));
        }

        //Partner takes priority over requested
        if (booking.getPartner() != null)
        {
            mPartnerText.setVisibility(booking.getPartner().equalsIgnoreCase(PartnerNames.AIRBNB) ?
                    View.VISIBLE : View.GONE);
            mRequestedLayout.setVisibility(View.GONE);
        }
        else if (booking.isRequested() && !isFromPayments)
        {
            mPartnerText.setVisibility(View.GONE);
            mRequestedLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            mPartnerText.setVisibility(View.GONE);
            mRequestedLayout.setVisibility(View.GONE);
        }
    }
}
