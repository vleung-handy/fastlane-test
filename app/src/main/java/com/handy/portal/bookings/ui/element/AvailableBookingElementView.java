package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Address;
import com.handy.portal.library.util.UIUtils;

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

    @Bind(R.id.booking_entry_listing_message_title_view)
    BookingMessageTitleView mBookingMessageTitleView;

    @Bind(R.id.booking_entry_start_date_text)
    TextView mStartTimeText;

    @Bind(R.id.booking_entry_end_date_text)
    TextView mEndTimeText;

    @Bind(R.id.booking_entry_distance_text)
    TextView mFormattedDistanceText;

    @Bind(R.id.booking_list_entry_left_strip_indicator)
    ImageView mLeftStripIndicator;

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
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

        //Honor pro request display attributes
        //TODO ugly! would be nice if the display attributes were generic but
        //since there's not enough time to fully generalize this
        //we're making it specific to pro request for now
        Booking.DisplayAttributes proRequestDisplayAttributes = booking.getProviderRequestDisplayAttributes();
        boolean isRequested = booking.isRequested();

        if (isRequested
                && proRequestDisplayAttributes != null
                && proRequestDisplayAttributes.getListingTitle() != null)
        {
            mBookingMessageTitleView
                    .setBodyText(proRequestDisplayAttributes.getListingTitle())
                    .setVisibility(View.VISIBLE); //the layout is GONE by default

            //show the green strip indicator on the left of this entry.
            //if no listing title don't show this because it doesn't convey any information by itself
            mLeftStripIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            mLeftStripIndicator.setVisibility(View.GONE);
            mBookingMessageTitleView.setVisibility(View.GONE);
        }

        //Partner
        setPartnerText(booking.getPartner());

        //Date and Time
        final String formattedStartDate = TIME_OF_DAY_FORMAT.format(booking.getStartDate());
        final String formattedEndDate = TIME_OF_DAY_FORMAT.format(booking.getEndDate());
        mStartTimeText.setText(formattedStartDate.toLowerCase());
        mEndTimeText.setText(formattedEndDate.toLowerCase());

        mAssociatedView = convertView;

        return convertView;
    }

    private void setPartnerText(String partner)
    {
        if (partner != null && partner.equalsIgnoreCase(PartnerNames.AIRBNB))
        {
            mPartnerText.setText(partner);
            mPartnerText.setVisibility(View.VISIBLE);

            // if the partner text is present, "you're requested" should not show up
            mBookingMessageTitleView.setVisibility(View.GONE);
        }
        else
        {
            mPartnerText.setVisibility(View.GONE);
        }
    }
}
