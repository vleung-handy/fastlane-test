package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingModalsManager;
import com.handy.portal.bookings.manager.BookingModalsManager.BookingsForDaysAheadModalsManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.bookings.ui.element.BookingsAccessLockedView;
import com.handy.portal.bookings.ui.element.BookingsBannerView;
import com.handy.portal.bookings.ui.fragment.dialog.EarlyAccessTrialDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.JobAccessUnlockedDialogFragment;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.ProviderSettingsEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.handy.portal.bookings.manager.BookingModalsManager.BookingsForDaysAheadModalsManager.BookingsForDaysAheadModalType;

public class AvailableBookingsFragment extends BookingsFragment<HandyEvent.ReceiveAvailableBookingsSuccess> {
    public final static int DEFAULT_NUM_DAYS_FOR_AVAILABLE_JOBS = DateTimeUtils.DAYS_IN_A_WEEK; //includes Today
    private static final String SOURCE_AVAILABLE_JOBS_LIST = "available_jobs_list";

    @BindView(R.id.available_jobs_list_view)
    BookingListView mAvailableJobsListView;
    @BindView(R.id.available_bookings_dates_scroll_view_layout)
    LinearLayout mAvailableJobsDatesScrollViewLayout;
    @BindView(R.id.available_bookings_empty)
    SwipeRefreshLayout mNoAvailableBookingsLayout;
    @BindView(R.id.layout_job_access_locked)
    BookingsAccessLockedView mJobAccessLockedLayout;
    @BindView(R.id.toggle_available_job_notification)
    SwitchCompat mToggleAvailableJobNotification;

    BookingsBannerView mJobAccessUnlockedBannerLayout;

    @Inject
    BookingModalsManager mBookingModalsManager;
    @Inject
    PageNavigationManager mNavigationManager;

    private boolean mFastForwardToFirst;
    private Date mFirstAvailableDate;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.AVAILABLE_JOBS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mJobAccessUnlockedBannerLayout = new BookingsBannerView(getContext()).setContentVisible(false);
        getBookingListView().addHeaderView(mJobAccessUnlockedBannerLayout);
        //hacky: need to add the banner as booking list header view so it will scroll with the bookings list

