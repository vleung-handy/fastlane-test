package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Address;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AvailableBookingElementView extends BookingElementView
{
    @Bind(R.id.booking_entry_payment)
    BookingDetailsPaymentView mPayment;

    @Bind(R.id.booking_entry_payment_bonus_text)
    TextView mBonusPaymentText;

    @Bind(R.id.booking_entry_area_text)
    TextView mBookingAreaTextView;

    @Bind(R.id.booking_entry_service_text)
    TextView mBookingServiceTextView;

    @Bind(R.id.booking_entry_partner_text)
    TextView mPartnerText;

    @Bind(R.id.booking_entry_requested_indicator_layout)
    LinearLayout mRequestedIndicatorLayout;

    @Bind(R.id.booking_entry_start_date_text)
    TextView mStartTimeText;

    @Bind(R.id.booking_entry_end_date_text)
    TextView mEndTimeText;

    @Bind(R.id.booking_entry_distance_text)
    TextView mFormattedDistanceText;

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
        mPayment.init(booking);

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
            Address address = booking.getAddress();
            if (address != null)
            {
                mBookingAreaTextView.setText(booking.isUK() ?
                        parentContext.getString(R.string.comma_formatted,
                                address.getShortRegion(), address.getZip()) :
                        address.getShortRegion());
            }
        }

        //Service or frequency for home cleaning jobs
        UIUtils.setService(mBookingServiceTextView, booking);

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