package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.PartnerNames;
import com.handy.portal.model.Booking;
import com.handy.portal.util.UIUtils;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AvailableBookingElementView extends BookingElementView
{
    @InjectView(R.id.booking_entry_payment_text)
    protected TextView paymentText;

    @InjectView(R.id.booking_entry_cents_text)
    protected TextView centsPaymentText;

    @InjectView(R.id.booking_entry_payment_bonus_text)
    protected TextView bonusPaymentText;

    @InjectView(R.id.booking_entry_area_text)
    protected TextView bookingAreaTextView;

    @InjectView(R.id.booking_entry_service_text)
    protected TextView bookingServiceTextView;

    @InjectView(R.id.booking_entry_partner_text)
    protected TextView partnerText;

    @InjectView(R.id.booking_entry_requested_indicator_layout)
    protected LinearLayout requestedIndicatorLayout;

    @InjectView(R.id.booking_entry_requested_indicator)
    protected ImageView requestedIndicatorBar;

    @InjectView(R.id.booking_entry_start_date_text)
    protected TextView startTimeText;

    @InjectView(R.id.booking_entry_end_date_text)
    protected TextView endTimeText;

    @InjectView(R.id.booking_entry_distance_text)
    protected TextView formattedDistanceText;

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        boolean isRequested = booking.isRequested();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_available_booking_list_entry, parent, false);
        }

        ButterKnife.inject(this, convertView);

        //Payment
        UIUtils.setPaymentInfo(paymentText, centsPaymentText, booking.getPaymentToProvider(), parentContext.getString(R.string.payment_value));

        //Bonus Payment
        UIUtils.setPaymentInfo(bonusPaymentText, null, booking.getBonusPaymentToProvider(), parentContext.getString(R.string.bonus_payment_value));

        //Area
        if (booking.isProxy())
        {
            bookingAreaTextView.setText(booking.getLocationName());
        }
        else
        {
            bookingAreaTextView.setText(booking.getAddress().getShortRegion());
        }

        //Service or frequency for home cleaning jobs
        Booking.ServiceInfo serviceInfo = booking.getServiceInfo();
        if (serviceInfo.isHomeCleaning())
        {
            String frequencyInfo = UIUtils.getFrequencyInfo(booking, parentContext);
            if (booking.isUK() && booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES).size() > 0)
            {
                frequencyInfo += " \u22C5 " + parentContext.getString(R.string.supplies);
            }
            bookingServiceTextView.setText(frequencyInfo);
        }
        else
        {
            bookingServiceTextView.setText(serviceInfo.getDisplayName());
        }

        //Distance
        String formattedDistance = booking.getFormattedDistance();
        if (formattedDistance != null)
        {
            formattedDistanceText.setText(formattedDistance);
            formattedDistanceText.setVisibility(View.VISIBLE);
        }

        //Requested Provider
        requestedIndicatorBar.setVisibility(isRequested ? View.VISIBLE : View.INVISIBLE);
        requestedIndicatorLayout.setVisibility(isRequested ? View.VISIBLE : View.GONE);

        //Partner
        setPartnerText(booking.getPartner());

        //Date and Time
        SimpleDateFormat timeOfDayFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedStartDate = timeOfDayFormat.format(booking.getStartDate());
        String formattedEndDate = timeOfDayFormat.format(booking.getEndDate());
        startTimeText.setText(formattedStartDate.toLowerCase());
        endTimeText.setText(formattedEndDate.toLowerCase());

        this.associatedView = convertView;

        return convertView;
    }

    private void setPartnerText(String partner)
    {
        if (partner != null && partner.equalsIgnoreCase(PartnerNames.AIRBNB))
        {
            partnerText.setText(partner);
            partnerText.setVisibility(View.VISIBLE);

            // if the partner text is present, "you're requested" should not show up
            requestedIndicatorLayout.setVisibility(View.GONE);
        }
        else
        {
            partnerText.setVisibility(View.GONE);
        }
    }
}
