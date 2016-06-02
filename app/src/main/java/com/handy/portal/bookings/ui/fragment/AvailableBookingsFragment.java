package com.handy.portal.bookings.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingModalsManager;
import com.handy.portal.bookings.BookingModalsManager.BookingsForDaysAheadModalsManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.bookings.ui.element.BookingsAccessLockedView;
import com.handy.portal.bookings.ui.element.BookingsBannerView;
import com.handy.portal.bookings.ui.fragment.dialog.EarlyAccessTrialDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.JobAccessUnlockedDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ProRequestedJobsDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.helpcenter.constants.HelpCenterUrl;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.onboarding.ui.activity.GettingStartedActivity;
import com.handy.portal.ui.fragment.MainActivityFragment;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

import static com.handy.portal.bookings.BookingModalsManager.BookingsForDaysAheadModalsManager.BookingsForDaysAheadModalType;

public class AvailableBookingsFragment extends BookingsFragment<HandyEvent.ReceiveAvailableBookingsSuccess>
{
    private final static int DEFAULT_NUM_DAYS_SPANNING_AVAILABLE_BOOKINGS = 7; //includes Today
    private static final String SOURCE_AVAILABLE_JOBS_LIST = "available_jobs_list";

    @Bind(R.id.available_jobs_list_view)
    BookingListView mAvailableJobsListView;
    @Bind(R.id.available_bookings_dates_scroll_view_layout)
    LinearLayout mAvailableJobsDatesScrollViewLayout;
    @Bind(R.id.available_bookings_empty)
    SwipeRefreshLayout mNoAvailableBookingsLayout;
    @Bind(R.id.layout_job_access_locked)
    BookingsAccessLockedView mJobAccessLockedLayout;
    @Bind(R.id.toggle_available_job_notification)
    SwitchCompat mToggleAvailableJobNotification;

    BookingsBannerView mJobAccessUnlockedBannerLayout;

    @Inject
    BookingModalsManager mBookingModalsManager;

    private MenuItem mMenuSchedule;
    private MenuItem mMenuRequestedJobs;
    private ProviderProfile mProviderProfile;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.AVAILABLE_JOBS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mJobAccessUnlockedBannerLayout = new BookingsBannerView(getContext()).setContentVisible(false);
        getBookingListView().addHeaderView(mJobAccessUnlockedBannerLayout);
        //hacky: need to add the banner as booking list header view so it will scroll with the bookings list

