package com.handy.portal.proavailability.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.ui.view.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

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
    @BindColor(R.color.black)
    int mBlack;
    @BindColor(R.color.white)
    int mWhite;

    private Date mDate;
    private DailyAvailabilityTimeline mAvailabilityTimeline;
    private TimeEditType mTimeEditType;
    private HandyTimePicker.TimeClickListener mTimeClickListener = new HandyTimePicker.TimeClickListener()
    {
        @Override
        public void onTimeClick(final int time)
        {
            int targetTime = time;

            // Default selection to start time.
            if (mTimeEditType == null)
            {
                mTimeEditType = TimeEditType.START_TIME;
            }
            // Default selection to end time if a start time has been selected but end time hasn't
            // been selected.
            else if (mTimeEditType == TimeEditType.START_TIME
                    && mTimePicker.hasSelectedStartTime()
                    && !mTimePicker.hasSelectedEndTime())
            {
                mTimeEditType = TimeEditType.END_TIME;
            }

            // Tapping selected times on the picker will cancel the range and leave the start time,
            // or deselect a single selection.
            if (mTimePicker.getSelectedStartTime() == targetTime
                    || mTimePicker.getSelectedEndTime() == targetTime)
            {
                if (mTimePicker.hasSelectedRange()) // a range is selected
                {
                    // Reset selection then reselect the original selected start time.
                    final int selectedStartTime = mTimePicker.getSelectedStartTime();
                    resetTimeRange();
                    targetTime = selectedStartTime;
                }
                else // a single time is currently selected
                {
                    resetTimeRange();
                    return;
                }
            }

            // Tapping an earlier time when there is already a selected start time will force the
            // editing to start time.
            if (mTimePicker.hasSelectedStartTime()
                    && targetTime < mTimePicker.getSelectedStartTime())
            {
                mTimeEditType = TimeEditType.START_TIME;
            }

            // Tapping a later time when there is already a selected end time will force the editing
            // to end time.
            if (mTimePicker.hasSelectedEndTime() && targetTime > mTimePicker.getSelectedEndTime())
            {
                mTimeEditType = TimeEditType.END_TIME;
            }

            // Select start time if applicable.
            if (mTimeEditType == TimeEditType.START_TIME && mTimePicker.selectStartTime(targetTime))
            {
                updateStartTime(targetTime);
            }

            // Select end time if applicable.
            if (mTimeEditType == TimeEditType.END_TIME && mTimePicker.selectEndTime(targetTime))
            {
                updateEndTime(targetTime);
            }
        }
    };


    private enum TimeEditType
    {
        START_TIME, END_TIME
    }

    @OnClick(R.id.reset_time_range)
    public void resetTimeRange()
    {
        uneditStartTime();
        uneditEndTime();
        mStartTime.setText(R.string.start_time);
        mEndTime.setText(R.string.end_time);
        mTimeEditType = TimeEditType.START_TIME;
        mResetTimeRangeButton.setVisibility(View.GONE);
        mTimePicker.resetSelection();
    }

    @OnClick(R.id.start_time)
    public void editStartTime()
    {
        mStartTime.setBackgroundResource(R.color.tertiary_gray);
        mStartTime.setTextColor(mWhite);
        uneditEndTime();
        mTimeEditType = TimeEditType.START_TIME;
    }

    @OnClick(R.id.end_time_holder)
    public void editEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.tertiary_gray);
        mEndTime.setTextColor(mWhite);
        uneditStartTime();
        mTimeEditType = TimeEditType.END_TIME;
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
        editStartTime();
        updateTime(mStartTime, time);
    }

    private void updateEndTime(final int time)
    {
        editEndTime();
        updateTime(mEndTime, time);
    }

    private void updateTime(final TextView timeView, final int time)
    {
        final Date date = DateTimeUtils.parseDateString(
                String.valueOf(time), DateTimeUtils.HOUR_INT_FORMATTER);
        timeView.setText(DateTimeUtils.formatDateTo12HourClock(date));
        if (mTimePicker.hasSelectedRange())
        {
            mResetTimeRangeButton.setVisibility(View.VISIBLE);
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
        mTimePicker.setTimeClickListener(mTimeClickListener);
    }

    private void initTimeRange()
    {
        if (mAvailabilityTimeline != null
                && mAvailabilityTimeline.getAvailabilityIntervals() != null
                && !mAvailabilityTimeline.getAvailabilityIntervals().isEmpty())
        {
            final AvailabilityInterval interval =
                    mAvailabilityTimeline.getAvailabilityIntervals().get(0);
            if (mTimePicker.selectTimeRange(interval.getStartTimeInt(), interval.getEndTimeInt()))
            {
                updateStartTime(interval.getStartTimeInt());
                updateEndTime(interval.getEndTimeInt());
            }
        }
    }

    @Override
    public void onDestroyView()
    {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
