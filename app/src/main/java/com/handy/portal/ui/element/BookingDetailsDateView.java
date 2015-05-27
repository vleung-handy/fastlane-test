package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsDateView extends BookingDetailsView
{
    private static final String DATE_FORMAT = "E MMM d";
    private static final String TIME_FORMAT = "h:mm a";


    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_date;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        TextView timeText = (TextView) parentViewGroup.findViewById(R.id.booking_details_time_text);
        TextView dateText = (TextView) parentViewGroup.findViewById(R.id.booking_details_date_text);

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String formattedTime = timeFormat.format(startDate) + " - " + timeFormat.format(endDate);

        dateText.setText(formattedDate);
        timeText.setText(formattedTime);
    }

}
