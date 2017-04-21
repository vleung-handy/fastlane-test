package com.handy.portal.proavailability.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.activity.BaseActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.view.timepicker.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.view.TimeRangeListView;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAvailableHoursFragment extends ActionBarFragment {
    @Inject
    ProviderManager mProviderManager;

    private static final int DEFAULT_START_HOUR = 7;
    private static final int DEFAULT_END_HOUR = 23;
    @BindView(R.id.availability_toggle)
    SwitchCompat mAvailabilityToggle;
    @BindView(R.id.time_picker)
    HandyTimePicker mTimePicker;
    @BindView(R.id.time_ranges)
    TimeRangeListView mTimeRanges;
    //    @BindView(R.id.start_time)
//    TextView mStartTime;
//    @BindView(R.id.end_time)
//    TextView mEndTime;
//    @BindView(R.id.end_time_holder)
//    ViewGroup mEndTimeHolder;
//    @BindView(R.id.reset_time_range)
//    View mResetTimeRangeButton;
    @BindView(R.id.save)
    Button mSaveButton;
    //    @BindColor(R.color.black)
//    int mBlackColorValue;
//    @BindColor(R.color.white)
//    int mWhiteColorValue;
    @BindColor(R.color.tertiary_gray)
    int mGrayColorValue;
    @BindColor(R.color.handy_blue)
    int mBlueColorValue;

    private String mFlowContext;
    private Date mDate;
    private DailyAvailabilityTimeline mAvailabilityTimeline;
    private BaseActivity.OnBackPressedListener mOnBackPressedListener;
    private CompoundButton.OnCheckedChangeListener mAvailabilityToggleCheckedChangeListener;
    private boolean mIsFrozen;
    private TimeRangeListView.Callbacks mTimeRangesCallbacks;
    private int mCurrentTimeRangeIndex;

    {
        mOnBackPressedListener = new BaseActivity.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                if (!isOriginalState()) {
                    showDiscardChangesDialog();
                    ((BaseActivity) getActivity()).addOnBackPressedListener(mOnBackPressedListener);
                }
                else {
                    getActivity().onBackPressed();
                }
            }
        };
        mAvailabilityToggleCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    final CompoundButton buttonView,
                    final boolean isChecked
            ) {
                if (isChecked) {
                    unfreezeTimePicker();
                    initTimeRanges();
                }
                else {
                    clearSelection();
                    freezeTimePicker();
                }
                updateSaveButtonVisibility();
            }
        };
        mTimeRangesCallbacks = new TimeRangeListView.Callbacks() {
            @Override
            public void onStartTimeClicked(final int index) {
                mCurrentTimeRangeIndex = index;
                if (mIsFrozen) {
                    setAvailabilityToggleOnWithoutCallback();
                }
                mTimePicker.setSelectionType(HandyTimePicker.SelectionType.START_TIME);
            }

            @Override
            public void onEndTimeClicked(final int index) {
                mCurrentTimeRangeIndex = index;
                if (mIsFrozen) {
                    setAvailabilityToggleOnWithoutCallback();
                }
                mTimePicker.setSelectionType(HandyTimePicker.SelectionType.END_TIME);
            }

            @Override
            public void onClear(final int index) {
                mTimePicker.clearSelection(index);
            }

            @Override
            public void onRemove(final int index) {
                mTimePicker.removeRangeAt(index);
            }
        };
    }

    private void showDiscardChangesDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.send_available_hours_discard_confirmation_title)
                .setMessage(R.string.send_available_hours_discard_confirmation_message)
                .setPositiveButton(R.string.dont_save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
                                getActivity().onBackPressed();
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(mBlueColorValue);
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(mGrayColorValue);
            }
        });
        alertDialog.show();
    }

    private void clearSelection() {
        mTimeRanges.clearCurrentTimeRange();
        mTimePicker.setSelectionType(HandyTimePicker.SelectionType.START_TIME);
        mTimePicker.clearCurrentSelection();
    }

    private void setAvailabilityToggleOnWithoutCallback() {
        mAvailabilityToggle.setOnCheckedChangeListener(null);
        mAvailabilityToggle.setChecked(true);
        mAvailabilityToggle.setOnCheckedChangeListener(mAvailabilityToggleCheckedChangeListener);
        unfreezeTimePicker();
        updateSaveButtonVisibility();
    }

    @OnClick(R.id.save)
    public void onSave() {
        final AvailabilityTimelinesWrapper availabilityTimelinesWrapper =
                getAvailabilityTimelinesWrapperFromTimePicker();
        logSubmit(availabilityTimelinesWrapper);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
//        dataManager.saveProviderAvailability(mProviderManager.getLastProviderId(),
//                availabilityTimelinesWrapper,
//                new FragmentSafeCallback<Void>(this) {
//                    @Override
//                    public void onCallbackSuccess(final Void response) {
//                        logSuccess(availabilityTimelinesWrapper);
//                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
//                        callTargetFragmentResult();
//                        ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
//                        getActivity().onBackPressed();
//                    }
//
//                    @Override
//                    public void onCallbackError(final DataManager.DataManagerError error) {
//                        logError(availabilityTimelinesWrapper);
//                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
//                        String message = error.getMessage();
//                        if (TextUtils.isEmpty(message)) {
//                            message = getString(R.string.an_error_has_occurred);
//                        }
//                        showToast(message);
//                    }
//                });
    }

    private void logSubmit(final AvailabilityTimelinesWrapper availabilityTimelinesWrapper) {
        final DailyAvailabilityTimeline timeline =
                availabilityTimelinesWrapper.getTimelines().get(0);
        final AvailabilityInterval interval = timeline.hasIntervals() ?
                timeline.getAvailabilityIntervals().get(0) : null;
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursSubmitted(mFlowContext, timeline.getDateString(),
                        interval != null ? interval.getEndHour() - interval.getStartHour() : 0,
                        !timeline.hasIntervals())));
    }

    private void logSuccess(final AvailabilityTimelinesWrapper availabilityTimelinesWrapper) {
        final DailyAvailabilityTimeline timeline =
                availabilityTimelinesWrapper.getTimelines().get(0);
        final AvailabilityInterval interval = timeline.hasIntervals() ?
                timeline.getAvailabilityIntervals().get(0) : null;
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursSuccess(mFlowContext, timeline.getDateString(),
                        interval != null ? interval.getEndHour() - interval.getStartHour() : 0,
                        !timeline.hasIntervals())));
    }

    private void logError(final AvailabilityTimelinesWrapper availabilityTimelinesWrapper) {
        final DailyAvailabilityTimeline timeline =
                availabilityTimelinesWrapper.getTimelines().get(0);
        final AvailabilityInterval interval = timeline.hasIntervals() ?
                timeline.getAvailabilityIntervals().get(0) : null;
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursError(mFlowContext, timeline.getDateString(),
                        interval != null ? interval.getEndHour() - interval.getStartHour() : 0,
                        !timeline.hasIntervals())));
    }

    private void callTargetFragmentResult() {
        if (getTargetFragment() != null) {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.DAILY_AVAILABILITY_TIMELINE,
                    getDailyAvailabilityTimelineFromTimePicker());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    private AvailabilityTimelinesWrapper getAvailabilityTimelinesWrapperFromTimePicker() {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        timelinesWrapper.addTimeline(mDate, getAvailabilityIntervalsFromTimePicker());
        return timelinesWrapper;
    }

    private DailyAvailabilityTimeline getDailyAvailabilityTimelineFromTimePicker() {
        return new DailyAvailabilityTimeline(mDate, getAvailabilityIntervalsFromTimePicker());
    }

    private ArrayList<AvailabilityInterval> getAvailabilityIntervalsFromTimePicker() {
        final ArrayList<AvailabilityInterval> intervals = new ArrayList<>();
        if (mTimePicker.hasSelectedRange()) {
            final int selectedStartHour = mTimePicker.getSelectedStartHour();
            final int selectedEndHour = mTimePicker.getSelectedEndHour();
            intervals.add(new AvailabilityInterval(selectedStartHour, selectedEndHour));
        }
        return intervals;
    }

    private void updateSaveButtonVisibility() {
        mSaveButton.setVisibility(canSave() ? View.VISIBLE : View.GONE);
    }

    private boolean canSave() {
        if (mAvailabilityToggle.isChecked()) {
            return mTimePicker.hasSelectedRange() && !isOriginalIntervalSelected();
        }
        else {
            return isOriginallyAvailable();
        }
    }

    private boolean isOriginallyAvailable() {
        return mAvailabilityTimeline == null || mAvailabilityTimeline.hasIntervals();
    }

    private boolean isOriginalState() {
        if (isOriginallyAvailable()) {
            return mAvailabilityTimeline != null ? isOriginalIntervalSelected()
                    : (mAvailabilityToggle.isChecked() && !mTimePicker.hasSelectedRange());
        }
        else {
            return !mAvailabilityToggle.isChecked();
        }
    }

    private boolean isOriginalIntervalSelected() {
        final AvailabilityInterval originalInterval = getFirstAvailabilityInterval();
        return originalInterval != null
                && originalInterval.getStartHour() == mTimePicker.getSelectedStartHour()
                && originalInterval.getEndHour() == mTimePicker.getSelectedEndHour();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlowContext = getArguments().getString(BundleKeys.FLOW_CONTEXT);
        mDate = (Date) getArguments().getSerializable(BundleKeys.DATE);
        mAvailabilityTimeline = (DailyAvailabilityTimeline) getArguments()
                .getSerializable(BundleKeys.DAILY_AVAILABILITY_TIMELINE);
        ((BaseActivity) getActivity()).addOnBackPressedListener(mOnBackPressedListener);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_available_hours, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String dateFormatted = DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mDate);
        setActionBar(getString(R.string.hours_for_date_formatted, dateFormatted), true);
        initTimePicker();
        initTimeRanges();
        initAvailabilityToggle();
        mTimeRanges.setCallbacks(mTimeRangesCallbacks);
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
    }

    private void initAvailabilityToggle() {
        mAvailabilityToggle.setChecked(isOriginallyAvailable());
        mAvailabilityToggle.setOnCheckedChangeListener(mAvailabilityToggleCheckedChangeListener);
        if (!isOriginallyAvailable()) {
            freezeTimePicker();
        }
        else {
            unfreezeTimePicker();
        }
        updateSaveButtonVisibility();
    }

    private void unfreezeTimePicker() {
        mTimeRanges.setAlpha(1.0f);
        mTimePicker.setAlpha(1.0f);
        mIsFrozen = false;
    }

    private void freezeTimePicker() {
        mTimeRanges.setAlpha(0.3f);
        mTimePicker.setAlpha(0.3f);
        mIsFrozen = true;
    }

    private void initTimePicker() {
        mTimePicker.setTimeRange(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
        mTimePicker.setCallbacks(new HandyTimePicker.Callbacks() {
            @Override
            public void onRangeUpdated(final int index, final HandyTimePicker.Range range) {
                if (mIsFrozen) {
                    setAvailabilityToggleOnWithoutCallback();
                }
                mTimeRanges.updateCurrentTimeRange(range.getStartHour(), range.getEndHour());
            }

            @Override
            public void onSelectionTypeChanged(
                    final int index,
                    final HandyTimePicker.SelectionType selectionType
            ) {
                if (selectionType == HandyTimePicker.SelectionType.START_TIME) {
                    mTimeRanges.editCurrentStartTime();
                }
                else if (selectionType == HandyTimePicker.SelectionType.END_TIME) {
                    mTimeRanges.editCurrentEndTime();
                }
            }
        });
    }

    private void initTimeRanges() {
        if (mAvailabilityTimeline != null && mAvailabilityTimeline.hasIntervals()) {
            for (final AvailabilityInterval interval :
                    mAvailabilityTimeline.getAvailabilityIntervals()) {
                mTimeRanges.createNewTimeRange();
                mTimeRanges.selectLastTimeRange();
                mTimeRanges.updateCurrentTimeRange(interval.getStartHour(), interval.getEndHour());
                mTimePicker.createNewRange(interval.getStartHour(), interval.getEndHour());
            }
            mTimePicker.setSelectionType(HandyTimePicker.SelectionType.END_TIME);
            mTimePicker.setCurrentRangeIndex(mCurrentTimeRangeIndex);
        }
        else {
            mTimeRanges.createNewTimeRange();
            mTimeRanges.selectLastTimeRange();
            mTimePicker.setSelectionType(HandyTimePicker.SelectionType.START_TIME);
        }
    }

    @Nullable
    private AvailabilityInterval getFirstAvailabilityInterval() {
        if (mAvailabilityTimeline != null && mAvailabilityTimeline.hasIntervals()) {
            return mAvailabilityTimeline.getAvailabilityIntervals().get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
