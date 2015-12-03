package com.handy.portal.ui.fragment;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.util.DateTimeUtils;
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

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.AVAILABLE_JOBS;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.available_jobs, false);
    }

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
    protected HandyEvent getRequestEvent(List<Date> dates, boolean useCachedIfPresent)
    {
        return new HandyEvent.RequestAvailableBookings(dates, useCachedIfPresent);
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
    protected boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay)
    {
        //Bookings are sorted such that the requested bookings show up first so we just need to check the first one
        return bookingsForDay.size() > 0 && bookingsForDay.get(0).isRequested();
    }

    @Override
    protected boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay)
    {
        return false;
    }

    @Override
    protected int numberOfDaysToDisplay()
    {
        int daysSpanningAvailableBookings = DateTimeUtils.DAYS_IN_WEEK; //default to one week as a fallback
        if (configManager.getConfigurationResponse() != null)
        {
            daysSpanningAvailableBookings = configManager.getConfigurationResponse().getHoursSpanningAvailableBookings() / DateTimeUtils.HOURS_IN_DAY;
        }
        return daysSpanningAvailableBookings + 1; // plus today
    }

    @Override
    protected void beforeRequestBookings()
    {
    }

    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings)
    {
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveAvailableBookingsSuccess event)
    {
        handleBookingsRetrieved(event);
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass()
    {
        return AvailableBookingElementView.class;
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveAvailableBookingsError event)
    {
        handleBookingsRetrievalError(event, R.string.error_fetching_available_jobs);
    }
}
