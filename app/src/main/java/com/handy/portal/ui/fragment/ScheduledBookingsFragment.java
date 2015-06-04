package com.handy.portal.ui.fragment;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduledBookingsFragment extends BookingsFragment
{
    @InjectView(R.id.scheduled_jobs_requested_list_view)
    protected BookingListView scheduledJobsRequestedListView;

    @InjectView(R.id.scheduled_jobs_unrequested_list_view)
    protected BookingListView scheduledJobsUnrequestedListView;

    @InjectView(R.id.scheduled_bookings_dates_scroll_view_layout)
    protected LinearLayout scheduledJobsDatesScrollViewLayout;

    protected LinearLayout getDatesLayout()
    {
        return scheduledJobsDatesScrollViewLayout;
    }

    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_scheduled_bookings);
    }

    @Override
    protected BookingListView getBookingListView()
    {
        return scheduledJobsUnrequestedListView;
    }

    // TODO: Implement
    @Override
    protected ViewGroup getNoBookingsView()
    {
        return null;
    }

    protected void requestBookings()
    {
        bus.post(new Event.RequestScheduledBookingsEvent());
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        handleBookingsRetrieved(event);
    }
}
