package com.handy.portal.ui.fragment.bookings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class AvailableBookingsFragment extends BookingsFragment<HandyEvent.ReceiveAvailableBookingsSuccess>
{
    private final static int DEFAULT_NUM_DAYS_SPANNING_AVAILABLE_BOOKINGS = 6;
    private static final String SOURCE_AVAILABLE_JOBS_LIST = "available_jobs_list";

    @Bind(R.id.available_jobs_list_view)
    BookingListView mAvailableJobsListView;
    @Bind(R.id.available_bookings_dates_scroll_view_layout)
    LinearLayout mAvailableJobsDatesScrollViewLayout;
    @Bind(R.id.available_bookings_empty)
    SwipeRefreshLayout mNoAvailableBookingsLayout;
    @Bind(R.id.toggle_available_job_notification)
    SwitchCompat mToggleAvailableJobNotification;
    private String mMessage;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.AVAILABLE_JOBS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMessage = getArguments().getString(BundleKeys.MESSAGE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.available_jobs, false);

        if (!MainActivityFragment.clearingBackStack)
        {
            if (shouldShowAvailableBookingsToggle())
            {
                mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
            }

            setLateDispatchOptInToggleListener();
        }
    }

    protected BookingListView getBookingListView()
    {
        return mAvailableJobsListView;
    }

    @Override
    protected SwipeRefreshLayout getNoBookingsSwipeRefreshLayout()
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
        int daysSpanningAvailableBookings = DEFAULT_NUM_DAYS_SPANNING_AVAILABLE_BOOKINGS;
        if (configManager.getConfigurationResponse() != null)
        {
            daysSpanningAvailableBookings = configManager.getConfigurationResponse().getHoursSpanningAvailableBookings() / DateTimeUtils.HOURS_IN_DAY;
        }
        return daysSpanningAvailableBookings + 1; // plus today
    }

    @Override
    protected void beforeRequestBookings()
    {
        if (shouldShowAvailableBookingsToggle())
        {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
        else
        {
            mToggleAvailableJobNotification.setVisibility(View.GONE);
        }
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass()
    {
        return AvailableBookingElementView.class;
    }

    @Override
    protected String getBookingSourceName()
    {
        return SOURCE_AVAILABLE_JOBS_LIST;
    }

    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings)
    {
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.DateClicked(dateOfBookings, bookingsForDay.size())));

        if (mMessage != null)
        {
            Snackbar.make(
                    mBookingsContent,
                    mMessage,
                    Snackbar.LENGTH_LONG
            ).show();
            if (mMessage.equals(getString(R.string.job_no_longer_available)))
            {
                final Bundle extras = getArguments().getBundle(BundleKeys.EXTRAS);
                bus.post(new LogEvent.AddLogEvent(
                        new AvailableJobsLog.UnavailableJobNoticeShown(extras)));
            }
            mMessage = null; // this is a one-off
        }
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
        mProviderSettings = event.getProviderSettings().clone();
        mToggleAvailableJobNotification.setChecked(mProviderSettings.hasOptedInToLateDispatchNotifications());
        if (shouldShowAvailableBookingsToggle())
        {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe
    public void onReceiveProviderSettingsError(ProviderSettingsEvent.ReceiveProviderSettingsError event)
    {
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateSuccess(ProviderSettingsEvent.ReceiveProviderSettingsUpdateSuccess event)
    {
        mProviderSettings = event.getProviderSettings().clone();
        if (!mPrefsManager.getBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, false) &&
                mProviderSettings.hasOptedInToLateDispatchNotifications())
        {
            mPrefsManager.setBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, true);
            Snackbar snackbar = Snackbar
                    .make(mBookingsContent, R.string.notify_available_jobs_update_intro_success, Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateError(ProviderSettingsEvent.ReceiveProviderSettingsUpdateError event)
    {
        if (mProviderSettings != null)
        {
            boolean optedIn = !mProviderSettings.hasOptedInToLateDispatchNotifications();
            mProviderSettings.setLateDispatchOptIn(optedIn);
            mToggleAvailableJobNotification.setChecked(optedIn);
        }
        else
        {
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());
        }

        Snackbar snackbar = Snackbar
                .make(mBookingsContent, R.string.notify_available_jobs_update_error, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private boolean shouldShowAvailableBookingsToggle()
    {
        return mSelectedDay != null &&
                DateTimeUtils.isToday(mSelectedDay) &&
                getConfigurationResponse() != null &&
                getConfigurationResponse().shouldShowLateDispatchOptIn() &&
                mProviderSettings != null;

    }

    private ConfigurationResponse getConfigurationResponse()
    {
        return mConfigManager.getConfigurationResponse();
    }

    private void setLateDispatchOptInToggleListener()
    {
        mToggleAvailableJobNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
            {
                if (mProviderSettings == null)
                {
                    bus.post(new ProviderSettingsEvent.RequestProviderSettings());
                }
                else if (mProviderSettings.hasOptedInToLateDispatchNotifications() != isChecked)
                {
                    mProviderSettings.setLateDispatchOptIn(isChecked);
                    bus.post(new ProviderSettingsEvent.RequestProviderSettingsUpdate(mProviderSettings));
                }
            }
        });
    }
}