        mJobAccessLockedLayout.setKeepRateInfoButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                goToHelpCenter(HelpCenterConstants.KEEP_RATE_INFO_PATH);
            }
        });

        //check to see if there is a deep link flag for specifying fast forwarding to the first
        //available job
        if (getArguments() != null) {
            String firstAvailString = getArguments().getString(DeeplinkUtils.DEEP_LINK_PARAM_FIRST_AVAILABLE, null);
            try {
                mFastForwardToFirst = Boolean.parseBoolean(firstAvailString);
            }
            catch (Exception e) {
                //no need to do anything on error.
            }
        }
    }

    @Override
    public void onResume() {
        bus.register(this);
        super.onResume();
        setActionBar(R.string.available_jobs, false);

        if (!MainActivity.clearingBackStack) {
            if (shouldShowAvailableBookingsToggle()) {
                mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
            }
            setLateDispatchOptInToggleListener();
        }
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_available_bookings, menu);
    }

    protected BookingListView getBookingListView() {
        return mAvailableJobsListView;
    }

    @Override
    protected SwipeRefreshLayout getNoBookingsSwipeRefreshLayout() {
        return mNoAvailableBookingsLayout;
    }

    protected LinearLayout getDatesLayout() {
        return mAvailableJobsDatesScrollViewLayout;
    }

    @Override
    protected void requestBookings(List<Date> dates, boolean useCachedIfPresent) {
        mBookingManager.requestAvailableBookings(dates, useCachedIfPresent);
    }

    @Override
    protected int getFragmentResourceId() {
        return (R.layout.fragment_available_bookings);
    }

    @NonNull
    @Override
    protected String getTrackingType() {
        return getString(R.string.available_job);
    }

    @Override
    protected boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay) {
        //Bookings are sorted such that the requested bookings show up first so we just need to check the first one
        return bookingsForDay.size() > 0 && bookingsForDay.get(0).isRequested();
    }

    private void goToHelpCenter(final String helpCenterRedirectPath) {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, helpCenterRedirectPath);
        mNavigationManager.navigateToPage(getFragmentManager(), MainViewPage.HELP_WEBVIEW,
                arguments, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @Override
    protected boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay) {
        return false;
    }

    @Override
    protected int numberOfDaysToDisplay() {
        int daysSpanningAvailableBookings = DEFAULT_NUM_DAYS_FOR_AVAILABLE_JOBS;
        if (configManager.getConfigurationResponse() != null) {
            daysSpanningAvailableBookings = configManager.getConfigurationResponse()
                    .getNumberOfDaysForAvailableJobs();
        }
        return daysSpanningAvailableBookings;
    }

    @Override
    protected void beforeRequestBookings() {
        if (shouldShowAvailableBookingsToggle()) {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
        else {
            mToggleAvailableJobNotification.setVisibility(View.GONE);
        }
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass() {
        return AvailableBookingElementView.class;
    }

    @Override
    protected String getBookingSourceName() {
        return SOURCE_AVAILABLE_JOBS_LIST;
    }

    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings) {
        super.afterDisplayBookings(bookingsForDay, dateOfBookings);
    }

    /**
     * TODO needs better naming
     * populates the booking content view. may show a locked screen, banners and/or the booking list
     *
     * @param bookingsWrapper
     * @param dateOfBookings
     */
    @Override
    protected void displayBookings(@NonNull final BookingsWrapper bookingsWrapper, @NonNull final Date dateOfBookings) {
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
        if (priorityAccessInfo != null) {
            BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus bookingsForDayPriorityAccessStatus = priorityAccessInfo.getBookingsForDayStatus();
            if (bookingsForDayPriorityAccessStatus != null) {
                switch (bookingsForDayPriorityAccessStatus) {
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
                                                                     @NonNull Date dateOfBookings) {
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
                && getActivity().getSupportFragmentManager().findFragmentByTag(EarlyAccessTrialDialogFragment.FRAGMENT_TAG) == null) {
            EarlyAccessTrialDialogFragment earlyAccessTrialDialogFragment =
                    EarlyAccessTrialDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(earlyAccessTrialDialogFragment, getActivity(), EarlyAccessTrialDialogFragment.FRAGMENT_TAG);
            bookingsForDaysAheadModalsManager.onBookingsForDayModalShown();
        }
    }

    private void showBookingsLayoutForUnlockedPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo,
                                                             @NonNull Date dateOfBookings) {
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
                && getActivity().getSupportFragmentManager().findFragmentByTag(JobAccessUnlockedDialogFragment.FRAGMENT_TAG) == null) {
            JobAccessUnlockedDialogFragment jobAccessUnlockedDialogFragment =
                    JobAccessUnlockedDialogFragment.newInstance(priorityAccessInfo);
            FragmentUtils.safeLaunchDialogFragment(jobAccessUnlockedDialogFragment, getActivity(), JobAccessUnlockedDialogFragment.FRAGMENT_TAG);
            bookingsForDaysAheadModalsManager.onBookingsForDayModalShown();
        }
    }

    private void showBookingsLayoutForLockedPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo) {
        mJobAccessLockedLayout
                .setTitleText(priorityAccessInfo.getMessageTitle())
                .setDescriptionText(priorityAccessInfo.getMessageDescription());

        getNoBookingsSwipeRefreshLayout().setVisibility(View.GONE);
        mJobAccessLockedLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveAvailableBookingsSuccess event) {

        /*
            If we get a date that is smaller than what we've seen so far, then navigate to that
            page via onCellClicked method. Since this booking is already loaded, it'll fetch
            from cache, no extra service call.
         */
        if (mFastForwardToFirst
                && event.bookingsWrapper.getBookings() != null
                && !event.bookingsWrapper.getBookings().isEmpty()) {
            if (mFirstAvailableDate == null) {
                mFirstAvailableDate = event.bookingsWrapper.getDate();
                onDateClicked(mFirstAvailableDate);
            }
            else if (mFirstAvailableDate.compareTo(event.bookingsWrapper.getDate()) > 0) {
                mFirstAvailableDate = event.bookingsWrapper.getDate();
                onDateClicked(mFirstAvailableDate);
            }
        }

        handleBookingsRetrieved(event);
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveAvailableBookingsError event) {
        handleBookingsRetrievalError(event, R.string.error_fetching_available_jobs);
    }

    @Subscribe
    public void onReceiveProviderSettingsSuccess(ProviderSettingsEvent.ReceiveProviderSettingsSuccess event) {
        mProviderSettings = event.getProviderSettings().clone();
        mToggleAvailableJobNotification.setChecked(mProviderSettings.hasOptedInToLateDispatchNotifications());
        if (shouldShowAvailableBookingsToggle()) {
            mToggleAvailableJobNotification.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe
    public void onReceiveProviderSettingsError(ProviderSettingsEvent.ReceiveProviderSettingsError event) {
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateSuccess(ProviderSettingsEvent.ReceiveProviderSettingsUpdateSuccess event) {
        mProviderSettings = event.getProviderSettings().clone();
        if (!mPrefsManager.getSecureBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, false) &&
                mProviderSettings.hasOptedInToLateDispatchNotifications()) {
            mPrefsManager.setSecureBoolean(PrefsKey.SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED, true);
            Snackbar snackbar = Snackbar
                    .make(mBookingsContent, R.string.notify_available_jobs_update_intro_success, Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Subscribe
    public void onReceiveProviderSettingsUpdateError(ProviderSettingsEvent.ReceiveProviderSettingsUpdateError event) {
        if (mProviderSettings != null) {
            boolean optedIn = !mProviderSettings.hasOptedInToLateDispatchNotifications();
            mProviderSettings.setLateDispatchOptIn(optedIn);
            mToggleAvailableJobNotification.setChecked(optedIn);
        }
        else {
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());
        }

        Snackbar snackbar = Snackbar
                .make(mBookingsContent, R.string.notify_available_jobs_update_error, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private boolean shouldShowAvailableBookingsToggle() {
        return mSelectedDay != null &&
                DateTimeUtils.isToday(mSelectedDay) &&
                getConfigurationResponse() != null &&
                getConfigurationResponse().shouldShowLateDispatchOptIn() &&
                mProviderSettings != null;

    }

    private ConfigurationResponse getConfigurationResponse() {
        return mConfigManager.getConfigurationResponse();
    }

    private void setLateDispatchOptInToggleListener() {
        mToggleAvailableJobNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (mProviderSettings == null) {
                    bus.post(new ProviderSettingsEvent.RequestProviderSettings());
                }
                else if (mProviderSettings.hasOptedInToLateDispatchNotifications() != isChecked) {
                    mProviderSettings.setLateDispatchOptIn(isChecked);
                    bus.post(new ProviderSettingsEvent.RequestProviderSettingsUpdate(mProviderSettings));
                }
            }
        });
    }

    @Override
    protected void handleBookingsRetrieved(final HandyEvent.ReceiveAvailableBookingsSuccess event) {
        super.handleBookingsRetrieved(event);

        if (event.bookingsWrapper == null || event.day == null) {
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
    private void resetBookingsForDayUnlockedModalsShownIfNecessary(@NonNull BookingsWrapper bookingsWrapper, @NonNull Date date) {
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = bookingsWrapper.getPriorityAccessInfo();
        if (priorityAccessInfo == null
                || priorityAccessInfo.getBookingsForDayStatus() == null
                || priorityAccessInfo.getBookingsForDayStatus() ==
                BookingsWrapper.PriorityAccessInfo.BookingsForDayPriorityAccessStatus.LOCKED) {
            mBookingModalsManager
                    .getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_MODAL, date)
                    .resetModalShownStatus();

            mBookingModalsManager
                    .getBookingsForDayModalsManager(BookingsForDaysAheadModalType.UNLOCKED_TRIAL_MODAL, date)
                    .resetModalShownStatus();
        }
    }
}
