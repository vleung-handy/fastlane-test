package com.handy.portal.proavailability.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.view.timepicker.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAvailableHoursFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    private static final int DEFAULT_START_HOUR = 7;
    private static final int DEFAULT_END_HOUR = 23;
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
    int mBlackColorValue;
    @BindColor(R.color.white)
    int mWhiteColorValue;
    @BindColor(R.color.error_red)
    int mRedColorValue;

    private Date mDate;
    private DailyAvailabilityTimeline mAvailabilityTimeline;

    @OnClick(R.id.reset_time_range)
    public void resetTimeRange()
    {
        if (getFirstAvailabilityInterval() != null)
        {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setCancelable(true)
                    .setMessage(R.string.time_slot_removal_prompt)
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            clearSelection();
                            onSave();
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
        else
        {
            clearSelection();
        }
    }

    private void clearSelection()
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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        dataManager.saveProviderAvailability(mProviderManager.getLastProviderId(),
                getAvailabilityTimelinesWrapperFromTimePicker(),
                new FragmentSafeCallback<Void>(this)
                {
                    @Override
                    public void onCallbackSuccess(final Void response)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        callTargetFragmentResult();
                        getActivity().onBackPressed();
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

    private void callTargetFragmentResult()
    {
        if (getTargetFragment() != null)
        {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.DATE, mDate);
            data.putExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE,
                    getDailyAvailabilityTimelineFromTimePicker());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    private AvailabilityTimelinesWrapper getAvailabilityTimelinesWrapperFromTimePicker()
    {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        timelinesWrapper.addTimeline(mDate, getAvailabilityIntervalsFromTimePicker());
        return timelinesWrapper;
    }

    private DailyAvailabilityTimeline getDailyAvailabilityTimelineFromTimePicker()
    {
        return new DailyAvailabilityTimeline(mDate, getAvailabilityIntervalsFromTimePicker());
    }

    private ArrayList<AvailabilityInterval> getAvailabilityIntervalsFromTimePicker()
    {
        final ArrayList<AvailabilityInterval> intervals = new ArrayList<>();
        if (mTimePicker.hasSelectedRange())
        {
            final int selectedStartHour = mTimePicker.getSelectedStartHour();
            final int selectedEndHour = mTimePicker.getSelectedEndHour();
            intervals.add(new AvailabilityInterval(selectedStartHour, selectedEndHour));
        }
        return intervals;
    }

    public void editStartTime()
    {
        mStartTime.setBackgroundResource(R.color.tertiary_gray);
        mStartTime.setTextColor(mWhiteColorValue);
        uneditEndTime();
    }

    private void editEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.tertiary_gray);
        mEndTime.setTextColor(mWhiteColorValue);
        uneditStartTime();
    }

    private void uneditStartTime()
    {
        mStartTime.setBackgroundResource(R.color.handy_bg);
        mStartTime.setTextColor(mBlackColorValue);
    }

    private void uneditEndTime()
    {
        mEndTimeHolder.setBackgroundResource(R.color.handy_bg);
        mEndTime.setTextColor(mBlackColorValue);
    }

    private void updateStartTime(final int hour)
    {
        updateTime(mStartTime, hour, R.string.start_time);
    }

    private void updateEndTime(final int hour)
    {
        updateTime(mEndTime, hour, R.string.end_time);
    }

    private void updateTime(final TextView timeView, final int hour,
                            @StringRes final int emptyStringResId)
    {
        if (hour == HandyTimePicker.NO_HOUR_SELECTED)
        {
            timeView.setText(emptyStringResId);
        }
        else
        {
            final Date date = DateTimeUtils.parseDateString(
                    String.valueOf(hour), DateTimeUtils.HOUR_INT_FORMATTER);
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
        if (isOriginalIntervalSelected() || mTimePicker.hasSelectedSingleTime())
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
            return originalInterval.getStartHour() == mTimePicker.getSelectedStartHour()
                    && originalInterval.getEndHour() == mTimePicker.getSelectedEndHour();
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
        mTimePicker.setTimeRange(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
        mTimePicker.setCallbacks(new HandyTimePicker.Callbacks()
        {
            @Override
            public void onRangeUpdated(final int startHour, final int endHour)
            {
                updateStartTime(startHour);
                updateEndTime(endHour);
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
            if (mTimePicker.selectTimeRange(interval.getStartHour(), interval.getEndHour()))
            {
                updateStartTime(interval.getStartHour());
                updateEndTime(interval.getEndHour());
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
