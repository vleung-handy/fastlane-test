package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.availability.AvailabilityEvent;
import com.handy.portal.availability.fragment.EditAvailableHoursFragment;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.AvailableHoursView;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.AuxiliaryInfo;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.adapter.DatesPagerAdapter;
import com.handy.portal.bookings.ui.adapter.RequestedJobsPagerAdapter;
import com.handy.portal.bookings.ui.element.NewDateButton;
import com.handy.portal.bookings.ui.element.NewDateButtonGroup;
import com.handy.portal.bookings.ui.element.ScheduledBookingElementView;
import com.handy.portal.bookings.util.BookingListUtils;
import com.handy.portal.bookings.util.ClaimUtils;
import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;
import com.handy.portal.clients.ui.fragment.dialog.RequestDismissalReasonsDialogFragment;
import com.handy.portal.clients.ui.fragment.dialog.RescheduleDialogFragment;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.ProviderSettingsEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.EventType;
import com.handy.portal.logger.handylogger.model.JobsLog;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.logger.handylogger.model.SendAvailabilityLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduledBookingsFragment extends ActionBarFragment
        implements DatesPagerAdapter.DateSelectedListener {
    private static final String SOURCE_SCHEDULED_JOBS_LIST = "scheduled_jobs_list";
    public static final int DEFAULT_NUMBER_OF_DAYS_TO_ENABLE = 28;

    @Inject
    BookingManager mBookingManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    AvailabilityManager mAvailabilityManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.bookings_content)
    LinearLayout mContent;
    @BindView(R.id.scheduled_jobs_view)
    LinearLayout mScheduledJobsView;
    @BindView(R.id.scheduled_jobs_scroll_view)
    ScrollView mScheduledJobsScrollView;
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
    @BindView(R.id.requested_jobs_view_pager)
    ViewPager mRequestedJobsViewPager;
    @BindView(R.id.requested_jobs_guide)
    View mRequestedJobsGuide;
    @BindView(R.id.requested_jobs_guide_date)
    TextView mRequestedJobsGuideDate;
    @BindView(R.id.requested_jobs_guide_item_count)
    TextView mRequestedJobsGuideItemCount;
    @BindDimen(R.dimen.default_margin_half)
    int mRequestedJobsGapMargin;
    @BindDimen(R.dimen.default_margin_x3)
    int mRequestedJobsMargin;
    @BindDimen(R.dimen.thin_padding)
    int mBorderSize;
    private Date mSelectedDay;
    private DatesPagerAdapter mDatesPagerAdapter;
    private int mLastDatesPosition;
    private Availability.AdhocTimeline mTimelineForSelectedDay;
    private final Runnable mRefreshRunnable;
    private final ViewPager.OnPageChangeListener mDatesPageChangeListener;
    private final ViewPager.OnPageChangeListener mRequestedJobsPageChangeListener;
    private RequestedJobsRecyclerViewAdapter.JobViewHolder.Listener mJobViewHolderListener;

    {
        mDatesPageChangeListener = new ViewPager.OnPageChangeListener() {
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
                    // We need to give this a slight delay because it's causing the UI to to lag.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dateButton.select();
                        }
                    }, 100);
                }
                mLastDatesPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                // do nothing
            }
        };
        mRequestedJobsPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(final int position) {
                updateRequestedJobsItemCount(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                // do nothing
            }
        };
        mJobViewHolderListener = new RequestedJobsRecyclerViewAdapter.JobViewHolder.Listener() {
            @Override
            public void onSelect(final Booking booking) {
                showBookingDetails(booking);
            }

            @Override
            public void onClaim(final Booking booking) {
                boolean confirmClaimDialogShown =
                        ClaimUtils.showConfirmBookingClaimDialogIfNecessary(
                                booking,
                                ScheduledBookingsFragment.this,
                                getFragmentManager()
                        );
                if (!confirmClaimDialogShown) {
                    requestClaimJob(booking);
                }
            }

            @Override
            public void onDismiss(final Booking booking) {
                if (booking.getRequestAttributes() != null
                        && booking.getRequestAttributes().hasCustomer()) {
                    // Display dialog for selecting a request dismissal reason
                    final RequestDismissalReasonsDialogFragment dialogFragment =
                            RequestDismissalReasonsDialogFragment.newInstance(booking);
                    dialogFragment.setTargetFragment(
                            ScheduledBookingsFragment.this, RequestCode.CONFIRM_DISMISS
                    );
                    FragmentUtils.safeLaunchDialogFragment(
                            dialogFragment, ScheduledBookingsFragment.this, null
                    );
                    bus.post(
                            new RequestedJobsLog.DismissJobShown(EventContext.SCHEDULED_JOBS, booking));
                }
                else {
                    dismissJob(booking);
                }
            }

            @Override
            public void onReschedule(final Booking booking) {
                FragmentUtils.safeLaunchDialogFragment(
                        RescheduleDialogFragment.newInstance(booking),
                        ScheduledBookingsFragment.this,
                        null
                );
                bus.post(new SendAvailabilityLog.SendAvailabilitySelected(
                        EventContext.SCHEDULED_JOBS, booking)
                );
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
        bus.register(this);
        setHasOptionsMenu(true);

        if (getArguments() != null
                && getArguments().getBoolean(DeeplinkUtils.DEEP_LINK_AVAILABLE_HOURS, false)) {
            navigateToEditWeeklyAvailableHours();
        }

        initializeSelectedDate();
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

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestBookingsForSelectedDay(false, false);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        mRequestedJobsViewPager.setPageMargin(-mRequestedJobsMargin + mRequestedJobsGapMargin);
        mRequestedJobsViewPager.setOffscreenPageLimit(3);

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
            else {
                //This is used to get the deep link date. This date will be the selected date
                final String date = getArguments().getString(BundleKeys.DATE);

                if (!TextUtils.isEmpty(date)) {
                    try {
                        mSelectedDay = DateTimeUtils.ISO8601_FORMATTER2.parse(date);
                    }
                    catch (Exception e) {
                        Crashlytics.logException(e);
                    }
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
        bus.post(new ScheduledJobsLog.SetWeekAvailabilitySelected(
                DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(mSelectedDay)));
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.FLOW_CONTEXT, EventContext.AVAILABILITY);
        mNavigationManager.navigateToPage(
                getActivity().getSupportFragmentManager(),
                mConfigManager.getConfigurationResponse().isTemplateAvailabilityEnabled() ?
                        MainViewPage.EDIT_WEEKLY_TEMPLATE_AVAILABLE_HOURS :
                        MainViewPage.EDIT_WEEKLY_ADHOC_AVAILABLE_HOURS,
                arguments,
                TransitionStyle.NATIVE_TO_NATIVE,
                true);
    }

    @Override
    public void onResume() {
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

                //remove listener so it won't trigger since we're setting the date on the next section
                mDatesViewPager.removeOnPageChangeListener(mDatesPageChangeListener);
                mDatesViewPager.setCurrentItem(position);
                //Add this back so that selecting will work now
                mDatesViewPager.addOnPageChangeListener(mDatesPageChangeListener);
            }

            final NewDateButton dateButton =
                    mDatesPagerAdapter.getDateButtonForDate(mSelectedDay);
            if (dateButton != null) {
                dateButton.select();
            }

            requestProviderAvailability();

            updateRequestedJobsItemCount(mRequestedJobsViewPager.getCurrentItem());
        }
    }

    @OnClick(R.id.try_again_button)
    public void onRetryRequestBookings() {
        requestBookingsForSelectedDay(true, false);
    }

    private void requestBookingsForSelectedDay(boolean refreshing, boolean useCachedIfPresent) {
        if (mSelectedDay != null) {
            requestBookings(Lists.newArrayList(mSelectedDay), refreshing, useCachedIfPresent);
        }
    }

    private void requestBookingsForOtherDays() {
        final List<Date> dates = Lists.newArrayList();
        for (int i = 0; i < getNumberOfDaysToEnable(); i++) {
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
            mScheduledJobsScrollView.scrollTo(0, 0);
            hideRequestedJobs();
            hideScheduledJobs();
            // this delay will prevent the refreshing icon to flicker when loading cached data
            mScheduledJobsView.postDelayed(mRefreshRunnable, 200);
        }
        requestBookings(dates, useCachedIfPresent);
    }

    private void hideRequestedJobs() {
        mRequestedJobsViewPager.setAdapter(null);
        mRequestedJobsViewPager.removeOnPageChangeListener(mRequestedJobsPageChangeListener);
        mRequestedJobsViewPager.setVisibility(View.GONE);
        mRequestedJobsGuide.setVisibility(View.GONE);
    }

    private void hideScheduledJobs() {
        mScheduledJobsView.setVisibility(View.GONE);
        mScheduledJobsView.removeAllViews();
    }

    private void showJobs() {
        mScheduledJobsView.removeCallbacks(mRefreshRunnable);
        mRefreshLayout.setRefreshing(false);
        mScheduledJobsView.setVisibility(View.VISIBLE);
        if (mRequestedJobsViewPager.getAdapter() != null
                && mRequestedJobsViewPager.getAdapter().getCount() > 0) {
            mRequestedJobsViewPager.setVisibility(View.VISIBLE);
            mRequestedJobsGuide.setVisibility(View.VISIBLE);
        }
    }

    private boolean isScheduleTabRequestedEnabled() {
        return mConfigManager.getConfigurationResponse() != null
                && mConfigManager.getConfigurationResponse().isScheduleTabRequestedEnabled();
    }

    private void requestBookings(final List<Date> dates, final boolean useCachedIfPresent) {
        mBookingManager.requestScheduledBookings(dates, useCachedIfPresent);
        if (isScheduleTabRequestedEnabled()) {
            mBookingManager.requestProRequestedJobs(dates, useCachedIfPresent);
        }
        requestProviderAvailability();
    }

    private void requestProviderAvailability() {
        if (isAvailableHoursEnabled()) {
            mAvailabilityManager.getAvailability(
                    false,
                    new FragmentSafeCallback<Void>(this) {
                        @Override
                        public void onCallbackSuccess(final Void response) {
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

    @Subscribe
    public void onAvailabilityAdhocTimelineUpdated(
            final AvailabilityEvent.AdhocTimelineUpdated event
    ) {
        showAvailableHours();
        setAvailableHoursBannerVisibility();
    }

    @Subscribe
    public void onAvailabilityTemplateTimelineUpdated(
            final AvailabilityEvent.TemplateTimelineUpdated event
    ) {
        showAvailableHours();
        setAvailableHoursBannerVisibility();
    }

    private void setAvailableHoursBannerVisibility() {
        if (isAvailableHoursEnabled()
                && !mAvailabilityManager.hasAvailableHours()
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
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        return currentWeekRange != null
                && dateString != null
                && currentWeekRange.getStartDateString().equals(dateString);
    }

    private boolean isAvailableHoursEnabled() {
        return mConfigManager.getConfigurationResponse().isAvailableHoursEnabled();
    }

    private void showAvailableHours() {
        if (!isAvailableHoursEnabled() || mSelectedDay == null) {
            mAvailableHoursView.setVisibility(View.GONE);
        }
        else if (mAvailabilityManager.isReady()) {
            mAvailableHoursView.setVisibility(View.VISIBLE);
            mTimelineForSelectedDay = mAvailabilityManager.getTimelineForDate(mSelectedDay);
            mAvailableHoursView.setAvailableHours(mTimelineForSelectedDay == null ? null
                    : mTimelineForSelectedDay.getIntervals());
            mAvailableHoursView.setEnabled(mAvailabilityManager.covers(mSelectedDay));
        }
    }

    @Override
    public void onPause() {
        mDatesViewPager.removeOnPageChangeListener(mDatesPageChangeListener);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    private void initDateButtons() {
        mDatesPagerAdapter = new DatesPagerAdapter(getActivity(), getNumberOfDaysToEnable(), this);
        mDatesViewPager.setAdapter(mDatesPagerAdapter);
        mDatesViewPager.addOnPageChangeListener(mDatesPageChangeListener);
    }

    private int getNumberOfDaysToEnable() {
        return mConfigManager.getConfigurationResponse().getNumberOfDaysForScheduledJobs();
    }

    @Subscribe
    public void onReceiveScheduledBookingsSuccess(
            final HandyEvent.ReceiveScheduledBookingsSuccess event
    ) {
        final BookingsWrapper bookingsWrapper = event.bookingsWrapper;

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
            displayJobs(bookingsWrapper, mSelectedDay);
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

    private synchronized void displayJobs(
            @NonNull final BookingsWrapper bookingsWrapper,
            @NonNull final Date dateOfBookings
    ) {
        mScheduledJobsView.removeAllViews();
        final List<Booking> bookings = bookingsWrapper.getBookings();
        if (bookings.isEmpty()) {
            LayoutInflater.from(getActivity()).inflate(R.layout.layout_scheduled_bookings_empty,
                    mScheduledJobsView, true);
        }
        else {
            for (final Booking booking : bookings) {
                final ScheduledBookingElementView mediator = new ScheduledBookingElementView();
                mediator.initView(getActivity(), booking, null, mScheduledJobsView);

                final FrameLayout view = new FrameLayout(getActivity());
                view.addView(mediator.getAssociatedView());
                view.setBackgroundResource(R.drawable.border_gray_bottom);
                view.setPadding(0, 0, 0, mBorderSize);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        showBookingDetails(booking);
                    }
                });
                mScheduledJobsView.addView(view);
            }
        }
        if (!isScheduleTabRequestedEnabled()
                || mRequestedJobsViewPager.getAdapter() != null) {
            showJobs();
        }
    }

    private boolean shouldShowClaimedIndicator(final List<Booking> bookingsForDay) {
        for (final Booking booking : bookingsForDay) {
            if (!booking.isEnded()) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public synchronized void onReceiveProRequestedJobsSuccess(
            final BookingEvent.ReceiveProRequestedJobsSuccess event
    ) {
        final List<BookingsWrapper> requestedJobsWrappers = event.getProRequestedJobs();

        if (!isScheduleTabRequestedEnabled()
                || requestedJobsWrappers == null
                || requestedJobsWrappers.isEmpty()
                || mRequestedJobsViewPager.getAdapter() != null) { return; }

        for (final BookingsWrapper requestedJobsWrapper : requestedJobsWrappers) {
            if (mSelectedDay.equals(requestedJobsWrapper.getDate())) {
                populateRequestedJobs(requestedJobsWrapper);
                break;
            }
        }
    }

    private void populateRequestedJobs(final BookingsWrapper requestedJobsWrapper) {
        final List<Booking> undismissedBookings = requestedJobsWrapper.getUndismissedBookings();
        if (undismissedBookings != null) {
            mRequestedJobsViewPager.setAdapter(new RequestedJobsPagerAdapter(
                    getActivity(), undismissedBookings, mJobViewHolderListener));
            mRequestedJobsViewPager.addOnPageChangeListener(mRequestedJobsPageChangeListener);
            updateRequestedJobsItemCount(0);
            final String formattedDate = DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(
                    requestedJobsWrapper.getDate());
            mRequestedJobsGuideDate.setText(getString(R.string.requests_for_date_formatted,
                    formattedDate));
            if (mScheduledJobsView.getChildCount() > 0) {
                showJobs();
            }
            bus.post(
                    new RequestedJobsLog.RequestsShown(
                            EventContext.SCHEDULED_JOBS,
                            undismissedBookings.size(),
                            BookingListUtils.getCountPerAuxType(undismissedBookings, AuxiliaryInfo.Type.REFERRAL),
                            BookingListUtils.getCountPerAuxType(undismissedBookings, AuxiliaryInfo.Type.FAVORITE),
                            requestedJobsWrapper.getDate()
                    )
            );
        }
    }

    private void updateRequestedJobsItemCount(final int position) {
        final PagerAdapter adapter = mRequestedJobsViewPager.getAdapter();
        if (adapter != null) {
            mRequestedJobsGuideItemCount.setText(getString(R.string.x_of_n_formatted,
                    position + 1, adapter.getCount()));
        }
    }

    @OnClick(R.id.set_hours_dismiss_button)
    public void onDismissSetHoursBannerClicked() {
        mSetAvailableHoursBanner.setVisibility(View.GONE);
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        if (currentWeekRange != null) {
            mPrefsManager.setString(PrefsKey.DISMISSED_AVAILABLE_HOURS_BANNER_WEEK_START_DATE,
                    currentWeekRange.getStartDateString());
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
            item.setIcon(mAvailabilityManager.hasAvailableHours()
                    || mSetAvailableHoursBanner.getVisibility() == View.VISIBLE ?
                    mAvailableHoursIcon : mAvailableHoursPendingIcon);
        }
    }

    @OnClick(R.id.available_hours_view)
    public void onAvailableHoursClicked() {
        bus.post(new ScheduledJobsLog.SetDayAvailabilitySelected(
                mSelectedDay != null ? DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(mSelectedDay)
                        : null));
        final Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.FLOW_CONTEXT, EventContext.SCHEDULED_JOBS);
        bundle.putSerializable(BundleKeys.MODE, EditAvailableHoursFragment.Mode.ADHOC);
        bundle.putSerializable(BundleKeys.DATE, mSelectedDay);
        bundle.putSerializable(BundleKeys.TIMELINE,
                mAvailabilityManager.getTimelineForDate(mSelectedDay));
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.EDIT_AVAILABLE_HOURS, bundle, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final Booking booking = (Booking) data.getSerializableExtra(BundleKeys.BOOKING);
            switch (requestCode) {
                case RequestCode.CONFIRM_DISMISS:
                    final String dismissalReason =
                            data.getStringExtra(BundleKeys.DISMISSAL_REASON);
                    dismissJob(booking, dismissalReason);
                    break;
                case RequestCode.CONFIRM_SWAP:
                case RequestCode.CONFIRM_REQUEST:
                    requestClaimJob(booking);
                    break;
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
        arguments.putString(BundleKeys.EVENT_CONTEXT, EventContext.SCHEDULED_JOBS);
        arguments.putSerializable(BundleKeys.PAGE, getAppPage());
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.JOB_DETAILS, arguments, TransitionStyle.JOB_LIST_TO_DETAILS, true);
    }

    private void requestClaimJob(final Booking booking) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new JobsLog(EventType.CLAIM_SUBMITTED,
                EventContext.SCHEDULED_JOBS, booking));
        mBookingManager.requestClaimJob(booking, null);
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new JobsLog(EventType.CLAIM_SUCCESS,
                EventContext.SCHEDULED_JOBS, event.originalBooking));
        Snackbar.make(mContent, R.string.job_claim_success,
                Snackbar.LENGTH_LONG).show();
        mBookingManager.requestProRequestedJobsCount();
        requestBookingsForSelectedDay(true, false);
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = getString(R.string.job_claim_error);
        }
        bus.post(new JobsLog(EventType.CLAIM_ERROR,
                EventContext.SCHEDULED_JOBS, event.getBooking()));
        Snackbar.make(mContent, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Subscribe
    public void onAvailableHoursSent(final HandyEvent.AvailableHoursSent event) {
        requestBookingsForSelectedDay(true, false);
    }

    private void dismissJob(final Booking booking) {
        dismissJob(booking, BookingManager.DISMISSAL_REASON_UNSPECIFIED);
    }

    private void dismissJob(@NonNull final Booking booking,
                            @NonNull @BookingManager.DismissalReason final String dismissalReason) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new RequestedJobsLog.DismissJobSubmitted(
                EventContext.SCHEDULED_JOBS, booking, dismissalReason));
        final Booking.RequestAttributes requestAttributes = booking.getRequestAttributes();
        String customerId = null;
        if (requestAttributes != null && requestAttributes.hasCustomer()) {
            customerId = requestAttributes.getCustomerId();
        }
        mBookingManager.requestDismissJob(booking, customerId, dismissalReason);
    }

    @Subscribe
    public void onReceiveDismissJobSuccess(final HandyEvent.ReceiveDismissJobSuccess event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new RequestedJobsLog.DismissJobSuccess(
                EventContext.SCHEDULED_JOBS, event.getBooking()));
        Snackbar.make(mContent, R.string.request_dismissal_success_message,
                Snackbar.LENGTH_LONG).show();
        mBookingManager.requestProRequestedJobsCount();
        requestBookingsForSelectedDay(true, false);
    }

    @Subscribe
    public void onReceiveDismissJobError(final HandyEvent.ReceiveDismissJobError event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = getString(R.string.request_dismissal_error);
        }
        bus.post(new RequestedJobsLog.DismissJobError(
                EventContext.SCHEDULED_JOBS, event.getBooking(), errorMessage));
        Snackbar.make(mContent, errorMessage, Snackbar.LENGTH_LONG).show();
    }
}
