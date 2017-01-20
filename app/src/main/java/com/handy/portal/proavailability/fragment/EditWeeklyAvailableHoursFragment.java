package com.handy.portal.proavailability.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.ProviderAvailability;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.AvailableHoursWithDateView;
import com.handy.portal.proavailability.view.AvailableTimeSlotView.RemoveTimeSlotListener;
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

public class EditWeeklyAvailableHoursFragment extends ActionBarFragment
{
    private static final int CURRENT_WEEK_INDEX = 0;
    private static final int NEXT_WEEK_INDEX = 1;
    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.available_hours_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.available_hours_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.copy_hours_button)
    Button mCopyHoursButton;
    @BindColor(R.color.black)
    int mBlackColorValue;
    @BindColor(R.color.error_red)
    int mRedColorValue;

    private ProviderAvailability mProviderAvailability;
    private TabAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mWeekPageChangeListener =
            new ViewPager.OnPageChangeListener()
            {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                                           final int positionOffsetPixels)
                {
                    // do nothing
                }

                @Override
                public void onPageSelected(final int position)
                {
                    mCopyHoursButton.setVisibility(position == NEXT_WEEK_INDEX ?
                            View.VISIBLE : View.GONE);
                }

                @Override
                public void onPageScrollStateChanged(final int state)
                {
                    // do nothing
                }
            };
    private RemoveTimeSlotListener mRemoveTimeSlotListener = new RemoveTimeSlotListener()
    {
        @Override
        public void onRemoveClicked(final Date date, final AvailabilityInterval interval)
        {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(true)
                    .setMessage(R.string.time_slot_removal_prompt)
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            removeInterval(date, interval);
                        }
                    })
                    .setNegativeButton(R.string.keep, null)
                    .create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                public void onShow(final DialogInterface dialogInterface)
                {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(mRedColorValue);
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(mBlackColorValue);
                }
            });
            alertDialog.show();
        }
    };
    private DateClickListener mDateClickListener = new DateClickListener()
    {
        @Override
        public void onDateClicked(final Date date)
        {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKeys.DATE, date);
            bundle.putSerializable(BundleKeys.DAILY_AVAILABILITY_TIMELINE,
                    getAvailablityForDate(date));
            final NavigationEvent.NavigateToPage navigationEvent =
                    new NavigationEvent.NavigateToPage(MainViewPage.EDIT_AVAILABLE_HOURS, bundle, true);
            navigationEvent.setReturnFragment(EditWeeklyAvailableHoursFragment.this,
                    RequestCode.EDIT_HOURS);
            bus.post(navigationEvent);
        }
    };
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;
    private boolean mIsCurrentWeekAndNextWeekInSync;

    private DailyAvailabilityTimeline getAvailablityForDate(final Date date)
    {
        DailyAvailabilityTimeline availabilityForDate = null;
        if (mUpdatedAvailabilityTimelines != null)
        {
            availabilityForDate = mUpdatedAvailabilityTimelines.get(date);
        }
        if (availabilityForDate == null)
        {
            availabilityForDate =
                    mProviderAvailability.getAvailabilityForDate(date);
        }
        return availabilityForDate;
    }

    private void removeInterval(final Date date, final AvailabilityInterval interval)
    {
        final DailyAvailabilityTimeline availability =
                mProviderAvailability.getAvailabilityForDate(date);
        final ArrayList<AvailabilityInterval> intervals = new ArrayList<>();
        if (availability != null && availability.getAvailabilityIntervals() != null)
        {
            intervals.addAll(availability.getAvailabilityIntervals());
            intervals.remove(interval);
        }
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        timelinesWrapper.addTimeline(date, intervals);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.saveProviderAvailability(mProviderManager.getLastProviderId(), timelinesWrapper,
                new FragmentSafeCallback<Void>(this)
                {
                    @Override
                    public void onCallbackSuccess(final Void response)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        final DailyAvailabilityTimeline updatedAvailabilityTimeline =
                                new DailyAvailabilityTimeline(date, intervals);
                        updateAvailability(updatedAvailabilityTimeline);
                        setIsCurrentWeekAndNextWeekInSync(false);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        String message = error.getMessage();
                        if (TextUtils.isEmpty(message))
                        {
                            message = getString(R.string.an_error_has_occurred);
                        }
                        showToast(message);
                    }
                });
    }

    private void updateAvailability(final DailyAvailabilityTimeline timeline)
    {
        mUpdatedAvailabilityTimelines.put(timeline.getDate(), timeline);
        mPagerAdapter.updateViewWithTimeline(timeline);
        callTargetFragmentResult(timeline);
    }

    private void callTargetFragmentResult(
            final DailyAvailabilityTimeline updatedAvailabilityTimeline)
    {
        if (getTargetFragment() != null)
        {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE, updatedAvailabilityTimeline);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    @OnClick(R.id.copy_hours_button)
    public void onCopyHoursClicked()
    {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        final List<DailyAvailabilityTimeline> timelines = new ArrayList<>();

        final WeeklyAvailabilityTimelinesWrapper currentWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelineWrappers()
                        .get(CURRENT_WEEK_INDEX);
        final Calendar currentWeekDate = Calendar.getInstance(Locale.US);
        final Date startDate = currentWeekTimelines.getStartDate();
        final Date endDate = currentWeekTimelines.getEndDate();
        currentWeekDate.setTime(startDate);
        while (DateTimeUtils.daysBetween(currentWeekDate.getTime(), endDate) >= 0)
        {
            final Date date = currentWeekDate.getTime();
            final Calendar nextWeekDate = Calendar.getInstance(Locale.US);
            nextWeekDate.setTime(date);
            nextWeekDate.add(Calendar.DATE, DateTimeUtils.DAYS_IN_A_WEEK);

            final DailyAvailabilityTimeline availability =
                    currentWeekTimelines.getAvailabilityForDate(date);
            final ArrayList<AvailabilityInterval> intervals = Lists.newArrayList();
            if (availability != null && availability.getAvailabilityIntervals() != null)
            {
                intervals.addAll(availability.getAvailabilityIntervals());
            }
            timelinesWrapper.addTimeline(nextWeekDate.getTime(), intervals);
            timelines.add(new DailyAvailabilityTimeline(nextWeekDate.getTime(), intervals));

            currentWeekDate.add(Calendar.DATE, 1);
        }

        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.saveProviderAvailability(mProviderManager.getLastProviderId(), timelinesWrapper,
                new FragmentSafeCallback<Void>(this)
                {
                    @Override
                    public void onCallbackSuccess(final Void response)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        for (DailyAvailabilityTimeline timeline : timelines)
                        {
                            updateAvailability(timeline);
                        }
                        setIsCurrentWeekAndNextWeekInSync(true);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        String message = error.getMessage();
                        if (TextUtils.isEmpty(message))
                        {
                            message = getString(R.string.an_error_has_occurred);
                        }
                        showToast(message);
                    }
                });
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mProviderAvailability = (ProviderAvailability) getArguments()
                .getSerializable(BundleKeys.PROVIDER_AVAILABILITY);
        mUpdatedAvailabilityTimelines = (HashMap<Date, DailyAvailabilityTimeline>) getArguments()
                .getSerializable(BundleKeys.PROVIDER_AVAILABILITY_CACHE);
        if (mUpdatedAvailabilityTimelines == null)
        {
            mUpdatedAvailabilityTimelines = new HashMap<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        final View view =
                inflater.inflate(R.layout.fragment_edit_weekly_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (mProviderAvailability != null)
        {
            displayAvailableHours();
            displayTabs();
            updateCopyHoursButton();
        }
    }

    private void setIsCurrentWeekAndNextWeekInSync(final boolean value)
    {
        mIsCurrentWeekAndNextWeekInSync = value;
        updateCopyHoursButton();
    }

    private void updateCopyHoursButton()
    {
        if (mIsCurrentWeekAndNextWeekInSync)
        {
            mCopyHoursButton.setText(R.string.copied);
            mCopyHoursButton.setEnabled(false);
        }
        else
        {
            mCopyHoursButton.setText(R.string.copy_hours_from_current_week);
            mCopyHoursButton.setEnabled(true);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_HOURS)
        {
            final DailyAvailabilityTimeline timeline = (DailyAvailabilityTimeline)
                    data.getSerializableExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
            if (timeline != null)
            {
                updateAvailability(timeline);
                setIsCurrentWeekAndNextWeekInSync(false);
            }
        }
    }

    private void displayAvailableHours()
    {
        mTabLayout.setupWithViewPager(mViewPager);
        mPagerAdapter = new TabAdapter(getActivity(), mProviderAvailability, mDateClickListener,
                mRemoveTimeSlotListener);
        for (DailyAvailabilityTimeline availability : mUpdatedAvailabilityTimelines.values())
        {
            mPagerAdapter.updateViewWithTimeline(availability);
        }
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mWeekPageChangeListener);
    }

    private void displayTabs()
    {
        final WeeklyAvailabilityTimelinesWrapper currentWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelineWrappers()
                        .get(CURRENT_WEEK_INDEX);
        final WeeklyAvailabilityTimelinesWrapper nextWeekTimelines =
                mProviderAvailability.getWeeklyAvailabilityTimelineWrappers().get(NEXT_WEEK_INDEX);
        mTabLayout.getTabAt(0).setCustomView(new TabWithDateRangeView(getActivity(),
                R.string.current_week, currentWeekTimelines));
        mTabLayout.getTabAt(1).setCustomView(new TabWithDateRangeView(getActivity(),
                R.string.next_week, nextWeekTimelines));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.available_hours, true);
        if (mProviderAvailability == null)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            requestAvailableHours();
        }
    }

    private void requestAvailableHours()
    {
        dataManager.getProviderAvailability(mProviderManager.getLastProviderId(),
                new FragmentSafeCallback<ProviderAvailability>(this)
                {
                    @Override
                    public void onCallbackSuccess(final ProviderAvailability providerAvailability)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        mProviderAvailability = providerAvailability;
                        displayAvailableHours();
                        displayTabs();
                        updateCopyHoursButton();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        // FIXME: Show error state
                    }
                });
    }

    private class TabAdapter extends PagerAdapter
    {
        private static final int POSITION_NOT_FOUND = -1;
        private List<WeeklyAvailableHoursView> mViews;

        TabAdapter(final Context context,
                   final ProviderAvailability providerAvailability,
                   final DateClickListener dateClickListener,
                   final RemoveTimeSlotListener removeTimeSlotListener)
        {
            mViews = new ArrayList<>();
            for (WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper :
                    providerAvailability.getWeeklyAvailabilityTimelineWrappers())
            {
                mViews.add(new WeeklyAvailableHoursView(context, weeklyAvailabilityTimelinesWrapper,
                        dateClickListener, removeTimeSlotListener));
            }
        }

        public void updateViewWithTimeline(final DailyAvailabilityTimeline timeline)
        {
            for (WeeklyAvailableHoursView weekView : mViews)
            {
                final AvailableHoursWithDateView view = weekView.getViewForDate(timeline.getDate());
                if (view != null)
                {
                    view.updateTimelines(timeline);
                }
            }
        }

        @Override
        public int getItemPosition(final Object object)
        {
            int index = mViews.indexOf(object);
            if (index == POSITION_NOT_FOUND)
            {
                return POSITION_NONE;
            }
            else
            {
                return index;
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position)
        {
            final View view = getItemAt(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount()
        {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(getItemAt(position));
        }

        private View getItemAt(final int position)
        {
            return mViews.get(position);
        }
    }
}
