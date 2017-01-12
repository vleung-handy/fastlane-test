package com.handy.portal.proavailability.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.ui.view.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAvailableHoursFragment extends ActionBarFragment
{
    private static final int DEFAULT_START_TIME = 7;
    private static final int DEFAULT_END_TIME = 23;
    @BindView(R.id.time_picker)
    HandyTimePicker mTimePicker;
    @BindView(R.id.start_time)
    TextView mStartTime;
    @BindView(R.id.end_time)
    TextView mEndTime;
    @BindView(R.id.end_time_holder)
    ViewGroup mEndTimeHolder;
    @BindView(R.id.reset_time_range)
    View mResetTimeRangeButton;
    @BindView(R.id.save)
    Button mSaveButton;
    @BindColor(R.color.black)
    int mBlack;
    @BindColor(R.color.white)
    int mWhite;

    private Date mDate;
    private DailyAvailabilityTimeline mAvailabilityTimeline;

    @OnClick(R.id.reset_time_range)
    public void resetTimeRange()
    {
        uneditStartTime();
        uneditEndTime();
        mTimePicker.setSelectionType(null);
        mResetTimeRangeButton.setVisibility(View.GONE);
        mTimePicker.clearSelection();
    }

    @OnClick(R.id.start_time)
    public void onStartTimeClicked()
    {
        mTimePicker.setSelectionType(HandyTimePicker.SelectionType.START_TIME);
    }

    @OnClick(R.id.end_time_holder)
    public void onEndTimeClicked()
    {
        mTimePicker.setSelectionType(HandyTimePicker.SelectionType.END_TIME);
    }

    @OnClick(R.id.save)
    public void onSave()
    {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        if (mTimePicker.hasSelectedRange())
        {
            final ArrayList<AvailabilityInterval> intervals = new ArrayList<>();
            final int selectedStartTime = mTimePicker.getSelectedStartTime();
            final int selectedEndTime = mTimePicker.getSelectedEndTime();
            intervals.add(new AvailabilityInterval(selectedStartTime, selectedEndTime));
            timelinesWrapper.addTimeline(mDate, intervals);
        }
    }

    public void editStartTime()
    {
        mStartTime.setBackgroundResource(R.color.tertiary_gray);
        mStartTime.setTextColor(mWhite);
        uneditEndTime();
    }

    private void editEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.tertiary_gray);
        mEndTime.setTextColor(mWhite);
        uneditStartTime();
    }

    private void uneditStartTime()
    {
        mStartTime.setBackgroundResource(R.color.handy_bg);
        mStartTime.setTextColor(mBlack);
    }

    private void uneditEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.handy_bg);
        mEndTime.setTextColor(mBlack);
    }

    private void updateStartTime(final int time)
    {
        updateTime(mStartTime, time, R.string.start_time);
    }

    private void updateEndTime(final int time)
    {
        updateTime(mEndTime, time, R.string.end_time);
    }

    private void updateTime(final TextView timeView, final int time,
                            @StringRes final int emptyStringResId)
    {
        if (time == HandyTimePicker.NO_TIME_SELECTED)
        {
            timeView.setText(emptyStringResId);
        }
        else
        {
            final Date date = DateTimeUtils.parseDateString(
                    String.valueOf(time), DateTimeUtils.HOUR_INT_FORMATTER);
            timeView.setText(DateTimeUtils.formatDateTo12HourClock(date));
        }
        updateResetTimeRangeButtonVisibility();
        updateSaveButtonVisibility();
    }

    private void updateResetTimeRangeButtonVisibility()
    {
        if (mTimePicker.hasSelectedRange())
        {
            mResetTimeRangeButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mResetTimeRangeButton.setVisibility(View.GONE);
        }
    }

    private void updateSaveButtonVisibility()
    {
        if (isOriginalIntervalSelected() || mTimePicker.hasSelectedOneTime())
        {
            mSaveButton.setVisibility(View.GONE);
        }
        else
        {
            mSaveButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean isOriginalIntervalSelected()
    {
        final AvailabilityInterval originalInterval = getFirstAvailabilityInterval();
        if (originalInterval == null)
        {
            return !mTimePicker.hasSelectedRange();
        }
        else
        {
            return originalInterval.getStartTimeInt() == mTimePicker.getSelectedStartTime()
                    && originalInterval.getEndTimeInt() == mTimePicker.getSelectedEndTime();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDate = (Date) getArguments().getSerializable(BundleKeys.DATE);
        mAvailabilityTimeline = (DailyAvailabilityTimeline) getArguments()
                .getSerializable(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_edit_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final String dateFormatted = DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mDate);
        setActionBar(getString(R.string.hours_for_date_formatted, dateFormatted), true);
        initTimePicker();
        initTimeRange();
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
    }

    private void initTimePicker()
    {
        mTimePicker.setTimeRange(DEFAULT_START_TIME, DEFAULT_END_TIME);
        mTimePicker.setCallbacks(new HandyTimePicker.Callbacks()
        {
            @Override
            public void onRangeUpdated(final int startTime, final int endTime)
            {
                updateStartTime(startTime);
                updateEndTime(endTime);
            }

            @Override
            public void onSelectionTypeChanged(final HandyTimePicker.SelectionType selectionType)
            {
                if (selectionType == HandyTimePicker.SelectionType.START_TIME)
                {
                    editStartTime();
                }
                else if (selectionType == HandyTimePicker.SelectionType.END_TIME)
                {
                    editEndTime();
                }
                else if (selectionType == null)
                {
                    uneditStartTime();
                    uneditEndTime();
                }
            }
        });
    }

    private void initTimeRange()
    {
        final AvailabilityInterval interval = getFirstAvailabilityInterval();
        if (interval != null)
        {
            if (mTimePicker.selectTimeRange(interval.getStartTimeInt(), interval.getEndTimeInt()))
            {
                updateStartTime(interval.getStartTimeInt());
                updateEndTime(interval.getEndTimeInt());
                mTimePicker.setSelectionType(HandyTimePicker.SelectionType.END_TIME);
            }
        }
    }

    @Nullable
    private AvailabilityInterval getFirstAvailabilityInterval()
    {
        if (mAvailabilityTimeline != null
                && mAvailabilityTimeline.getAvailabilityIntervals() != null
                && !mAvailabilityTimeline.getAvailabilityIntervals().isEmpty())
        {
            return mAvailabilityTimeline.getAvailabilityIntervals().get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onDestroyView()
    {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
