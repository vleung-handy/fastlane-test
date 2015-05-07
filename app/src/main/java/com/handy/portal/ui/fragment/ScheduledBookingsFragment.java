package com.handy.portal.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;

import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduledBookingsFragment extends BookingsFragment
{

    @InjectView(R.id.scheduled_jobs_list_view)
    protected BookingListView scheduledJobsListView;

    @InjectView(R.id.scheduled_bookings_dates_scroll_view_layout)
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
        bus.post(new Event.RequestScheduledBookingsEvent());
    }

    protected void initListClickListener()
    {
        scheduledJobsListView.setOnItemClickListener(
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
}
