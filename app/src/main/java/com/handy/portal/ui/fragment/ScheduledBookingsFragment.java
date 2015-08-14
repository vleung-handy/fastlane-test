package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.Booking;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.ui.element.ScheduledBookingElementView;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class ScheduledBookingsFragment extends BookingsFragment<HandyEvent.ReceiveScheduledBookingsSuccess>
{
    @InjectView(R.id.scheduled_jobs_list_view)
    protected BookingListView scheduledJobsListView;

    @InjectView(R.id.scheduled_bookings_dates_scroll_view_layout)
    protected LinearLayout scheduledJobsDatesScrollViewLayout;

    @InjectView(R.id.scheduled_bookings_empty)
    protected ViewGroup noScheduledBookingsLayout;

    @InjectView(R.id.find_jobs_for_day_button)
    protected Button findJobsForDayButton;

    @InjectView(R.id.find_matching_jobs_button_container)
    protected ViewGroup findMatchingJobsButtonContainer;

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

    @Override
    protected ViewGroup getNoBookingsView()
    {
        return noScheduledBookingsLayout;
    }

    @Override
    protected HandyEvent getRequestEvent(List<Date> dates)
    {
        return new HandyEvent.RequestScheduledBookings(dates);
    }

    @Override
    protected String getTrackingType()
    {
        return "scheduled job";
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveScheduledBookingsSuccess event)
    {
        handleBookingsRetrieved(event);
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass()
    {
        return ScheduledBookingElementView.class;
    }

    @Override
    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings)
    {

        //show Find Jobs and Find Matching Jobs buttons only if we're inside of our available bookings length range
        long currentTime = DateTimeUtils.getDateWithoutTime(new Date()).getTime();
        long dateOfBookingsTime = dateOfBookings.getTime();
        long dateDifference = dateOfBookingsTime - currentTime;
        final Integer hoursSpanningAvailableBookings = configManager.getConfigParamValue(ConfigManager.KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS, 0);
        if (bookingsForDay.size() == 0 && dateDifference < DateTimeUtils.MILLISECONDS_IN_HOUR * hoursSpanningAvailableBookings)
        {
            findJobsForDayButton.setVisibility(View.VISIBLE);
        }
        else
        {
            findJobsForDayButton.setVisibility(View.GONE);
        }

        if (bookingsForDay.size() == 1 && dateDifference < DateTimeUtils.MILLISECONDS_IN_HOUR * hoursSpanningAvailableBookings)
        {
            findMatchingJobsButtonContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            findMatchingJobsButtonContainer.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.find_jobs_for_day_button)
    public void onFindJobsButtonClicked()
    {
        TransitionStyle transitionStyle = TransitionStyle.TAB_TO_TAB;
        long epochTime = selectedDay.getTime();
        //navigate back to available bookings for this day
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS, arguments, transitionStyle));
    }

    @OnClick(R.id.find_matching_jobs_button)
    public void onFindMatchingJobsButtonClicked()
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingsForSelectedDay.get(0).getId());

        bus.post(new HandyEvent.NavigateToTab(MainViewTab.MATCHING_JOBS, arguments, TransitionStyle.SLIDE_UP));
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
        for (Booking b : bookingsForDay)
        {
            if (!b.isEnded())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int numberOfDaysToDisplay()
    {
        return 28;
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveScheduledBookingsError event)
    {
        handleBookingsRetrievalError(event);
    }
}
