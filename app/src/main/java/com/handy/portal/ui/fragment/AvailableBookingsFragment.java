package com.handy.portal.ui.fragment;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
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

    @InjectView(R.id.available_bookings_empty)
    protected ViewGroup noAvailableBookingsLayout;

    protected BookingListView getBookingListView()
    {
        return availableJobsListView;
    }

    @Override
    protected ViewGroup getNoBookingsView()
    {
        return noAvailableBookingsLayout;
    }

    protected LinearLayout getDatesLayout()
    {
        return availableJobsDatesScrollViewLayout;
    }

    @Override
    protected int getErrorTextResId()
    {
        return R.string.error_fetching_available_jobs;
    }

    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_available_bookings);
    }

    protected void requestBookings()
    {
        bus.post(new Event.RequestAvailableBookingsEvent());
    }

    @Override
    protected void trackDateClicked()
    {
        bus.post(new Event.DateClickedEvent("available job"));
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        handleBookingsRetrieved(event);
    }

}