        mJobAccessLockedLayout.setKeepRateInfoButtonClickListener(new View.OnClickListener()
        {
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

        bus.post(new ProfileEvent.RequestProviderProfile(false));

        if (!MainActivityFragment.clearingBackStack)
        {
            if (shouldShowAvailableBookingsToggle())
            {
                mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
            }

            setLateDispatchOptInToggleListener();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_available_bookings, menu);
        mMenuSchedule = menu.findItem(R.id.action_initial_jobs);
        mMenuRequestedJobs = menu.findItem(R.id.action_requested_jobs);

        updateMenuItems();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_initial_jobs:
                startActivity(new Intent(getContext(), GettingStartedActivity.class));
                return true;
            case R.id.action_requested_jobs:
                if(getChildFragmentManager().findFragmentByTag(ProRequestedJobsDialogFragment.FRAGMENT_TAG) == null)
                {
                    ProRequestedJobsDialogFragment fragment = ProRequestedJobsDialogFragment.newInstance();
                    FragmentUtils.safeLaunchDialogFragment(fragment, this, ProRequestedJobsDialogFragment.FRAGMENT_TAG);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            daysSpanningAvailableBookings = configManager.getConfigurationResponse()
                    .getNumberOfDaysForAvailableJobs();
        }
        return daysSpanningAvailableBookings;
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
        super.afterDisplayBookings(bookingsForDay, dateOfBookings);
    }

    private void updateMenuItems()
    {

        if (mMenuSchedule != null)
        {
            if (mProviderProfile != null
                    && mProviderProfile.getPerformanceInfo() != null
                    && mProviderProfile.getPerformanceInfo().getTotalJobsCount() <= 0)
            {
                mMenuSchedule.setVisible(true);
            }
            else
            {
                mMenuSchedule.setVisible(false);
            }
        }

        if(mMenuRequestedJobs != null)
        {
            if(mConfigManager.getConfigurationResponse() != null
                    && mConfigManager.getConfigurationResponse().isPendingRequestsInboxEnabled())
            {
                mMenuRequestedJobs.setVisible(true);
            }
            else
            {
                mMenuRequestedJobs.setVisible(false);
            }
        }
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        //show the menu option if the pro haven't claimed jobs before.
        mProviderProfile = event.providerProfile;
        updateMenuItems();
    }

    /**
     * TODO needs better naming
     * populates the booking content view. may show a locked screen, banners and/or the booking list
     *
     * @param bookingsWrapper
     * @param dateOfBookings
     */
    @Override
    protected void displayBookings(@NonNull final BookingsWrapper bookingsWrapper, @NonNull final Date dateOfBookings)
    {
        super.displayBookings(bookingsWrapper, dateOfBookings);
        showBookingsForDayModalsAndBannersIfNecessary(bookingsWrapper, dateOfBookings);
    }

    /**
     * shows any popups based on the given bookings/date selected
     * <p/>
     * shows any banners based on the given bookings/date selected
     *
     * @param bookingsWrapper
     * @param dateOfBookings
     */
    private void showBookingsForDayModalsAndBannersIfNecessary(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date dateOfBookings) //todo rename
    {
        mJobAccessUnlockedBannerLayout.setContentVisible(false);
        mJobAccessLockedLayout.setVisibility(View.GONE);
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = bookingsWrapper.getPriorityAccessInfo();
        if (priorityAccessInfo != null)
        {
            BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus bookingsForDayPriorityAccessStatus = priorityAccessInfo.getBookingsForDayStatus();
            if (bookingsForDayPriorityAccessStatus != null)
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
                        break;
                }
            }
        }
    }

    private void showBookingsLayoutForEarlyAccessTrialPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo,
                                                                     @NonNull Date dateOfBookings)
    {
        BookingsForDaysAheadModalsManager bookingsForDaysAheadModalsManager
                = mBookingModalsManager.getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_TRIAL_MODAL, dateOfBookings);

        //show the banner
        String title = getString(R.string.job_access_early_access_banner_title);
        String subtitle = getString(R.string.job_access_early_access_banner_subtitle_formatted,
                priorityAccessInfo.getMinimumKeepRate());
        mJobAccessUnlockedBannerLayout
                .setLeftDrawable(ContextCompat.getDrawable(getContext(), R.drawable.img_unlocked_trial_banner))
                .setTitleText(title)
                .setDescriptionText(subtitle)
                .setContentVisible(true);

        //only show the modal if it wasn't shown before
        if (!bookingsForDaysAheadModalsManager.bookingsForDayModalPreviouslyShown()
                && getActivity().getSupportFragmentManager().findFragmentByTag(EarlyAccessTrialDialogFragment.FRAGMENT_TAG) == null)
        {
            EarlyAccessTrialDialogFragment earlyAccessTrialDialogFragment =
                    EarlyAccessTrialDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(earlyAccessTrialDialogFragment, getActivity(), EarlyAccessTrialDialogFragment.FRAGMENT_TAG);
            bookingsForDaysAheadModalsManager.onBookingsForDayModalShown();
        }
    }

    private void showBookingsLayoutForUnlockedPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo,
                                                             @NonNull Date dateOfBookings)
    {
        BookingsForDaysAheadModalsManager bookingsForDaysAheadModalsManager
                = mBookingModalsManager.getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_MODAL, dateOfBookings);

        //show the banner
        String title = getString(R.string.job_access_unlocked_banner_title_formatted,
                priorityAccessInfo.getMinimumKeepRate());
        String subtitle = getString(R.string.job_access_unlocked_banner_subtitle_formatted,
                priorityAccessInfo.getCurrentKeepRate());
        mJobAccessUnlockedBannerLayout
                .setLeftDrawable(ContextCompat.getDrawable(getContext(), R.drawable.img_unlocked_banner))
                .setTitleText(title)
                .setDescriptionText(subtitle)
                .setContentVisible(true);

        //only show the modal if it wasn't shown before
        if (!bookingsForDaysAheadModalsManager.bookingsForDayModalPreviouslyShown()
                && getActivity().getSupportFragmentManager().findFragmentByTag(JobAccessUnlockedDialogFragment.FRAGMENT_TAG) == null)
        {
            JobAccessUnlockedDialogFragment jobAccessUnlockedDialogFragment =
                    JobAccessUnlockedDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(jobAccessUnlockedDialogFragment, getActivity(), JobAccessUnlockedDialogFragment.FRAGMENT_TAG);
            bookingsForDaysAheadModalsManager.onBookingsForDayModalShown();
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

        if (event.bookingsWrapper == null || event.day == null)
        {
            Crashlytics.logException(new Exception("received available bookings event with null bookings wrapper or day"));
            return;
        }

        resetBookingsForDayUnlockedModalsShownIfNecessary(event.bookingsWrapper, event.day);
    }

    /**
     * resets the modal shown status for the given day if the priority access status
     * is not explicitly "unlocked" or "new_pro", so is either not present or "locked"
     *
     * @param bookingsWrapper
     * @param date
     */
    private void resetBookingsForDayUnlockedModalsShownIfNecessary(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date date)
    {
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = bookingsWrapper.getPriorityAccessInfo();
        if (priorityAccessInfo == null
                || priorityAccessInfo.getBookingsForDayStatus() == null
                || priorityAccessInfo.getBookingsForDayStatus() ==
                BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus.LOCKED)
        {
            mBookingModalsManager
                    .getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_MODAL, date)
                    .resetModalShownStatus();

            mBookingModalsManager
                    .getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_TRIAL_MODAL, date)
                    .resetModalShownStatus();
        }
    }
}
