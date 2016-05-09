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
            mBookingAreaTextView.setText(booking.getAddress().getShortRegion());
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

        //Honor display attributes
        Booking.DisplayAttributes displayAttributes = booking.getDisplayAttributes();
        if(displayAttributes != null)
        {
            if(displayAttributes.getListingTitle() != null)
            {
                mBookingMessageTitleView
                        .setBodyText(displayAttributes.getListingTitle())
                        .setVisibility(View.VISIBLE); //the layout is GONE by default
            }
        }

        //apply styles specific to pro requested status
        boolean isRequested = booking.isRequested();
        if(isRequested)
        {
            //show the green strip indicator on the left of this entry
            mLeftStripIndicator.setVisibility(View.VISIBLE);
        }

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
            mBookingMessageTitleView.setVisibility(View.GONE);
        }
        else
        {
            mPartnerText.setVisibility(View.GONE);
        }
    }
}
