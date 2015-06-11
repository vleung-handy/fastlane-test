package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.PartnerNames;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.util.UIUtils;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookingElementView
{
    @InjectView(R.id.booking_entry_payment_text)
    protected TextView paymentText;

    @InjectView(R.id.booking_entry_payment_bonus_text)
    protected TextView bonusPaymentText;

    @InjectView(R.id.booking_entry_area_text)
    protected TextView bookingAreaTextView;

    @InjectView(R.id.booking_entry_frequency_text)
    protected TextView frequencyTextView;

    @InjectView(R.id.booking_entry_requested_indicator)
    protected ImageView requestedIndicator;

    @InjectView(R.id.booking_entry_partner_text)
    protected TextView partnerText;

    @InjectView(R.id.booking_entry_requested_indicator_layout)
    protected LinearLayout requestedIndicatorLayout;

    @InjectView(R.id.booking_entry_start_date_text)
    protected TextView startTimeText;

    @InjectView(R.id.booking_entry_end_date_text)
    protected TextView endTimeText;

    private static final String DATE_FORMAT = "h:mm a";

    public View associatedView;

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        if (booking == null)
        {
            View separator = LayoutInflater.from(parentContext).inflate(R.layout.element_booking_list_entry_separator, parent, false);
            this.associatedView = separator;
            return separator;
        }

        boolean isRequested = booking.getIsRequested();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getId() == R.id.booking_list_entry_separator)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_booking_list_entry, parent, false);
        }

        ButterKnife.inject(this, convertView);

        //Payment
        UIUtils.setPaymentInfo(paymentText, booking.getPaymentToProvider(), parentContext.getString(R.string.payment_value));

        //Bonus Payment
        UIUtils.setPaymentInfo(bonusPaymentText, booking.getBonusPaymentToProvider(), parentContext.getString(R.string.bonus_payment_value));

        //Area
        bookingAreaTextView.setText(booking.getAddress().getShortRegion());

        //Frequency
        String frequencyInfo = UIUtils.getFrequencyInfo(booking, parentContext);
        if (booking.getAddress().isUKRegion() && booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES).size() > 0)
        {
            frequencyInfo += " \u22C5 " + parentContext.getString(R.string.supplies);
        }
        frequencyTextView.setText(frequencyInfo);

        //Requested Provider
        requestedIndicator.setVisibility(isRequested ? View.VISIBLE : View.INVISIBLE);
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
        }
        else
        {
            partnerText.setVisibility(View.GONE);
        }
    }
}