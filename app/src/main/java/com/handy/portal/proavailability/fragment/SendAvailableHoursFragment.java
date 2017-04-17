package com.handy.portal.proavailability.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
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
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.SendAvailabilityLog;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.ProviderAvailability;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.AvailableHoursWithDateStaticView;
import com.handy.portal.proavailability.view.WeeklyAvailableHoursCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendAvailableHoursFragment extends ActionBarFragment {
    private static final int NEXT_WEEK_INDEX = 1;

    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.content)
    View mContent;
    @BindView(R.id.no_availability_view)
    View mNoAvailabilityView;
    @BindView(R.id.send_availability_view)
    View mSendAvailabilityView;
    @BindView(R.id.header_title)
    TextView mHeaderTitle;
    @BindView(R.id.header_subtitle)
    TextView mHeaderSubtitle;
    @BindView(R.id.availability_pager)
    ViewPager mAvailabilityPager;
    @BindView(R.id.send_availability_button)
    Button mSendButton;
    @BindDimen(R.dimen.default_margin_half)
    int mGapMargin;
    @BindDimen(R.dimen.default_margin_double)
    int mCardMargin;
    @BindColor(R.color.handy_blue)
    int mBlueColor;

    private Booking mBooking;
    private ProviderAvailability mAvailability;
    private HashMap<Date, DailyAvailabilityTimeline> mUpdatedAvailabilityTimelines;
    private WeeklyAvailableHoursCardView.EditListener mEditHoursClickListener;
    private WeeklyAvailableHoursPagerAdapter mAvailabilityPagerAdapter;
    private Set<Date> mDatesWithoutAvailability;

    {
        mEditHoursClickListener = new WeeklyAvailableHoursCardView.EditListener() {
            @Override
            public void onEdit(final int cardIndex) {
                navigateToEditWeeklyAvailableHours(cardIndex == NEXT_WEEK_INDEX);
            }
        };
    }

    @OnClick(R.id.update_availability_button)
    void updateAvailability() {
        navigateToEditWeeklyAvailableHours(false);
    }

    private void navigateToEditWeeklyAvailableHours(final boolean shouldDefaultToNextWeek) {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.FLOW_CONTEXT, EventContext.SEND_AVAILABILITY);
        arguments.putBoolean(BundleKeys.SHOULD_DEFAULT_TO_NEXT_WEEK, shouldDefaultToNextWeek);
        arguments.putSerializable(BundleKeys.PROVIDER_AVAILABILITY, mAvailability);
        arguments.putSerializable(BundleKeys.PROVIDER_AVAILABILITY_CACHE,
                mUpdatedAvailabilityTimelines);
        final NavigationEvent.NavigateToPage navigationEvent =
                new NavigationEvent.NavigateToPage(MainViewPage.EDIT_WEEKLY_AVAILABLE_HOURS,
                        arguments, true);
        navigationEvent.setReturnFragment(this, RequestCode.EDIT_HOURS);
        bus.post(navigationEvent);
    }

    @OnClick(R.id.send_availability_button)
    void sendAvailability() {
        final Booking.Action action = mBooking.getAction(Booking.Action.ACTION_SEND_TIMES);
        if (action == null) { return; }
        final String providerId = mProviderManager.getLastProviderId();
        final String providerRequestId = action.getProviderRequest().getId();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.sendAvailability(providerId, providerRequestId, null,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        showToast(getString(R.string.send_available_hours_send_success_formatted,
                                mBooking.getRequestAttributes().getCustomerName()));
                        bus.post(new HandyEvent.AvailableHoursSent(mBooking));
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        Snackbar.make(mContent, R.string.send_available_hours_send_error,
                                Snackbar.LENGTH_LONG).show();
                    }
                });
        bus.post(new LogEvent.AddLogEvent(
                new SendAvailabilityLog.SendAvailabilitySubmitted(mBooking)));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mUpdatedAvailabilityTimelines = new HashMap<>();
        mDatesWithoutAvailability = new HashSet<>();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view =
                inflater.inflate(R.layout.fragment_send_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.send_alternate_times, true);
        initHeader();
        initAvailabilityPager();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_HOURS) {
            final DailyAvailabilityTimeline availability = (DailyAvailabilityTimeline)
                    data.getSerializableExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
            if (availability != null) {
                updateAvailability(availability);
            }
        }
    }

    private void updateAvailability(final DailyAvailabilityTimeline availability) {
        mUpdatedAvailabilityTimelines.put(availability.getDate(), availability);
        mDatesWithoutAvailability.remove(availability.getDate());

        if (mAvailabilityPagerAdapter != null) {
            mAvailabilityPagerAdapter.updateViewWithTimeline(availability);
        }
        else {
            initAvailabilityPager();
        }

        updateSendButtonState();
        callTargetFragmentResult(availability);
    }

    private void updateSendButtonState() {
        mSendButton.setEnabled(mDatesWithoutAvailability.isEmpty());
    }

    private void callTargetFragmentResult(
            final DailyAvailabilityTimeline updatedAvailabilityTimeline) {
        if (getTargetFragment() != null) {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE, updatedAvailabilityTimeline);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    private void initHeader() {
        mHeaderTitle.setText(mBooking.getRequestAttributes().getCustomerName());
        mHeaderSubtitle.setText(getString(R.string.original_time_formatted,
                DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mBooking.getStartDate()),
                DateTimeUtils.formatDateTo12HourClock(mBooking.getStartDate()),
                DateTimeUtils.formatDateTo12HourClock(mBooking.getEndDate())));
    }

    private void initAvailabilityPager() {
        if (mAvailability == null) {
            loadProviderAvailability();
        }
        else if (mAvailability.hasAvailableHours() || !mUpdatedAvailabilityTimelines.isEmpty()) {
            mNoAvailabilityView.setVisibility(View.GONE);
            mSendAvailabilityView.setVisibility(View.VISIBLE);
            mAvailabilityPagerAdapter = new WeeklyAvailableHoursPagerAdapter(getActivity(),
                    mAvailability, mEditHoursClickListener);
            for (DailyAvailabilityTimeline availability : mUpdatedAvailabilityTimelines.values()) {
                mAvailabilityPagerAdapter.updateViewWithTimeline(availability);
            }
            mAvailabilityPager.setAdapter(mAvailabilityPagerAdapter);
            mAvailabilityPager.setPageMargin(mGapMargin - (mCardMargin * 2));
            updateSendButtonState();
        }
        else {
            mSendAvailabilityView.setVisibility(View.GONE);
            mNoAvailabilityView.setVisibility(View.VISIBLE);
            updateSendButtonState();
        }
    }

    private void loadProviderAvailability() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.getProviderAvailability(mProviderManager.getLastProviderId(),
                new FragmentSafeCallback<ProviderAvailability>(this) {
                    @Override
                    public void onCallbackSuccess(final ProviderAvailability providerAvailability) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        mAvailability = providerAvailability;
                        mUpdatedAvailabilityTimelines.clear();
                        initDatesWithoutAvailability();
                        initAvailabilityPager();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        Snackbar.make(mContent, R.string.send_available_hours_loading_error,
                                Snackbar.LENGTH_INDEFINITE)
                                .setActionTextColor(mBlueColor)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        loadProviderAvailability();
                                    }
                                })
                                .show();
                    }
                });
    }

    private void initDatesWithoutAvailability() {
        mDatesWithoutAvailability.clear();
        for (final WeeklyAvailabilityTimelinesWrapper weekAvailability
                : mAvailability.getWeeklyAvailabilityTimelinesWrappers()) {
            final Calendar calendar = Calendar.getInstance(Locale.US);
            final Date startDate = weekAvailability.getStartDate();
            final Date endDate = weekAvailability.getEndDate();
            calendar.setTime(startDate);
            while (DateTimeUtils.daysBetween(calendar.getTime(), endDate) >= 0) {
                final Date date = calendar.getTime();
                if (!DateTimeUtils.isDaysPast(date)) {
                    final DailyAvailabilityTimeline availability =
                            weekAvailability.getAvailabilityForDate(date);
                    if (availability == null) {
                        mDatesWithoutAvailability.add(date);
                    }
                }
                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    private static class WeeklyAvailableHoursPagerAdapter extends PagerAdapter {
        public static final int[] WEEK_TITLE_RES_IDS = {R.string.current_week, R.string.next_week};
        public static final int POSITION_NOT_FOUND = -1;
        private List<WeeklyAvailableHoursCardView> mViews;

        WeeklyAvailableHoursPagerAdapter(
                final Context context,
                final ProviderAvailability availability,
                final WeeklyAvailableHoursCardView.EditListener editHoursClickListener
        ) {
            mViews = new ArrayList<>();
            ArrayList<WeeklyAvailabilityTimelinesWrapper> weeklyTimelinesList =
                    availability.getWeeklyAvailabilityTimelinesWrappers();
            for (int i = 0; i < weeklyTimelinesList.size(); i++) {
                final WeeklyAvailabilityTimelinesWrapper weeklyAvailability =
                        weeklyTimelinesList.get(i);
                final int weekTitleResId = i < WEEK_TITLE_RES_IDS.length ?
                        WEEK_TITLE_RES_IDS[i] : R.string.special_empty_string;
                mViews.add(new WeeklyAvailableHoursCardView(context, weekTitleResId,
                        weeklyAvailability, editHoursClickListener, i));
            }
        }

        public void updateViewWithTimeline(final DailyAvailabilityTimeline timeline) {
            for (WeeklyAvailableHoursCardView weekView : mViews) {
                final AvailableHoursWithDateStaticView view =
                        weekView.getViewForDate(timeline.getDate());
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
