package com.handy.portal.proavailability.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.ProviderAvailability;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.AvailableHoursWithDateView;
import com.handy.portal.proavailability.view.TabWithDateRangeView;
import com.handy.portal.proavailability.view.WeeklyAvailableHoursView;
import com.handy.portal.proavailability.view.WeeklyAvailableHoursView.DateClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditWeeklyAvailableHoursFragment extends ActionBarFragment {
    private static final int CURRENT_WEEK_INDEX = 0;
    private static final int NEXT_WEEK_INDEX = 1;
    private static final String SETTING_AVAILABLE_HOURS_HELP_URL = "https://handy.com/help/setting-available-hours";
    @Inject
    ProviderManager mProviderManager;


    @BindView(R.id.available_hours_info_banner_body)
    TextView mInfoBannerBody;
    @BindView(R.id.available_hours_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.available_hours_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.fetch_error_view)
    View mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;
    @BindView(R.id.copy_hours_button)
    Button mCopyHoursButton;
    @BindColor(R.color.black)
    int mBlackColor;
    @BindColor(R.color.error_red)
    int mRedColor;

    private String mFlowContext;
    private ProviderAvailability mProviderAvailability;
    private TabAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mWeekPageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                                           final int positionOffsetPixels) {
                    // do nothing
                }

                @Override
                public void onPageSelected(final int position) {
                    mCopyHoursButton.setVisibility(position == NEXT_WEEK_INDEX ?
                            View.VISIBLE : View.GONE);
                }

                @Override
                public void onPageScrollStateChanged(final int state) {
                    // do nothing
                }
            };
    private DateClickListener mDateClickListener = new DateClickListener() {
        @Override
        public void onDateClicked(final Date date) {
            bus.post(new LogEvent.AddLogEvent(
                    new ProAvailabilityLog.SetDayAvailabilitySelected(mFlowContext,
                            DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(date))));
            final Bundle bundle = new Bundle();
            bundle.putString(BundleKeys.FLOW_CONTEXT, mFlowContext);
            bundle.putSerializable(BundleKeys.DATE, date);
            bundle.putSerializable(BundleKeys.DAILY_AVAILABILITY_TIMELINE,
                    getAvailabilityForDate(date));
            final NavigationEvent.NavigateToPage navigationEvent =
                    new NavigationEvent.NavigateToPage(MainViewPage.EDIT_AVAILABLE_HOURS, bundle, true);
            navigationEvent.setReturnFragment(EditWeeklyAvailableHoursFragment.this,
                    RequestCode.EDIT_HOURS);
            bus.post(navigationEvent);
        }
    };
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;
    private boolean mIsCurrentWeekAndNextWeekInSync;
    private int mDefaultSelectedTab;

    private DailyAvailabilityTimeline getAvailabilityForDate(final Date date) {
        DailyAvailabilityTimeline availabilityForDate = null;
        if (mUpdatedAvailabilityTimelines != null) {
            availabilityForDate = mUpdatedAvailabilityTimelines.get(date);
        }
        if (availabilityForDate == null) {
            availabilityForDate = mProviderAvailability.getAvailabilityForDate(date);
        }
        return availabilityForDate;
    }

    private void updateAvailability(final DailyAvailabilityTimeline timeline) {
        mUpdatedAvailabilityTimelines.put(timeline.getDate(), timeline);
        mPagerAdapter.updateViewWithTimeline(timeline);
        callTargetFragmentResult(timeline);
    }

    private void callTargetFragmentResult(
            final DailyAvailabilityTimeline updatedAvailabilityTimeline) {
        if (getTargetFragment() != null) {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE, updatedAvailabilityTimeline);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    @OnClick(R.id.available_hours_info_banner_body)
    public void onInfoBannerClicked() {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.TARGET_URL, SETTING_AVAILABLE_HOURS_HELP_URL);
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.WEB_PAGE, arguments, true));
    }

    @OnClick(R.id.try_again_button)
    public void onRetryFetchAvailableHours() {
        requestAvailableHours();
    }

    @OnClick(R.id.copy_hours_button)
    public void onCopyHoursClicked() {
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.CopyCurrentWeekSelected(mFlowContext)));
        final AvailabilityTimelinesWrapper timelinesWrapper =
                createNextWeekTimelinesFromCurrentWeek();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.saveProviderAvailability(mProviderManager.getLastProviderId(), timelinesWrapper,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        for (DailyAvailabilityTimeline timeline : timelinesWrapper.getTimelines()) {
                            updateAvailability(timeline);
                        }
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
                });
    }

    @NonNull
    private AvailabilityTimelinesWrapper createNextWeekTimelinesFromCurrentWeek() {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        final WeeklyAvailabilityTimelinesWrapper currentWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelinesWrappers()
                        .get(CURRENT_WEEK_INDEX);
        final Calendar currentWeekDate = Calendar.getInstance(Locale.US);
        final Date startDate = currentWeekTimelines.getStartDate();
        final Date endDate = currentWeekTimelines.getEndDate();
        currentWeekDate.setTime(startDate);
        while (DateTimeUtils.daysBetween(currentWeekDate.getTime(), endDate) >= 0) {
            final Date date = currentWeekDate.getTime();
            final Calendar nextWeekDate = Calendar.getInstance(Locale.US);
            nextWeekDate.setTime(date);
            nextWeekDate.add(Calendar.DATE, DateTimeUtils.DAYS_IN_A_WEEK);

            final DailyAvailabilityTimeline availability = getAvailabilityForDate(date);
            final ArrayList<AvailabilityInterval> intervals = Lists.newArrayList();
            if (availability != null && availability.getAvailabilityIntervals() != null) {
                intervals.addAll(availability.getAvailabilityIntervals());
            }
            timelinesWrapper.addTimeline(nextWeekDate.getTime(), intervals);

            currentWeekDate.add(Calendar.DATE, 1);
        }
        return timelinesWrapper;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlowContext = getArguments().getString(BundleKeys.FLOW_CONTEXT);
        mProviderAvailability = (ProviderAvailability) getArguments()
                .getSerializable(BundleKeys.PROVIDER_AVAILABILITY);
        mUpdatedAvailabilityTimelines = (HashMap<Date, DailyAvailabilityTimeline>) getArguments()
                .getSerializable(BundleKeys.PROVIDER_AVAILABILITY_CACHE);
        if (mUpdatedAvailabilityTimelines == null) {
            mUpdatedAvailabilityTimelines = new HashMap<>();
        }
        mDefaultSelectedTab = getArguments().getBoolean(BundleKeys.SHOULD_DEFAULT_TO_NEXT_WEEK,
                false) ? NEXT_WEEK_INDEX : CURRENT_WEEK_INDEX;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view =
                inflater.inflate(R.layout.fragment_edit_weekly_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFetchErrorText.setText(R.string.error_fetching_available_hours);
        if (mProviderAvailability != null) {
            displayAvailableHours();
            displayTabs();
            updateCopyHoursButton();
        }
        com.handy.portal.library.util.TextUtils.stripUnderlines(mInfoBannerBody);
    }

    private void setIsCurrentWeekAndNextWeekInSync(final boolean value) {
        mIsCurrentWeekAndNextWeekInSync = value;
        updateCopyHoursButton();
    }

    private void updateCopyHoursButton() {
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_HOURS) {
            final DailyAvailabilityTimeline timeline = (DailyAvailabilityTimeline)
                    data.getSerializableExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
            if (timeline != null) {
                updateAvailability(timeline);
                setIsCurrentWeekAndNextWeekInSync(false);
            }
        }
    }

    private void displayAvailableHours() {
        mPagerAdapter = new TabAdapter(getActivity(), mProviderAvailability, mDateClickListener);
        for (DailyAvailabilityTimeline availability : mUpdatedAvailabilityTimelines.values()) {
            mPagerAdapter.updateViewWithTimeline(availability);
        }
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mWeekPageChangeListener);
    }

    private void displayTabs() {
        final WeeklyAvailabilityTimelinesWrapper currentWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelinesWrappers()
                        .get(CURRENT_WEEK_INDEX);
        final WeeklyAvailabilityTimelinesWrapper nextWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelinesWrappers().get(NEXT_WEEK_INDEX);
        final int selectedTabPosition = mTabLayout.getSelectedTabPosition();
        mTabLayout.removeAllTabs();
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(CURRENT_WEEK_INDEX).setCustomView(new TabWithDateRangeView(
                getActivity(), R.string.current_week, currentWeekTimelines));
        mTabLayout.getTabAt(NEXT_WEEK_INDEX).setCustomView(new TabWithDateRangeView(getActivity(),
                R.string.next_week, nextWeekTimelines));
        mViewPager.setCurrentItem(selectedTabPosition != TabAdapter.POSITION_NOT_FOUND ?
                selectedTabPosition : mDefaultSelectedTab);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.available_hours, true);
        if (mProviderAvailability == null) {
            requestAvailableHours();
        }
        else {
            // This is here to fix a UI duplication bug related to tabs
            displayTabs();
        }
    }

    private void requestAvailableHours() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.getProviderAvailability(mProviderManager.getLastProviderId(),
                new FragmentSafeCallback<ProviderAvailability>(this) {
                    @Override
                    public void onCallbackSuccess(final ProviderAvailability providerAvailability) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        mFetchErrorView.setVisibility(View.GONE);
                        mProviderAvailability = providerAvailability;
                        displayAvailableHours();
                        displayTabs();
                        updateCopyHoursButton();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        mFetchErrorView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private class TabAdapter extends PagerAdapter {
        public static final int POSITION_NOT_FOUND = -1;
        private List<WeeklyAvailableHoursView> mViews;

        TabAdapter(final Context context,
                   final ProviderAvailability providerAvailability,
                   final DateClickListener dateClickListener) {
            mViews = new ArrayList<>();
            for (WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper :
                    providerAvailability.getWeeklyAvailabilityTimelinesWrappers()) {
                mViews.add(new WeeklyAvailableHoursView(context, weeklyAvailabilityTimelinesWrapper,
                        dateClickListener));
            }
        }

        public void updateViewWithTimeline(final DailyAvailabilityTimeline timeline) {
            for (WeeklyAvailableHoursView weekView : mViews) {
                final AvailableHoursWithDateView view = weekView.getViewForDate(timeline.getDate());
                if (view != null) {
                    view.updateTimelines(timeline);
                }
            }
        }

        @Override
        public int getItemPosition(final Object object) {
            int index = mViews.indexOf(object);
            if (index == POSITION_NOT_FOUND) {
                return POSITION_NONE;
            }
            else {
                return index;
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final View view = getItemAt(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(getItemAt(position));
        }

        private View getItemAt(final int position) {
            return mViews.get(position);
        }
    }
}
