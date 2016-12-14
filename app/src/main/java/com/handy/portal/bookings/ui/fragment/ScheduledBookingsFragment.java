package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.bookings.ui.element.NewDateButton;
import com.handy.portal.bookings.ui.element.ScheduledBookingElementView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.fragment.MainActivityFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ScheduledBookingsFragment extends BookingsFragment<HandyEvent.ReceiveScheduledBookingsSuccess>
    implements DatesPagerAdapter.DateSelectedListener
{
    private static final String SOURCE_SCHEDULED_JOBS_LIST = "scheduled_jobs_list";
    @BindView(R.id.scheduled_jobs_list_view)
    BookingListView mScheduledJobsListView;
    @BindView(R.id.scheduled_bookings_dates_scroll_view)
    ViewGroup mScheduledJobsDatesScrollView;
    @BindView(R.id.scheduled_bookings_dates_scroll_view_layout)
    LinearLayout mScheduledJobsDatesScrollViewLayout;
    @BindView(R.id.scheduled_bookings_empty)
    SwipeRefreshLayout mNoScheduledBookingsLayout;
    @BindView(R.id.find_jobs_for_day_button)
    Button mFindJobsForDayButton;
    @BindView(R.id.find_matching_jobs_button_container)
    ViewGroup mFindMatchingJobsButtonContainer;
    @BindView(R.id.dates_view_pager_holder)
    ViewGroup mDatesViewPagerHolder;
    @BindView(R.id.dates_view_pager)
    ViewPager mDatesViewPager;
    private DatesPagerAdapter mDatesPagerAdapter;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.SCHEDULED_JOBS;
    }

    @Override
    public void onResume()
    {
        bus.register(this);
        super.onResume();
        setActionBar(R.string.scheduled_jobs, false);
        if (!MainActivityFragment.clearingBackStack
                && mSelectedDay != null
                && mDatesPagerAdapter != null)
        {
            final int position = mDatesPagerAdapter.getItemPositionWithDate(mSelectedDay);
            if (position != -1)
            {
                mDatesViewPager.setCurrentItem(position);
            }
            final NewDateButton dateButton =
                    mDatesPagerAdapter.getDateButtonForDate(mSelectedDay);
            if (dateButton != null)
            {
                dateButton.select();
            }
        }
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    protected LinearLayout getDatesLayout()
    {
        return mScheduledJobsDatesScrollViewLayout;
    }

    @Override
    protected void initDateButtons()
    {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null && configuration.isNewDateScrollerEnabled())
        {
            mScheduledJobsDatesScrollView.setVisibility(View.GONE);
            mDatesPagerAdapter = new DatesPagerAdapter(getActivity(), this);
            mDatesViewPager.setAdapter(mDatesPagerAdapter);
        }
        else
        {
            mDatesViewPagerHolder.setVisibility(View.GONE);
            super.initDateButtons();
        }
    }

    protected int getFragmentResourceId()
    {
        return (R.layout.fragment_scheduled_bookings);
    }

    @Override
    protected BookingListView getBookingListView()
    {
        return mScheduledJobsListView;
    }

    @Override
    protected SwipeRefreshLayout getNoBookingsSwipeRefreshLayout()
    {
        return mNoScheduledBookingsLayout;
    }

    @Override
    protected void requestBookings(List<Date> dates, boolean useCachedIfPresent)
    {
        mBookingManager.requestScheduledBookings(dates, useCachedIfPresent);
    }

    @NonNull
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
    protected void beforeRequestBookings()
    {
        //Crash #476, some timing issue where butterknife hasn't injected yet
        //Ugly hack fix in lieu of restructuring code to track down root issue
        if (mFindMatchingJobsButtonContainer != null)
        {
            mFindMatchingJobsButtonContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass()
    {
        return ScheduledBookingElementView.class;
    }

    @Override
    protected String getBookingSourceName()
    {
        return SOURCE_SCHEDULED_JOBS_LIST;
    }

    @Nullable
    @Override
    protected DatesPagerAdapter getDatesPagerAdapter()
    {
        return mDatesPagerAdapter;
    }

    @Override
    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings)
    {
        super.afterDisplayBookings(bookingsForDay, dateOfBookings);
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.DateClicked(dateOfBookings, bookingsForDay.size())));
        bus.post(new HandyEvent.RequestProviderInfo());

        //Show "Find Jobs" buttons only if we're inside of our available bookings length range and we have no jobs

        int hoursSpanningAvailableBookings = DateTimeUtils.HOURS_IN_SIX_DAYS;
        if (configManager.getConfigurationResponse() != null)
        {
            hoursSpanningAvailableBookings =
                    configManager.getConfigurationResponse().getHoursSpanningAvailableBookings();
        }

        if (bookingsForDay.size() == 0 &&
                DateTimeUtils.isDateWithinXHoursFromNow(dateOfBookings, hoursSpanningAvailableBookings))
        {
            mFindJobsForDayButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mFindJobsForDayButton.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onReceiveProviderInfoSuccess(HandyEvent.ReceiveProviderInfoSuccess event)
    {
        if (mBookingsForSelectedDay == null || mSelectedDay == null) { return; }

        //show Find Matching Jobs buttons only if we're inside of our available bookings length range
        int hoursSpanningAvailableBookings = DateTimeUtils.HOURS_IN_SIX_DAYS;
        if (configManager.getConfigurationResponse() != null)
        {
            hoursSpanningAvailableBookings = configManager.getConfigurationResponse().getHoursSpanningAvailableBookings();
        }

        if (event.provider.isComplementaryJobsEnabled()
                && DateTimeUtils.isDateWithinXHoursFromNow(mSelectedDay, hoursSpanningAvailableBookings)
                && mBookingsForSelectedDay.size() == 1
                && !mBookingsForSelectedDay.get(0).isProxy()) // currently disable "Find Matching Jobs" for proxies
        {
            mFindMatchingJobsButtonContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.find_jobs_for_day_button)
    public void onFindJobsButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent((new ScheduledJobsLog.FindJobsSelected(mSelectedDay))));
        TransitionStyle transitionStyle = TransitionStyle.PAGE_TO_PAGE;
        long epochTime = mSelectedDay.getTime();
        //navigate back to available bookings for this day
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.AVAILABLE_JOBS, arguments, transitionStyle));
    }

    @OnClick(R.id.find_matching_jobs_button)
    public void onFindMatchingJobsButtonClicked()
    {
        Booking booking = mBookingsForSelectedDay.get(0);
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());

        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.COMPLEMENTARY_JOBS, arguments, TransitionStyle.SLIDE_UP, true));
    }

    @Override
    protected boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay)
    {
        return false;
    }

    @Override
    //All bookings not in the past on this page should cause the claimed indicator to appear
    protected boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay)
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
        handleBookingsRetrievalError(event, R.string.error_fetching_scheduled_jobs);
    }

    @Override
    public void onDateSelected(final Date date)
    {
        onDateClicked(date);
    }
}
