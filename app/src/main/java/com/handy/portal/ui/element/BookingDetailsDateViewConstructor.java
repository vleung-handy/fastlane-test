package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsDateViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_time_text)
    protected TextView timeText;

    @InjectView(R.id.booking_details_date_text)
    protected TextView dateText;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String TIME_FORMAT = "h:mm a";
    private static final String INTERPUNCT = "\u00B7";

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_date;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.Action> allowedActions, Bundle arguments)
    {
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String formattedTime = timeFormat.format(startDate) + " - " + timeFormat.format(endDate);

        dateText.setText(getPrependByStartDate(startDate) + formattedDate.toUpperCase());
        timeText.setText(formattedTime.toUpperCase());
    }

    //returns a today or tomorrow prepend as needed
    private String getPrependByStartDate(Date bookingStartDate)
    {
        String prepend = "";

        Calendar calendar = Calendar.getInstance();

        Date currentTime = calendar.getTime();

        if(Utils.equalCalendarDates(currentTime, bookingStartDate))
        {
            prepend = (activity.getString(R.string.today) + " " + INTERPUNCT + " ").toUpperCase();
        }

        calendar.add(Calendar.DATE, 1);
        Date tomorrowTime = calendar.getTime();
        if(Utils.equalCalendarDates(tomorrowTime, bookingStartDate))
        {
            prepend = (activity.getString(R.string.tomorrow) + " " + INTERPUNCT + " ").toUpperCase();
        }

        return prepend;
    }

}
