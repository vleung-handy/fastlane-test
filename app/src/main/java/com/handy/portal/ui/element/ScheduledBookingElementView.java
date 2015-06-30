package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

//Not setting up a clean UI hierarchy with AvailableBookingElement because ScheduledBookingElement is going to radically change soon to a google calendar style view

public class ScheduledBookingElementView extends BookingElementView
{
    @InjectView(R.id.booking_entry_address_text)
    protected TextView addressTextView;

    @InjectView(R.id.booking_entry_region_text)
    protected TextView bookingRegionText;

    @InjectView(R.id.booking_entry_claimed_indicator_layout)
    protected LinearLayout claimedIndicatorLayout;

    @InjectView(R.id.booking_entry_completed_text)
    protected TextView completedText;

    @InjectView(R.id.booking_entry_completed_indicator)
    protected ImageView completedIndicator;

    @InjectView(R.id.booking_entry_start_date_text)
    protected TextView startTimeText;

    @InjectView(R.id.booking_entry_end_date_text)
    protected TextView endTimeText;

    private static final String DATE_FORMAT = "h:mm a";

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        if (booking == null)
        {
            View separator = LayoutInflater.from(parentContext).inflate(R.layout.element_booking_list_entry_separator, parent, false);
            this.associatedView = separator;
            return separator;
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getId() == R.id.booking_list_entry_separator)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_scheduled_booking_list_entry, parent, false);
        }

        ButterKnife.inject(this, convertView);

        //Address
        addressTextView.setText(booking.getAddress().getCompleteAddress());

        //Area
        bookingRegionText.setText(booking.getAddress().getShortRegion());

        //Claimed
        claimedIndicatorLayout.setVisibility(booking.isEnded() ? View.GONE : View.VISIBLE);

        //Completed
        completedText.setVisibility(booking.isEnded() ? View.VISIBLE : View.GONE);
        completedIndicator.setVisibility(booking.isEnded() ? View.VISIBLE : View.GONE);

        //Date and Time
        SimpleDateFormat timeOfDayFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedStartDate = timeOfDayFormat.format(booking.getStartDate());
        String formattedEndDate = timeOfDayFormat.format(booking.getEndDate());
        startTimeText.setText(formattedStartDate.toLowerCase());
        endTimeText.setText(formattedEndDate.toLowerCase());

        this.associatedView = convertView;

        return convertView;
    }

}