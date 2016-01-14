package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.util.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsDateView extends FrameLayout
{
    @Bind(R.id.booking_details_time_text)
    TextView timeText;
    @Bind(R.id.booking_details_date_text)
    TextView dateText;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String INTERPUNCT = "\u00B7";

    public BookingDetailsDateView(final Context context, Booking booking)
    {
        super(context);
        init(booking);
    }

    public BookingDetailsDateView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsDateView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BookingDetailsDateView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final Booking booking)
    {
        inflate(getContext(), R.layout.element_booking_details_date, this);
        ButterKnife.bind(this);

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);

        String formattedTime = DateTimeUtils.formatDateTo12HourClock(startDate) + " - " + DateTimeUtils.formatDateTo12HourClock(endDate);

        dateText.setText(getPrependByStartDate(startDate) + formattedDate.toUpperCase());
        timeText.setText(formattedTime.toUpperCase());
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
