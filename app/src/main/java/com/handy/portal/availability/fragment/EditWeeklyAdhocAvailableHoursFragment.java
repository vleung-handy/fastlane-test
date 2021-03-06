package com.handy.portal.availability.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.availability.AvailabilityEvent;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.TabWithDateRangeView;
import com.handy.portal.availability.view.WeeklyAvailableHoursView.CellClickListener;
import com.handy.portal.availability.viewmodel.AvailableHoursViewModel;
import com.handy.portal.availability.viewmodel.WeeklyPagerAdapter;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class EditWeeklyAdhocAvailableHoursFragment extends EditWeeklyAvailableHoursFragment {
    private static final int CURRENT_WEEK_INDEX = 0;
    private static final int NEXT_WEEK_INDEX = 1;

    @Inject
    AvailabilityManager mAvailabilityManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.available_hours_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.available_hours_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.copy_hours_button)
    Button mCopyHoursButton;

    private String mFlowContext;
    private WeeklyPagerAdapter mPagerAdapter;
    private boolean mIsCurrentWeekAndNextWeekInSync;
    private int mDefaultSelectedTab;
    private ViewPager.OnPageChangeListener mWeekPageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                                           final int positionOffsetPixels) {
                    // do nothing
                }

                @Override
                public void onPageSelected(final int position) {
                    updateCopyHoursButton();
                }

                @Override
                public void onPageScrollStateChanged(final int state) {
                    // do nothing
                }
            };
    private CellClickListener mCellClickListener = new CellClickListener() {
        @Override
        public void onCellClicked(final AvailableHoursViewModel viewModel) {
            final Date date = (Date) viewModel.getIdentifier();
            bus.post(
                    new ProAvailabilityLog.SetDayAvailabilitySelected(mFlowContext,
                            DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(date)));
            final Bundle bundle = new Bundle();
            bundle.putString(BundleKeys.FLOW_CONTEXT, mFlowContext);
            bundle.putSerializable(BundleKeys.MODE, EditAvailableHoursFragment.Mode.ADHOC);
            bundle.putSerializable(BundleKeys.DATE, date);
            bundle.putSerializable(BundleKeys.TIMELINE, mAvailabilityManager.getTimelineForDate(date));
            mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                    MainViewPage.EDIT_AVAILABLE_HOURS, bundle, null, true);
        }
    };

    @Override
    protected int getContentResId() {
        return R.layout.element_weekly_adhoc_available_hours;
    }

    @Subscribe
    public void onAvailabilityTimelineUpdated(final AvailabilityEvent.AdhocTimelineUpdated event) {
        mPagerAdapter.updateViewWithTimeline(event.getTimeline().getDate(), event.getTimeline());
        setIsCurrentWeekAndNextWeekInSync(false);
    }

    @OnClick(R.id.copy_hours_button)
    public void onCopyHoursClicked() {
        bus.post(
                new ProAvailabilityLog.CopyCurrentWeekSelected(mFlowContext));
        final Availability.Wrapper.AdhocTimelines timelinesWrapper =
                createNextWeekTimelinesWrapperFromCurrentWeekRange();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

        mAvailabilityManager.saveAvailability(
                timelinesWrapper,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        setIsCurrentWeekAndNextWeekInSync(true);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        String message = error.getMessage();
                        if (TextUtils.isEmpty(message)) {
                            message = getString(R.string.an_error_has_occurred);
                        }
                        showToast(message);
                    }
                }
        );
    }

    @NonNull
    private Availability.Wrapper.AdhocTimelines createNextWeekTimelinesWrapperFromCurrentWeekRange() {
        final Availability.Wrapper.AdhocTimelines timelinesWrapper = new Availability.Wrapper.AdhocTimelines();
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        for (final Date date : currentWeekRange.dates()) {
            final Calendar nextWeekDate = Calendar.getInstance(Locale.US);
            nextWeekDate.setTime(date);
            nextWeekDate.add(Calendar.DATE, DateTimeUtils.DAYS_IN_A_WEEK);

            final Availability.AdhocTimeline timeline = mAvailabilityManager.getTimelineForDate(date);
            final ArrayList<Availability.Interval> intervals = Lists.newArrayList();
            if (timeline != null && timeline.getIntervals() != null) {
                intervals.addAll(timeline.getIntervals());
            }
            timelinesWrapper.addTimeline(nextWeekDate.getTime(), intervals);
        }
        return timelinesWrapper;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        mFlowContext = getArguments().getString(BundleKeys.FLOW_CONTEXT);
        mDefaultSelectedTab = getArguments().getBoolean(BundleKeys.SHOULD_DEFAULT_TO_NEXT_WEEK,
                false) ? NEXT_WEEK_INDEX : CURRENT_WEEK_INDEX;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPagerAdapter != null) {
            displayAvailableHours();
            displayTabs();
            updateCopyHoursButton();
        }
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    private void setIsCurrentWeekAndNextWeekInSync(final boolean value) {
        mIsCurrentWeekAndNextWeekInSync = value;
        updateCopyHoursButton();
    }

    private void updateCopyHoursButton() {
        mCopyHoursButton.setVisibility(mViewPager.getCurrentItem() == NEXT_WEEK_INDEX ?
                View.VISIBLE : View.GONE);
        if (mIsCurrentWeekAndNextWeekInSync) {
            mCopyHoursButton.setText(R.string.copied);
            mCopyHoursButton.setAlpha(0.3f);
            mCopyHoursButton.setEnabled(false);
        }
        else {
            mCopyHoursButton.setText(R.string.copy_hours_from_current_week);
            mCopyHoursButton.setAlpha(1.0f);
            mCopyHoursButton.setEnabled(true);
        }
    }

    private void displayAvailableHours() {
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        final Availability.Range nextWeekRange = mAvailabilityManager.getNextWeekRange();
        mPagerAdapter = new WeeklyPagerAdapter(
                getActivity(), currentWeekRange, nextWeekRange, mCellClickListener
        );

        final List<Date> allDates = new ArrayList<>();
        allDates.addAll(currentWeekRange.dates());
        allDates.addAll(nextWeekRange.dates());
        for (final Date date : allDates) {
            mPagerAdapter.updateViewWithTimeline(date, mAvailabilityManager.getTimelineForDate(date));
        }
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mWeekPageChangeListener);
    }

    private void displayTabs() {
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        final Availability.Range nextWeekRange = mAvailabilityManager.getNextWeekRange();
        final int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        mTabLayout.removeAllTabs();
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(CURRENT_WEEK_INDEX).setCustomView(new TabWithDateRangeView(
                getActivity(), R.string.current_week, currentWeekRange));
        mTabLayout.getTabAt(NEXT_WEEK_INDEX).setCustomView(new TabWithDateRangeView(getActivity(),
                R.string.next_week, nextWeekRange));
        mViewPager.setCurrentItem(selectedTabPosition != WeeklyPagerAdapter.POSITION_NOT_FOUND ?
                selectedTabPosition : mDefaultSelectedTab);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPagerAdapter == null) {
            requestAvailableHours();
        }
        else {
            // This is here to fix a UI duplication bug related to tabs
            mScrollView.setVisibility(View.VISIBLE);
            displayTabs();
        }
    }

    @Override
    protected void requestAvailableHours() {
        mRefreshLayout.setRefreshing(true);
        mAvailabilityManager.getAvailability(true, new FragmentSafeCallback<Void>(this) {
            @Override
            public void onCallbackSuccess(final Void response) {
                mRefreshLayout.setRefreshing(false);
                mFetchErrorView.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                displayAvailableHours();
                displayTabs();
                updateCopyHoursButton();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                mRefreshLayout.setRefreshing(false);
                mFetchErrorView.setVisibility(View.VISIBLE);
            }
        });
    }
}
