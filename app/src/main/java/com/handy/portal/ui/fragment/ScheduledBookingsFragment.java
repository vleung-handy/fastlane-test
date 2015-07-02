package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.core.ConfigManager;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.form.BookingListView;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A placeholder fragment containing a simple view.
 */
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
    protected HandyEvent getRequestEvent()
    {
        return new HandyEvent.RequestScheduledBookings();
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
    protected void insertSeparator(List<Booking> bookingsForDay)
    {
        // do nothing
    }

    @Override
    protected void setupCTAButton(List<Booking> bookingsForDay, Date dateOfBookings)
    {
        //don't show the CTA if we're outside of our available bookings length range
        long currentTime = Utils.getDateWithoutTime(new Date()).getTime();
        long dateOfBookingsTime = dateOfBookings.getTime();
        long dateDifference = dateOfBookingsTime - currentTime;
        final long millisecondsInHour = 3600000;
        final Integer hoursSpanningAvailableBookings = configManager.getConfigParamValue(ConfigManager.KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS, 0);
        if(bookingsForDay.size() == 0 && dateDifference < millisecondsInHour * hoursSpanningAvailableBookings)
        {
            findJobsForDayButton.setVisibility(View.VISIBLE);
        }
        else
        {
            findJobsForDayButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.find_jobs_for_day_button)
    public void onFindJobsButtonClicked(View v)
    {
        TransitionStyle transitionStyle = TransitionStyle.TAB_TO_TAB;
        long epochTime = selectedDay.getTime();
        //navigate back to available bookings for this day
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.JOBS, arguments, transitionStyle));
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
                if(!b.isEnded())
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveScheduledBookingsError event)
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
