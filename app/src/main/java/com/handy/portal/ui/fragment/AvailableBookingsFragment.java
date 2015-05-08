package com.handy.portal.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;

public class AvailableBookingsFragment extends BookingsFragment
{
    @InjectView(R.id.available_jobs_list_view)
    protected BookingListView availableJobsListView;

    @InjectView(R.id.available_bookings_dates_scroll_view_layout)
    protected LinearLayout availableJobsDatesScrollViewLayout;

    protected BookingListView getBookingListView()
    {
        return availableJobsListView;
    }

    protected LinearLayout getDatesLayout()
    {
        return availableJobsDatesScrollViewLayout;
    }

    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_available_bookings);
    }

    protected void requestBookings()
    {
        bus.post(new Event.RequestAvailableBookingsEvent());
    }

    protected void initListClickListener()
    {
        availableJobsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
                    {
                        Booking booking = (Booking) adapter.getItemAtPosition(position);
                    }
                }
        );
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
       handleBookingsRetrieved(event);
    }

}
