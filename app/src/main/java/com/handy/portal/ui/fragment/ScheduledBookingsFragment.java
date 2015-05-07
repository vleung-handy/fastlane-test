package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.event.BookingsRetrievedEvent;
import com.handy.portal.event.RequestAvailableBookingsEvent;
import com.handy.portal.event.RequestScheduledBookingsEvent;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduledBookingsFragment extends BookingsFragment {

    @InjectView(R.id.scheduledJobsListView)
    protected BookingListView scheduledJobsListView;

    @InjectView(R.id.scheduledBookingsDatesScrollViewLayout)
    protected LinearLayout datesScrollViewLayout;

    protected BookingListView getBookingListView()
    {
        return scheduledJobsListView;
    }

    protected LinearLayout getDatesLayout()
    {
        return datesScrollViewLayout;
    }

    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_scheduled_bookings);
    }

    protected void requestBookings()
    {
        bus.post(new RequestScheduledBookingsEvent(HACK_HARDCODE_PROVIDER_ID));
    }

    protected void initListClickListener()
    {
        scheduledJobsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        Booking booking = (Booking) adapter.getItemAtPosition(position);
                        System.out.println("clicked on booking with id " + booking.getId());
                    }
                }
        );
    }
}
