package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.BookingListView;
import com.handy.portal.bookings.ui.element.NewDateButton;
import com.handy.portal.bookings.ui.element.NewDateButtonGroup;
import com.handy.portal.bookings.ui.element.ScheduledBookingElementView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.ProviderAvailability;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.AvailableHoursView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;

public class ScheduledBookingsFragment extends BookingsFragment<HandyEvent.ReceiveScheduledBookingsSuccess>
        implements DatesPagerAdapter.DateSelectedListener {
    private static final int NEXT_WEEK_AVAILABILITY_INDEX = 1;
    @Inject
    ProviderManager mProviderManager;

    private static final String SOURCE_SCHEDULED_JOBS_LIST = "scheduled_jobs_list";
    @BindView(R.id.scheduled_jobs_list_view)
    BookingListView mScheduledJobsListView;
    @BindView(R.id.scheduled_bookings_dates_scroll_view)
    ViewGroup mScheduledJobsDatesScrollView;
    @BindView(R.id.scheduled_bookings_dates_scroll_view_layout)
    LinearLayout mScheduledJobsDatesScrollViewLayout;
    @BindView(R.id.scheduled_bookings_empty)
    SwipeRefreshLayout mNoScheduledBookingsLayout;
    @BindView(R.id.dates_view_pager_holder)
    ViewGroup mDatesViewPagerHolder;
    @BindView(R.id.dates_view_pager)
    ViewPager mDatesViewPager;
    @BindView(R.id.available_hours_view)
    AvailableHoursView mAvailableHoursView;
    @BindView(R.id.set_available_hours_banner)
    View mSetAvailableHoursBanner;
    @BindDrawable(R.drawable.ic_available_hours)
    Drawable mAvailableHoursIcon;
    @BindDrawable(R.drawable.ic_available_hours_pending)
    Drawable mAvailableHoursPendingIcon;
    private DatesPagerAdapter mDatesPagerAdapter;
    private int mLastDatesPosition;
    private ViewPager.OnPageChangeListener mDatesPageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                                           final int positionOffsetPixels) {
                    // do nothing
                }

                @Override
                public void onPageSelected(final int position) {
                    final NewDateButtonGroup dateButtonGroup =
                            mDatesPagerAdapter.getItemAt(position);
                    final NewDateButton dateButton;
                    if (mLastDatesPosition > position) {
                        dateButton =
                                dateButtonGroup.getLastEnabledDateButton();
                    }
                    else {
                        dateButton =
                                dateButtonGroup.getFirstEnabledDateButton();
                    }
                    if (dateButton != null) {
                        dateButton.select();
                    }
                    mLastDatesPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(final int state) {
                    // do nothing
                }
            };
    private ProviderAvailability mProviderAvailability;
    private DailyAvailabilityTimeline mAvailabilityForSelectedDay;
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.SCHEDULED_JOBS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null
                && getArguments().getBoolean(DeeplinkUtils.DEEP_LINK_AVAILABLE_HOURS, false)) {
            navigateToEditWeeklyAvailableHours();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (isAvailableHoursEnabled()) {
            inflater.inflate(R.menu.menu_scheduled_bookings, menu);
            updateAvailableHoursMenuItem();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_available_hours:
                navigateToEditWeeklyAvailableHours();
                return true;
            default:
                return false;
        }
    }

    private void navigateToEditWeeklyAvailableHours() {
        if (mSelectedDay == null) {
            //it'll only be null if we're coming in here directly from a deeplink, then we can
            //use today as our selected day.
            mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date());
        }
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.SetWeekAvailabilitySelected(
                DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(mSelectedDay))));
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.FLOW_CONTEXT, EventContext.AVAILABILITY);
        arguments.putSerializable(BundleKeys.PROVIDER_AVAILABILITY, mProviderAvailability);
        arguments.putSerializable(BundleKeys.PROVIDER_AVAILABILITY_CACHE,
                mUpdatedAvailabilityTimelines);
        arguments.putSerializable(BundleKeys.SHOULD_DEFAULT_TO_NEXT_WEEK,
                !hasAvailableHoursForNextWeek());
        final NavigationEvent.NavigateToPage navigationEvent =
                new NavigationEvent.NavigateToPage(MainViewPage.EDIT_WEEKLY_AVAILABLE_HOURS,
                        arguments, true);
        navigationEvent.setReturnFragment(this, RequestCode.EDIT_HOURS);
        bus.post(navigationEvent);
    }

    @Override
    public void onResume() {
        bus.register(this);
        super.onResume();
        setActionBar(R.string.scheduled_jobs, false);
        if (!MainActivity.clearingBackStack) {
            if (mSelectedDay != null && mDatesPagerAdapter != null) {
                final int position = mDatesPagerAdapter.getItemPositionWithDate(mSelectedDay);
                if (position != DatesPagerAdapter.POSITION_NOT_FOUND) {
                    mDatesViewPager.setCurrentItem(position);
                }
                final NewDateButton dateButton =
                        mDatesPagerAdapter.getDateButtonForDate(mSelectedDay);
                if (dateButton != null) {
                    dateButton.select();
                }
            }
            requestProviderAvailability();
            setAvailableHoursBannerVisibility();
        }
    }

    private void requestProviderAvailability() {
        if (mProviderAvailability == null && isAvailableHoursEnabled()) {
            dataManager.getProviderAvailability(mProviderManager.getLastProviderId(),
                    new FragmentSafeCallback<ProviderAvailability>(this) {
                        @Override
                        public void onCallbackSuccess(final ProviderAvailability providerAvailability) {
                            mProviderAvailability = providerAvailability;
                            mUpdatedAvailabilityTimelines = null;
                            showAvailableHours();
                            setAvailableHoursBannerVisibility();
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            // do nothing
                        }
                    });
        }
    }

    private void setAvailableHoursBannerVisibility() {
        if (isAvailableHoursEnabled()
                && mProviderAvailability != null
                && !hasAvailableHoursForNextWeek()
                && !hasDismissedAvailableHoursBannerThisWeek()) {
            mSetAvailableHoursBanner.setVisibility(View.VISIBLE);
        }
        else {
            mSetAvailableHoursBanner.setVisibility(View.GONE);
        }
        updateAvailableHoursMenuItem();
    }

    private boolean hasDismissedAvailableHoursBannerThisWeek() {
        final String dateString = mPrefsManager
                .getString(PrefsKey.DISMISSED_AVAILABLE_HOURS_BANNER_WEEK_START_DATE, null);
        final WeeklyAvailabilityTimelinesWrapper nextWeekAvailability = getNextWeekAvailability();
        return nextWeekAvailability != null && dateString != null &&
                nextWeekAvailability.getStartDateString().equals(dateString);
    }

    private boolean isAvailableHoursEnabled() {
        return mConfigManager.getConfigurationResponse() != null
                && mConfigManager.getConfigurationResponse().isAvailableHoursEnabled();
    }

    private void showAvailableHours() {
        if (mProviderAvailability == null || mSelectedDay == null) {
            mAvailableHoursView.setVisibility(View.GONE);
        }
        else {
            mAvailableHoursView.setVisibility(View.VISIBLE);
            mAvailabilityForSelectedDay = getAvailabilityForDate(mSelectedDay);
            mAvailableHoursView.setAvailableHours(mAvailabilityForSelectedDay == null ? null
                    : mAvailabilityForSelectedDay.getAvailabilityIntervals());
            mAvailableHoursView.setEnabled(mProviderAvailability.covers(mSelectedDay));
        }
    }

    @Nullable
    private DailyAvailabilityTimeline getAvailabilityForDate(final Date date) {
        DailyAvailabilityTimeline availability = null;
        if (mUpdatedAvailabilityTimelines != null) {
            availability = mUpdatedAvailabilityTimelines.get(date);
        }
        if (mProviderAvailability != null && availability == null) {
            availability = mProviderAvailability.getAvailabilityForDate(date);
        }
        return availability;
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        mDatesViewPager.removeOnPageChangeListener(mDatesPageChangeListener);
        super.onPause();
    }

    protected LinearLayout getDatesLayout() {
        return mScheduledJobsDatesScrollViewLayout;
    }

    @Override
    protected void initDateButtons() {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        if (configuration != null && configuration.isNewDateScrollerEnabled()) {
            mScheduledJobsDatesScrollView.setVisibility(View.GONE);
            mDatesPagerAdapter = new DatesPagerAdapter(getActivity(), this);
            mDatesViewPager.setAdapter(mDatesPagerAdapter);
            mDatesViewPager.addOnPageChangeListener(mDatesPageChangeListener);
        }
        else {
            mDatesViewPagerHolder.setVisibility(View.GONE);
            super.initDateButtons();
        }
    }

    protected int getFragmentResourceId() {
        return (R.layout.fragment_scheduled_bookings);
    }

    @Override
    protected BookingListView getBookingListView() {
        return mScheduledJobsListView;
    }

    @Override
    protected SwipeRefreshLayout getNoBookingsSwipeRefreshLayout() {
        return mNoScheduledBookingsLayout;
    }

    @Override
    protected void requestBookings(List<Date> dates, boolean useCachedIfPresent) {
        mBookingManager.requestScheduledBookings(dates, useCachedIfPresent);
        requestProviderAvailability();
    }

    @NonNull
    @Override
    protected String getTrackingType() {
        return "scheduled job";
    }

    @Subscribe
    public void onBookingsRetrieved(HandyEvent.ReceiveScheduledBookingsSuccess event) {
        handleBookingsRetrieved(event);
    }

    @Override
    protected void beforeRequestBookings() {
        // do nothing
    }

    @Override
    protected Class<? extends BookingElementView> getBookingElementViewClass() {
        return ScheduledBookingElementView.class;
    }

    @Override
    protected String getBookingSourceName() {
        return SOURCE_SCHEDULED_JOBS_LIST;
    }

    @Nullable
    @Override
    protected DatesPagerAdapter getDatesPagerAdapter() {
        return mDatesPagerAdapter;
    }

    @Override
    protected void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings) {
        super.afterDisplayBookings(bookingsForDay, dateOfBookings);

        //Show "Find Jobs" buttons only if we're inside of our available bookings length range and we have no jobs

        int numDaysForAvailableJobs = AvailableBookingsFragment.DEFAULT_NUM_DAYS_FOR_AVAILABLE_JOBS;
        if (configManager.getConfigurationResponse() != null) {
            numDaysForAvailableJobs =
                    configManager.getConfigurationResponse().getNumberOfDaysForAvailableJobs();
        }
    }

    @OnClick(R.id.set_hours_dismiss_button)
    public void onDismissSetHoursBannerClicked() {
        mSetAvailableHoursBanner.setVisibility(View.GONE);
        final WeeklyAvailabilityTimelinesWrapper nextWeekAvailability = getNextWeekAvailability();
        if (nextWeekAvailability != null) {
            mPrefsManager.setString(PrefsKey.DISMISSED_AVAILABLE_HOURS_BANNER_WEEK_START_DATE,
                    nextWeekAvailability.getStartDateString());
        }
        updateAvailableHoursMenuItem();
    }

    @OnClick(R.id.set_available_hours_banner)
    public void onSetAvailableHoursBannerClicked() {
        navigateToEditWeeklyAvailableHours();
    }

    private void updateAvailableHoursMenuItem() {
        if (getMenu() == null) {
            return;
        }
        final MenuItem item = getMenu().findItem(R.id.action_available_hours);
        if (item != null) {
            item.setIcon(hasAvailableHoursForNextWeek()
                    || mSetAvailableHoursBanner.getVisibility() == View.VISIBLE ?
                    mAvailableHoursIcon : mAvailableHoursPendingIcon);
        }
    }

    private boolean hasAvailableHoursForNextWeek() {
        final WeeklyAvailabilityTimelinesWrapper weekAvailability = getNextWeekAvailability();
        return weekAvailability != null && weekAvailability.hasAvailableHours();
    }

    @Nullable
    private WeeklyAvailabilityTimelinesWrapper getNextWeekAvailability() {
        if (mProviderAvailability != null) {
            return mProviderAvailability.getWeeklyAvailabilityTimelinesWrappers()
                    .get(NEXT_WEEK_AVAILABILITY_INDEX);
        }
        return null;
    }

    @OnClick(R.id.available_hours_view)
    public void onAvailableHoursClicked() {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.SetDayAvailabilitySelected(
                mSelectedDay != null ? DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(mSelectedDay)
                        : null)));
        final Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.FLOW_CONTEXT, EventContext.AVAILABILITY);
        bundle.putSerializable(BundleKeys.DATE, mSelectedDay);
        bundle.putSerializable(BundleKeys.DAILY_AVAILABILITY_TIMELINE, mAvailabilityForSelectedDay);
        final NavigationEvent.NavigateToPage navigationEvent =
                new NavigationEvent.NavigateToPage(MainViewPage.EDIT_AVAILABLE_HOURS, bundle, true);
        navigationEvent.setReturnFragment(this, RequestCode.EDIT_HOURS);
        bus.post(navigationEvent);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_HOURS) {
            final DailyAvailabilityTimeline availability = (DailyAvailabilityTimeline)
                    data.getSerializableExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
            if (availability != null) {
                if (mUpdatedAvailabilityTimelines == null) {
                    mUpdatedAvailabilityTimelines = new HashMap<>();
                }
                mUpdatedAvailabilityTimelines.put(availability.getDate(), availability);
                showAvailableHours();
            }
        }
    }

    @Override
    protected boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay) {
        return false;
    }

    @Override
    //All bookings not in the past on this page should cause the claimed indicator to appear
    protected boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay) {
        for (Booking b : bookingsForDay) {
            if (!b.isEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int numberOfDaysToDisplay() {
        return 28;
    }

    @Subscribe
    public void onRequestBookingsError(HandyEvent.ReceiveScheduledBookingsError event) {
        handleBookingsRetrievalError(event, R.string.error_fetching_scheduled_jobs);
    }

    @Override
    public void onDateSelected(final Date date) {
        onDateClicked(date);
        showAvailableHours();
    }
}
