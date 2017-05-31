package com.handy.portal.availability.fragment;

import android.content.Context;
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
import com.handy.portal.availability.AvailabilityEvent;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.AvailableHoursWithDateView;
import com.handy.portal.availability.view.TabWithDateRangeView;
import com.handy.portal.availability.view.WeeklyAvailableHoursView;
import com.handy.portal.availability.view.WeeklyAvailableHoursView.DateClickListener;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Inject
    ProviderManager mProviderManager;
    @Inject
    AvailabilityManager mAvailabilityManager;


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
            final NavigationEvent.NavigateToPage navigationEvent =
                    new NavigationEvent.NavigateToPage(MainViewPage.EDIT_AVAILABLE_HOURS, bundle, true);
            bus.post(navigationEvent);
        }
    };
    private boolean mIsCurrentWeekAndNextWeekInSync;
    private int mDefaultSelectedTab;

    @Subscribe
    public void onAvailabilityTimelineUpdated(final AvailabilityEvent.TimelineUpdated event) {
        mPagerAdapter.updateViewWithTimeline(event.getTimeline().getDate(), event.getTimeline());
        setIsCurrentWeekAndNextWeekInSync(false);
    }

    @OnClick(R.id.available_hours_info_banner_body)
    public void onInfoBannerClicked() {
        final Bundle arguments = new Bundle();
        arguments.putString(
                BundleKeys.TARGET_URL,
                dataManager.getBaseUrl() + HelpCenterConstants.SETTING_HOURS_INFO_PATH
        );
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
        final Availability.Wrapper.Timelines timelinesWrapper =
                createNextWeekTimelinesWrapperFromCurrentWeekRange();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

        dataManager.saveAvailability(mProviderManager.getLastProviderId(), timelinesWrapper,
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
                });
    }

    @NonNull
    private Availability.Wrapper.Timelines createNextWeekTimelinesWrapperFromCurrentWeekRange() {
        final Availability.Wrapper.Timelines timelinesWrapper = new Availability.Wrapper.Timelines();
        final Availability.Range currentWeekRange = mAvailabilityManager.getCurrentWeekRange();
        for (final Date date : currentWeekRange.dates()) {
            final Calendar nextWeekDate = Calendar.getInstance(Locale.US);
            nextWeekDate.setTime(date);
            nextWeekDate.add(Calendar.DATE, DateTimeUtils.DAYS_IN_A_WEEK);

            final Availability.Timeline timeline = mAvailabilityManager.getTimelineForDate(date);
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
        if (mPagerAdapter != null) {
            displayAvailableHours();
            displayTabs();
            updateCopyHoursButton();
        }
        com.handy.portal.library.util.TextUtils.stripUnderlines(mInfoBannerBody);
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
        mPagerAdapter = new TabAdapter(
                getActivity(), currentWeekRange, nextWeekRange, mDateClickListener
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
        mViewPager.setCurrentItem(selectedTabPosition != TabAdapter.POSITION_NOT_FOUND ?
                selectedTabPosition : mDefaultSelectedTab);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.available_hours, true);
        if (mPagerAdapter == null) {
            requestAvailableHours();
        }
        else {
            // This is here to fix a UI duplication bug related to tabs
            displayTabs();
        }
    }

    private void requestAvailableHours() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mAvailabilityManager.getAvailability(new FragmentSafeCallback<Void>(this) {
            @Override
            public void onCallbackSuccess(final Void response) {
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                mFetchErrorView.setVisibility(View.GONE);
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
        private static final int POSITION_NOT_FOUND = -1;
        private List<WeeklyAvailableHoursView> mViews;

        TabAdapter(final Context context,
                   final Availability.Range currentWeekRange,
                   final Availability.Range nextWeekRange,
                   final DateClickListener dateClickListener) {
            mViews = new ArrayList<>();
            mViews.add(new WeeklyAvailableHoursView(context, currentWeekRange, dateClickListener));
            mViews.add(new WeeklyAvailableHoursView(context, nextWeekRange, dateClickListener));
        }

        public void updateViewWithTimeline(
                @NonNull final Date date,
                @Nullable final Availability.Timeline timeline
        ) {
            for (WeeklyAvailableHoursView weekView : mViews) {
                final AvailableHoursWithDateView view = weekView.getViewForDate(date);
                if (view != null) {
                    view.updateIntervals(timeline);
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
