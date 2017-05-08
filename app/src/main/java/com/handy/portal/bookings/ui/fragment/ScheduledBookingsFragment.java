package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;
import com.handy.portal.bookings.ui.element.NewDateButton;
import com.handy.portal.bookings.ui.element.NewDateButtonGroup;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.event.ProviderSettingsEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduledBookingsFragment extends ActionBarFragment
        implements DatesPagerAdapter.DateSelectedListener {
    private static final int NEXT_WEEK_AVAILABILITY_INDEX = 1;
    private static final int NUMBER_OF_DAYS_TO_DISPLAY = 28;
    private static final String SOURCE_SCHEDULED_JOBS_LIST = "scheduled_jobs_list";

    @Inject
    ProviderManager mProviderManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    ConfigManager mConfigManager;

    @BindView(R.id.scheduled_jobs_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.dates_view_pager_holder)
    ViewGroup mDatesViewPagerHolder;
    @BindView(R.id.dates_view_pager)
    ViewPager mDatesViewPager;
    @BindView(R.id.available_hours_view)
    AvailableHoursView mAvailableHoursView;
    @BindView(R.id.set_available_hours_banner)
    View mSetAvailableHoursBanner;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindDrawable(R.drawable.ic_available_hours)
    Drawable mAvailableHoursIcon;
    @BindDrawable(R.drawable.ic_available_hours_pending)
    Drawable mAvailableHoursPendingIcon;
    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mErrorText;
    private Date mSelectedDay;
    private DatesPagerAdapter mDatesPagerAdapter;
    private int mLastDatesPosition;
    private ProviderAvailability mProviderAvailability;
    private DailyAvailabilityTimeline mAvailabilityForSelectedDay;
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;
    private final Runnable mRefreshRunnable;
    private final ViewPager.OnPageChangeListener mDatesPageChangeListener;

    {
        mDatesPageChangeListener =
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
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        };
    }

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

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate((R.layout.fragment_scheduled_bookings), null);
        ButterKnife.bind(this, view);

        initializeSelectedDate();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBookingsForSelectedDay(false, false);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        return view;
    }

    private void initializeSelectedDate() {
        if (getArguments() != null) {
            final long targetDateTime = getArguments().getLong(BundleKeys.DATE_EPOCH_TIME);
            if (targetDateTime > 0) {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date(targetDateTime));
            }

            final String daysToAdd =
                    getArguments().getString(DeeplinkUtils.DEEP_LINK_PARAM_DAY, null);
            if (daysToAdd != null) {
                try {
                    int dta = Integer.parseInt(daysToAdd);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE, dta);
                    mSelectedDay = DateTimeUtils.getDateWithoutTime(calendar.getTime());
                }
                catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
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
            bus.post(new ProviderSettingsEvent.RequestProviderSettings());

            initDateButtons();

            if (mSelectedDay == null) {
                mSelectedDay = DateTimeUtils.getDateWithoutTime(new Date());
            }

            requestBookingsForOtherDays();

            final int position = mDatesPagerAdapter.getItemPositionWithDate(mSelectedDay);
            if (position != DatesPagerAdapter.POSITION_NOT_FOUND) {
                mDatesViewPager.setCurrentItem(position);
            }
            final NewDateButton dateButton =
                    mDatesPagerAdapter.getDateButtonForDate(mSelectedDay);
            if (dateButton != null) {
                dateButton.select();
            }

            requestProviderAvailability();
            setAvailableHoursBannerVisibility();
        }
    }

    private void requestBookingsForSelectedDay(boolean refreshing, boolean useCachedIfPresent) {
        if (mSelectedDay != null) {
            requestBookings(Lists.newArrayList(mSelectedDay), refreshing, useCachedIfPresent);
        }
    }

    private void requestBookingsForOtherDays() {
        final List<Date> dates = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_DAYS_TO_DISPLAY; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            if (!day.equals(mSelectedDay)) {
                dates.add(day);
            }
        }
        requestBookings(dates, false, true);
    }

    private void requestBookings(List<Date> dates, boolean refreshing, boolean useCachedIfPresent) {
        mFetchErrorView.setVisibility(View.GONE);
        if (refreshing) {
            mRecyclerView.setLayoutManager(null);
            mRecyclerView.setAdapter(null);
            // this delay will prevent the refreshing icon to flicker when loading cached data
            mRecyclerView.postDelayed(mRefreshRunnable, 200);
        }
        requestBookings(dates, useCachedIfPresent);
    }


    private void requestBookings(List<Date> dates, boolean useCachedIfPresent) {
        mBookingManager.requestScheduledBookings(dates, useCachedIfPresent);
        requestProviderAvailability();
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

    private void initDateButtons() {
        mDatesPagerAdapter = new DatesPagerAdapter(getActivity(), this);
        mDatesViewPager.setAdapter(mDatesPagerAdapter);
        mDatesViewPager.addOnPageChangeListener(mDatesPageChangeListener);
    }

    protected int getFragmentResourceId() {
        return (R.layout.fragment_scheduled_bookings);
    }

    @Subscribe
    public void onReceiveScheduledBookingsSuccess(
            final HandyEvent.ReceiveScheduledBookingsSuccess event
    ) {
        final BookingsWrapper bookingsWrapper = event.bookingsWrapper;

        mRecyclerView.removeCallbacks(mRefreshRunnable);
        List<Booking> bookings = event.bookingsWrapper.getBookings();
        Collections.sort(bookings);

        for (Booking b : bookings) {
            if (b.getZipClusterId() != null) {
                bus.post(new BookingEvent.RequestZipClusterPolygons(b.getZipClusterId()));
            }
        }

        if (shouldShowClaimedIndicator(bookings)) {
            mDatesPagerAdapter.showClaimIndicatorForDate(event.day);
        }

        if (mSelectedDay != null && mSelectedDay.equals(event.day)) {
            mRefreshLayout.setRefreshing(false);
            displayBookings(bookingsWrapper, mSelectedDay);
        }
    }


    @Subscribe
    public void onReceiveScheduledBookingsError(
            final HandyEvent.ReceiveScheduledBookingsError event
    ) {
        if (event.days.contains(mSelectedDay)) {
            mRefreshLayout.setRefreshing(false);
            if (event.error != null && !TextUtils.isEmpty(event.error.getMessage())) {
                mErrorText.setText(event.error.getMessage());
            }
            else {
                mErrorText.setText(R.string.error_fetching_scheduled_jobs);
            }
            mFetchErrorView.setVisibility(View.VISIBLE);
        }
    }

    protected void displayBookings(
            @NonNull final BookingsWrapper bookingsWrapper,
            @NonNull final Date dateOfBookings
    ) {
        final List<Booking> bookings = bookingsWrapper.getBookings();
        // TODO: populate recycler view
        // mRecyclerView.setAdapter(...);
        // mRecyclerView.setLayoutManager(new LinearLayoutManager());

        // TODO: Log this somewhere -> bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.Clicked(booking, oneBasedIndex)));
    }

    private boolean shouldShowClaimedIndicator(final List<Booking> bookingsForDay) {
        for (final Booking booking : bookingsForDay) {
            if (!booking.isEnded()) {
                return true;
            }
        }
        return false;
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
    public void onDateSelected(final Date date) {
        mSelectedDay = date;
        requestBookings(Lists.newArrayList(date), true, true);
        showAvailableHours();
    }

    private void showBookingDetails(final Booking booking) {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        arguments.putString(BundleKeys.BOOKING_SOURCE, SOURCE_SCHEDULED_JOBS_LIST);
        arguments.putSerializable(BundleKeys.PAGE, getAppPage());
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.JOB_DETAILS, arguments,
                TransitionStyle.JOB_LIST_TO_DETAILS, true));
    }
}
