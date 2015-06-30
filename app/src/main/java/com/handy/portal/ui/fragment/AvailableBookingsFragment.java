package com.handy.portal.ui.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.InjectView;

public class AvailableBookingsFragment extends BookingsFragment<HandyEvent.ReceiveAvailableBookingsSuccess>
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
    protected HandyEvent getRequestEvent()
    {
        return new HandyEvent.RequestAvailableBookings();
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
    protected void setupCTAButton(List<Booking> bookingsForDay, Date dateOfBookings)
    {
        //do nothing, no ctas on this page, yet, maybe a refresh button
            //we should track how often pros see 0 jobs available
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveAvailableBookingsSuccess event)
    {
        handleBookingsRetrieved(event);
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveAvailableBookingsError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
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
