package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cdavis on 5/6/15.
 */
public class BookingElementView
{
    private BookingElementMediator mediator;
    public View associatedView;

    public BookingElementView(BookingElementMediator mediator)
    {
        this.mediator = mediator;
    }

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        if (booking == null)
        {
            System.err.println("Can not fill cell based on null booking");
            return null;
        }

        boolean isRequested = booking.getIsRequested();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_booking_list_entry, parent, false);
        }

        TextView bookingAreaTextView = (TextView) convertView.findViewById(R.id.booking_area);
        String bookingArea = booking.getAddress().getShortRegion();

        bookingAreaTextView.setText(bookingArea);

        LinearLayout requestedIndicator = (LinearLayout) convertView.findViewById(R.id.requested_indicator);
        requestedIndicator.setVisibility(isRequested ? View.VISIBLE : View.GONE);

        Date startDate = booking.getStartDate();

        SimpleDateFormat ft = new SimpleDateFormat("hh:mma");

        String formattedStartDate = ft.format(startDate);
        String formattedEndDate = ft.format(booking.getEndDate());

        TextView startTimeText = (TextView) convertView.findViewById(R.id.booking_start_date);
        TextView endTimeText = (TextView) convertView.findViewById(R.id.booking_end_date);

        startTimeText.setText(formattedStartDate);
        endTimeText.setText(formattedEndDate);

        this.associatedView = convertView;

        return convertView;
    }
}