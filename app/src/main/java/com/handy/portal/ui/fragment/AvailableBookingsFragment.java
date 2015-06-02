package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;

public class AvailableBookingsFragment extends BookingsFragment
{
    @InjectView(R.id.available_jobs_requested_list_view)
    protected BookingListView availableJobsRequestedListView;

    @InjectView(R.id.available_jobs_unrequested_list_view)
    protected BookingListView availableJobsUnrequestedListView;

    @InjectView(R.id.available_bookings_dates_scroll_view_layout)
    protected LinearLayout availableJobsDatesScrollViewLayout;

    protected BookingListView getRequestedBookingListView()
    {
        return availableJobsRequestedListView;
    }

    protected BookingListView getUnrequestedBookingListView()
    {
        return availableJobsUnrequestedListView;
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
        availableJobsRequestedListView.setOnItemClickListener(new OnBookingItemClickedListener());
        availableJobsUnrequestedListView.setOnItemClickListener(new OnBookingItemClickedListener());
    }

    private class OnBookingItemClickedListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
        {
            Booking booking = (Booking) adapter.getItemAtPosition(position);
            Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
            bus.post(new Event.NavigateToTabEvent(MainViewTab.DETAILS, arguments));
        }
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
       handleBookingsRetrieved(event);
    }

}
