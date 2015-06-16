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


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduledBookingsFragment extends BookingsFragment
{
    @InjectView(R.id.scheduled_jobs_list_view)
    protected BookingListView scheduledJobsListView;

    @InjectView(R.id.scheduled_bookings_dates_scroll_view_layout)
    protected LinearLayout scheduledJobsDatesScrollViewLayout;

    @InjectView(R.id.scheduled_bookings_empty)
    protected ViewGroup noScheduledBookingsLayout;

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
        return scheduledJobsListView;
    }

    // TODO: Implement
    @Override
    protected ViewGroup getNoBookingsView()
    {
        return noScheduledBookingsLayout;
    }

    @Override
    protected Event getRequestEvent()
    {
        return new Event.RequestScheduledBookingsEvent();
    }

    @Override
    protected String getTrackingType()
    {
        return "scheduled job";
    }

    @Subscribe
    public void onBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        handleBookingsRetrieved(event);
    }

    @Override
    protected boolean showRequestedIndicator(List<Booking> bookingsForDay)
    {
        return false;
    }

    @Override
    //All bookings not in the past on this page should cause the claimed indicator to appear
    protected boolean showClaimedIndicator(List<Booking> bookingsForDay)
    {
        if(bookingsForDay.size() > 0)
        {
            for(Booking b : bookingsForDay)
            {
                if(!b.isInPast())
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    public void onRequestBookingsError(Event.RequestScheduledBookingsErrorEvent event)
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
