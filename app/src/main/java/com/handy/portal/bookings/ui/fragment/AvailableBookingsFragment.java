package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingModalsManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.bookings.ui.element.BookingsAccessLockedView;
import com.handy.portal.bookings.ui.element.BookingsBannerView;
import com.handy.portal.bookings.ui.fragment.dialog.EarlyAccessTrialDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.JobAccessUnlockedDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.helpcenter.constants.HelpCenterUrl;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.FragmentUtils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

import static com.handy.portal.bookings.BookingModalsManager.BookingsForDayModalsManager.BookingsForDayModalType.BOOKINGS_FOR_DAY_UNLOCKED_MODAL;
import static com.handy.portal.bookings.BookingModalsManager.BookingsForDayModalsManager.BookingsForDayModalType.BOOKINGS_FOR_DAY_UNLOCKED_TRIAL_MODAL;

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
    @Bind(R.id.layout_job_access_locked)
    BookingsAccessLockedView mJobAccessLockedLayout;

    BookingsBannerView mJobAccessUnlockedBannerLayout;
    @Bind(R.id.toggle_available_job_notification)
    SwitchCompat mToggleAvailableJobNotification;

    @Inject
    BookingModalsManager mBookingModalsManager;

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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mJobAccessUnlockedBannerLayout = new BookingsBannerView(getContext())
                .setLeftDrawable(ContextCompat.getDrawable(getContext(), R.drawable.img_unlocked_banner))
                .setContentVisible(false);
        getBookingListView().addHeaderView(mJobAccessUnlockedBannerLayout);
        //hacky: need to add the banner as booking list header view so it will scroll with the bookings list

        mJobAccessLockedLayout.setKeepRateInfoButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                goToHelpCenter(HelpCenterUrl.KEEP_RATE_INFO_REDIRECT_URL);
            }
        });
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

    private void goToHelpCenter(final String helpCenterRedirectPath)
    {
        //don't ever need to support native help center again so ignore the config response
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, helpCenterRedirectPath);
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.HELP_WEBVIEW, arguments, true));
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

    /**
     * TODO needs better naming
     * populates the booking content view. may show a locked screen, banners and/or the booking list
     * @param bookingsWrapper
     * @param dateOfBookings
     */
    @Override
    protected void displayBookings(@NonNull final BookingsWrapper bookingsWrapper, @NonNull final Date dateOfBookings)
    {
        boolean shouldDisplayBookingsList = showBookingsForDayModalsAndBannersIfNecessary(bookingsWrapper, dateOfBookings);
        if(shouldDisplayBookingsList)
        {
            super.displayBookings(bookingsWrapper, dateOfBookings);
        }
    }

    /**
     * shows any popups based on the given bookings/date selected
     *
     * shows any banners based on the given bookings/date selected
     *
     * @param bookingsWrapper
     * @param dateOfBookings
     * @return true if the bookings list should still be shown. false otherwise
     * TODO how to make this clearer?
     */
    private boolean showBookingsForDayModalsAndBannersIfNecessary(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date dateOfBookings) //todo rename
    {
        mJobAccessUnlockedBannerLayout.setContentVisible(false);
        mJobAccessLockedLayout.setVisibility(View.GONE);
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = bookingsWrapper.getPriorityAccessInfo();
        if(priorityAccessInfo != null)
        {
            BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus bookingsForDayPriorityAccessStatus = priorityAccessInfo.getBookingsForDayStatus();
            if(bookingsForDayPriorityAccessStatus != null)
            {
                switch (bookingsForDayPriorityAccessStatus)
                {
                    case NEW_PRO:
                        showBookingsLayoutForEarlyAccessTrialPriorityAccess(priorityAccessInfo, dateOfBookings);
                        break;
                    case UNLOCKED:
                        showBookingsLayoutForUnlockedPriorityAccess(priorityAccessInfo, dateOfBookings);
                        break;
                    case LOCKED:
                        showBookingsLayoutForLockedPriorityAccess(priorityAccessInfo);
                        return false;
                }
            }
        }
        return true;
    }

    private void showBookingsLayoutForEarlyAccessTrialPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo,
                                                                     @NonNull Date dateOfBookings)
    {
        BookingModalsManager.BookingsForDayModalsManager bookingsForDayModalsManager
                = mBookingModalsManager.getBookingsForDayModalsManager(BOOKINGS_FOR_DAY_UNLOCKED_TRIAL_MODAL, dateOfBookings);
        if(bookingsForDayModalsManager.bookingsForDayModalPreviouslyShown())
        {
            String title = getString(R.string.job_access_early_access_banner_title);
            String subtitle = getString(R.string.job_access_early_access_banner_subtitle_formatted,
                    priorityAccessInfo.getMinimumKeepRate());
            mJobAccessUnlockedBannerLayout
                    .setTitleText(title)
                    .setDescriptionText(subtitle)
                    .setContentVisible(true);
        }
        else if(getActivity().getSupportFragmentManager().findFragmentByTag(EarlyAccessTrialDialogFragment.FRAGMENT_TAG) == null)
        {
            EarlyAccessTrialDialogFragment earlyAccessTrialDialogFragment =
                    EarlyAccessTrialDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(earlyAccessTrialDialogFragment, getActivity(), EarlyAccessTrialDialogFragment.FRAGMENT_TAG);
            bookingsForDayModalsManager.onBookingsForDayModalShown();
        }
    }

    private void showBookingsLayoutForUnlockedPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo,
                                                             @NonNull Date dateOfBookings)
    {
        BookingModalsManager.BookingsForDayModalsManager bookingsForDayModalsManager
                = mBookingModalsManager.getBookingsForDayModalsManager(BOOKINGS_FOR_DAY_UNLOCKED_MODAL, dateOfBookings);
        if(bookingsForDayModalsManager.bookingsForDayModalPreviouslyShown())
        {
            String title = getString(R.string.job_access_unlocked_banner_title_formatted,
                    priorityAccessInfo.getMinimumKeepRate());
            String subtitle = getString(R.string.job_access_unlocked_banner_subtitle_formatted,
                    priorityAccessInfo.getCurrentKeepRate());
            mJobAccessUnlockedBannerLayout
                    .setTitleText(title)
                    .setDescriptionText(subtitle)
                    .setContentVisible(true);
        }
        else if(getActivity().getSupportFragmentManager().findFragmentByTag(JobAccessUnlockedDialogFragment.FRAGMENT_TAG) == null)
        {
            JobAccessUnlockedDialogFragment jobAccessUnlockedDialogFragment =
                    JobAccessUnlockedDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(jobAccessUnlockedDialogFragment, getActivity(), JobAccessUnlockedDialogFragment.FRAGMENT_TAG);
            bookingsForDayModalsManager.onBookingsForDayModalShown();
        }
    }

    private void showBookingsLayoutForLockedPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo)
    {
        mJobAccessLockedLayout
                .setTitleText(priorityAccessInfo.getMessageTitle())
                .setDescriptionText(priorityAccessInfo.getMessageDescription());

        getNoBookingsSwipeRefreshLayout().setVisibility(View.GONE);
        mJobAccessLockedLayout.setVisibility(View.VISIBLE);
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

    @Override
    protected void handleBookingsRetrieved(final HandyEvent.ReceiveAvailableBookingsSuccess event)
    {
        super.handleBookingsRetrieved(event);

        if(event.bookingsWrapper == null || event.day == null)
        {
            Crashlytics.logException(new Exception("received available bookings event with null bookings wrapper or day"));
            return;
        }

        resetBookingsForDayUnlockedModalShownIfNecessary(event.bookingsWrapper, event.day);
        setDateButtonPropertiesForDay(event.bookingsWrapper.getPriorityAccessInfo(), event.day);
    }

    private void resetBookingsForDayUnlockedModalShownIfNecessary(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date date)
    {
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = bookingsWrapper.getPriorityAccessInfo();
        if(priorityAccessInfo == null || priorityAccessInfo.getBookingsForDayStatus() == null) return;
        if(priorityAccessInfo.getBookingsForDayStatus() == BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus.LOCKED)
        {
            mBookingModalsManager
                    .getBookingsForDayModalsManager(BOOKINGS_FOR_DAY_UNLOCKED_MODAL, date)
                    .resetModalShownStatus();

            mBookingModalsManager
                    .getBookingsForDayModalsManager(BOOKINGS_FOR_DAY_UNLOCKED_TRIAL_MODAL, date)
                    .resetModalShownStatus();
        }
    }

    /**
     * modifies the style of the date button for the given day if needed
     *
     * @param priorityAccessInfo
     * @param day
     */
    private void setDateButtonPropertiesForDay(@Nullable BookingsWrapper.PriorityAccessInfo priorityAccessInfo, Date day)
    {
        if(priorityAccessInfo != null)
        {
            BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus priorityAccessBookingsForDayPriorityAccessStatus = priorityAccessInfo.getBookingsForDayStatus();
            DateButtonView dateButtonView = mDateDateButtonViewMap.get(day);

            if(priorityAccessBookingsForDayPriorityAccessStatus == null || dateButtonView == null) return;

            switch(priorityAccessBookingsForDayPriorityAccessStatus)
            {
                case LOCKED:
                    dateButtonView.setAlpha(0.5f);
                    break;
                //more cases to handle?

            }


        }
    }
}
