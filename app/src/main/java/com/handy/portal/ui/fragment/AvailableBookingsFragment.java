package com.handy.portal.ui.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import java.util.List;

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
    protected Event getRequestEvent()
    {
        return new Event.RequestAvailableBookingsEvent();
    }

    @Override
    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_available_bookings);
    }

    @Override
    protected String getTrackingType()
    {
        return "available job";
    }

    @Override
    protected boolean showRequestedIndicator(List<Booking> bookingsForDay)
    {
        //Bookings are sorted such that the requested bookings show up first so we just need to check the first one
        return bookingsForDay.size() > 0 && bookingsForDay.get(0).getIsRequested();
    }

    @Override
    protected boolean showClaimedIndicator(List<Booking> bookingsForDay)
    {
        return false;
    }

    @Override
    protected void setupCTAButton(List<Booking> bookingsForDay)
    {
        //do nothing, no ctas on this page, yet, maybe a refresh button
            //we should track how often pros see 0 jobs available
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        handleBookingsRetrieved(event);
    }

    @Subscribe
    public void onRequestBookingsError(Event.RequestAvailableBookingsErrorEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            errorText.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            errorText.setText(R.string.error_fetching_available_jobs);
        }
        fetchErrorView.setVisibility(View.VISIBLE);
    }

}
