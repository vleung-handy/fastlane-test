package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Booking;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AvailableBookingElementView extends BookingElementView
{
    @Bind(R.id.booking_entry_payment_text)
    protected TextView mPaymentText;

    @Bind(R.id.booking_entry_cents_text)
    protected TextView mCentsPaymentText;

    @Bind(R.id.booking_entry_payment_bonus_text)
    protected TextView mBonusPaymentText;

    @Bind(R.id.booking_entry_area_text)
    protected TextView mBookingAreaTextView;

    @Bind(R.id.booking_entry_service_text)
    protected TextView mBookingServiceTextView;

    @Bind(R.id.booking_entry_time_window_text)
    protected TextView mTimeWindowText;

    @Bind(R.id.booking_entry_partner_text)
    protected TextView mPartnerText;

    @Bind(R.id.booking_entry_requested_indicator_layout)
    protected LinearLayout mRequestedIndicatorLayout;

    @Bind(R.id.booking_entry_start_date_text)
    protected TextView mStartTimeText;

    @Bind(R.id.booking_entry_end_date_text)
    protected TextView mEndTimeText;

    @Bind(R.id.booking_entry_distance_text)
    protected TextView mFormattedDistanceText;

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        boolean isRequested = booking.isRequested();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parentContext)
                    .inflate(R.layout.element_available_booking_list_entry, parent, false);
        }

        ButterKnife.bind(this, convertView);

        //Payment
        UIUtils.setPaymentInfo(mPaymentText, mCentsPaymentText, booking.getPaymentToProvider(),
                parentContext.getString(R.string.payment_value));

        //Bonus Payment
        UIUtils.setPaymentInfo(mBonusPaymentText, null, booking.getBonusPaymentToProvider(),
                parentContext.getString(R.string.bonus_payment_value));

        //Area
        if (booking.isProxy())
        {
            mBookingAreaTextView.setText(booking.getLocationName());
        }
        else
        {
            mBookingAreaTextView.setText(booking.getAddress().getShortRegion());
        }

        //Service or frequency for home cleaning jobs
        UIUtils.setService(mBookingServiceTextView, booking);

        //Time window
        UIUtils.setTimeWindow(mTimeWindowText, booking.getMinimumHours(), booking.getHours());

        //Distance
        String formattedDistance = booking.getFormattedDistance();
        if (formattedDistance != null)
        {
            mFormattedDistanceText.setText(formattedDistance);
            mFormattedDistanceText.setVisibility(View.VISIBLE);
        }

        //Requested Provider
        mRequestedIndicatorLayout.setVisibility(isRequested ? View.VISIBLE : View.GONE);

        //Partner
        setPartnerText(booking.getPartner());

        //Date and Time
        final String formattedStartDate = TIME_OF_DAY_FORMAT.format(booking.getStartDate());
        final String formattedEndDate = TIME_OF_DAY_FORMAT.format(booking.getEndDate());
        mStartTimeText.setText(formattedStartDate.toLowerCase());
        mEndTimeText.setText(formattedEndDate.toLowerCase());

        this.associatedView = convertView;

        return convertView;
    }

    private void setPartnerText(String partner)
    {
        if (partner != null && partner.equalsIgnoreCase(PartnerNames.AIRBNB))
        {
            mPartnerText.setText(partner);
            mPartnerText.setVisibility(View.VISIBLE);

            // if the partner text is present, "you're requested" should not show up
            mRequestedIndicatorLayout.setVisibility(View.GONE);
        }
        else
        {
            mPartnerText.setVisibility(View.GONE);
        }
    }
}
