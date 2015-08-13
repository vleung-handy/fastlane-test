package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.util.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;

public class BookingDetailsDateViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_time_text)
    protected TextView timeText;

    @InjectView(R.id.booking_details_date_text)
    protected TextView dateText;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String INTERPUNCT = "\u00B7";

    public BookingDetailsDateViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_date;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);

        String formattedTime = DateTimeUtils.formatDateTo12hrClock(startDate) + " - " + DateTimeUtils.formatDateTo12hrClock(endDate);

        dateText.setText(getPrependByStartDate(startDate) + formattedDate.toUpperCase());
        timeText.setText(formattedTime.toUpperCase());

        return true;
    }

    //returns a today or tomorrow prepend as needed
    private String getPrependByStartDate(Date bookingStartDate)
    {
        String prepend = "";

        Calendar calendar = Calendar.getInstance();

        Date currentTime = calendar.getTime();

        if (DateTimeUtils.equalCalendarDates(currentTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.today) + " " + INTERPUNCT + " ").toUpperCase();
        }

        calendar.add(Calendar.DATE, 1);
        Date tomorrowTime = calendar.getTime();
        if (DateTimeUtils.equalCalendarDates(tomorrowTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.tomorrow) + " " + INTERPUNCT + " ").toUpperCase();
        }

        return prepend;
    }

}
