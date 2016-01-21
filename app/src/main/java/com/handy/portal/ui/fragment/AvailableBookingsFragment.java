package com.handy.portal.ui.fragment;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class AvailableBookingsFragment extends BookingsFragment<HandyEvent.ReceiveAvailableBookingsSuccess>
{
    @Bind(R.id.available_jobs_list_view)
    BookingListView mAvailableJobsListView;
    @Bind(R.id.available_bookings_dates_scroll_view_layout)
    LinearLayout mAvailableJobsDatesScrollViewLayout;
    @Bind(R.id.available_bookings_empty)
    ViewGroup mNoAvailableBookingsLayout;

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
        return mAvailableJobsListView;
    }

    @Override
    protected ViewGroup getNoBookingsView()
    {
        return mNoAvailableBookingsLayout;
    }

    protected LinearLayout getDatesLayout()
    {
        return mAvailableJobsDatesScrollViewLayout;
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

    @NonNull
    @Override
    protected String getTrackingType()
    {
        return getString(R.string.available_job);
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
        int daysSpanningAvailableBookings = DateTimeUtils.HOURS_IN_SIX_DAYS;
        if (configManager.getConfigurationResponse() != null)
        {
            daysSpanningAvailableBookings = configManager.getConfigurationResponse().getHoursSpanningAvailableBookings() / DateTimeUtils.HOURS_IN_DAY;
        }
        return daysSpanningAvailableBookings + 1; // plus today
    }

    @Override
    protected void beforeRequestBookings() {}

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass()
    {
        return AvailableBookingElementView.class;
    }

    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings)
    {
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                .createAvailableJobDateClickedLog(dateOfBookings, bookingsForDay.size())));
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveAvailableBookingsSuccess event)
    {
        handleBookingsRetrieved(event);
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveAvailableBookingsError event)
    {
        handleBookingsRetrievalError(event, R.string.error_fetching_available_jobs);
    }

    @Subscribe
    public void onReceiveProviderSettingsSuccess(ProviderSettingsEvent.ReceiveProviderSettingsSuccess event)
    {
        super.onReceiveProviderSettingsSuccess(event);
    }


    @Subscribe
    public void onReceiveProviderSettingsError(ProviderSettingsEvent.ReceiveProviderSettingsError event)
    {
        super.onReceiveProviderSettingsError(event);
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateSuccess(ProviderSettingsEvent.ReceiveProviderSettingsUpdateSuccess event)
    {
        super.onReceiveProviderSettingsUpdateSuccess(event);
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateError(ProviderSettingsEvent.ReceiveProviderSettingsUpdateError event)
    {
        super.onReceiveProviderSettingsUpdateError(event);
    }
}
