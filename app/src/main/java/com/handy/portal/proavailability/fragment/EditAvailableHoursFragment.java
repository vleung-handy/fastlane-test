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
import com.handy.portal.library.ui.view.HandyTimePickerCell;
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
    private View.OnClickListener mTimeClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            int targetTime = ((HandyTimePickerCell) view).getTime();

            // Default selection to start time.
            if (mTimeEditType == null)
            {
                mTimeEditType = TimeEditType.START_TIME;
            }
            // Default selection to end time if a start time has been selected bu end time hasn't
            // been selected.
            else if (mTimeEditType == TimeEditType.START_TIME
                    && mTimePicker.getSelectedStartTime() != HandyTimePicker.NO_TIME_SELECTED
                    && mTimePicker.getSelectedEndTime() == HandyTimePicker.NO_TIME_SELECTED)
            {
                mTimeEditType = TimeEditType.END_TIME;
            }

            // Tapping selected times on the picker will cancel the range and leave the start time,
            // or deselect a single selection.
            if (mTimePicker.getSelectedStartTime() == targetTime
                    || mTimePicker.getSelectedEndTime() == targetTime)
            {
                if (mTimePicker.getSelectedStartTime() != HandyTimePicker.NO_TIME_SELECTED
                        && mTimePicker.getSelectedEndTime() != HandyTimePicker.NO_TIME_SELECTED)
                {
                    final int selectedStartTime = mTimePicker.getSelectedStartTime();
                    resetTimeRange();
                    targetTime = selectedStartTime;
                }
                else
                {
                    resetTimeRange();
                    return;
                }
            }

            // Tapping an earlier time when there is already a selected start time will force the
            // editing to start time.
            if (mTimePicker.getSelectedStartTime() != HandyTimePicker.NO_TIME_SELECTED
                    && targetTime < mTimePicker.getSelectedStartTime())
            {
                mTimeEditType = TimeEditType.START_TIME;
            }

            // Tapping a later time when there is already a selected end time will force the editing
            // to end time.
            if (mTimePicker.getSelectedEndTime() != HandyTimePicker.NO_TIME_SELECTED
                    && targetTime > mTimePicker.getSelectedEndTime())
            {
                mTimeEditType = TimeEditType.END_TIME;
            }

            if (mTimeEditType == TimeEditType.START_TIME)
            {
                if (mTimePicker.selectStartTime(targetTime))
                {
                    editStartTime();
                    final Date date = DateTimeUtils.parseDateString(
                            String.valueOf(targetTime), DateTimeUtils.HOUR_INT_FORMATTER);
                    mStartTime.setText(DateTimeUtils.formatDateTo12HourClock(date));
                }
            }
            if (mTimeEditType == TimeEditType.END_TIME)
            {
                if (mTimePicker.selectEndTime(targetTime))
                {
                    editEndTime();
                    final Date date = DateTimeUtils.parseDateString(
                            String.valueOf(targetTime), DateTimeUtils.HOUR_INT_FORMATTER);
                    mEndTime.setText(DateTimeUtils.formatDateTo12HourClock(date));
                    mResetTimeRangeButton.setVisibility(View.VISIBLE);
                }
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
        mStartTime.setBackgroundResource(R.color.handy_bg);
        mStartTime.setTextColor(mBlack);
        mEndTimeHolder.setBackgroundResource(R.color.handy_bg);
        mEndTime.setTextColor(mBlack);
        mStartTime.setText(R.string.start_time);
        mEndTime.setText(R.string.end_time);
        mTimeEditType = TimeEditType.START_TIME;
        mResetTimeRangeButton.setVisibility(View.GONE);
        // FIXME: Manage the state of this button
        mTimePicker.resetSelection();
    }

    @OnClick(R.id.start_time)
    public void editStartTime()
    {
        mStartTime.setBackgroundResource(R.color.tertiary_gray);
        mStartTime.setTextColor(mWhite);
        mEndTimeHolder.setBackgroundResource(R.color.handy_bg);
        mEndTime.setTextColor(mBlack);
        mTimeEditType = TimeEditType.START_TIME;
    }

    @OnClick(R.id.end_time_holder)
    public void editEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.tertiary_gray);
        mEndTime.setTextColor(mWhite);
        mStartTime.setBackgroundResource(R.color.handy_bg);
        mStartTime.setTextColor(mBlack);
        mTimeEditType = TimeEditType.END_TIME;
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
        mTimePicker.setTimeRange(7, 23);
        mTimePicker.setTimeClickListener(mTimeClickListener);
        if (mAvailabilityTimeline != null
                && mAvailabilityTimeline.getAvailabilityIntervals() != null
                && !mAvailabilityTimeline.getAvailabilityIntervals().isEmpty())
        {
            final AvailabilityInterval interval =
                    mAvailabilityTimeline.getAvailabilityIntervals().get(0);
            mTimePicker.selectTimeRange(interval.getStartTimeInt(), interval.getEndTimeInt());
            // FIXME: Set time in text fields
        }
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
    }

    @Override
    public void onDestroyView()
    {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
