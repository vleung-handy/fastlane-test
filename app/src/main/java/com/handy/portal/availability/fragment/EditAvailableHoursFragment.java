package com.handy.portal.availability.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.handy.portal.R;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.TimeRangeListView;
import com.handy.portal.availability.viewmodel.TimePickerViewModel;
import com.handy.portal.availability.viewmodel.TimePickerViewModel.SelectionType;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.activity.BaseActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.view.timepicker.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAvailableHoursFragment extends ActionBarFragment {
    @Inject
    AvailabilityManager mAvailabilityManager;

    private static final int TIME_SLOTS_LIMIT = 3;
    private static final int DEFAULT_START_HOUR = 7;
    private static final int DEFAULT_END_HOUR = 23;
    private static final int DEFAULT_TIME_RANGE_DURATION = 3;
    @BindView(R.id.availability_toggle)
    SwitchCompat mAvailabilityToggle;
    @BindView(R.id.time_picker)
    HandyTimePicker mTimePicker;
    @BindView(R.id.time_ranges)
    TimeRangeListView mTimeRanges;
    @BindView(R.id.save)
    Button mSaveButton;
    @BindView(R.id.add_time_range_button)
    Button mAddTimeRangeButton;
    @BindColor(R.color.tertiary_gray)
    int mGrayColorValue;
    @BindColor(R.color.handy_blue)
    int mBlueColorValue;

    private String mFlowContext;
    private Date mDate;
    private Availability.Timeline mOriginalTimeline;
    private BaseActivity.OnBackPressedListener mOnBackPressedListener;
    private CompoundButton.OnCheckedChangeListener mAvailabilityToggleCheckedChangeListener;
    private TimePickerViewModel mTimePickerViewModel;
    private final TimePickerViewModel.Listener mTimePickerViewModelListener;
    private boolean mIsDirty;

    {
        mOnBackPressedListener = new BaseActivity.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                if (mIsDirty) {
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
                final boolean closed = !isChecked;
                if (closed) {
                    while (mTimePickerViewModel.getTimeRangesCount() > 1) {
                        mTimePickerViewModel.removeTimeRange(1);
                    }
                    if (mTimePickerViewModel.getTimeRangesCount() == 1) {
                        mTimePickerViewModel.clearTimeRange(0);
                    }
                }
                mTimePickerViewModel.getPointer().point(0, SelectionType.START_TIME);
                mTimePickerViewModel.setClosed(closed);
                updateButtonsVisibility();
            }
        };
        mTimePickerViewModelListener = new TimePickerViewModel.Listener() {
            @Override
            public void onTimeRangeUpdated(
                    final int index,
                    final int oldStartHour,
                    final int oldEndHour,
                    final int newStartHour,
                    final int newEndHour
            ) {
                mIsDirty = true;
                updateButtonsVisibility();
            }

            @Override
            public void onTimeRangeAdded(final int index, final int startHour, final int endHour) {
                mIsDirty = true;
                updateButtonsVisibility();
            }

            @Override
            public void onTimeRangeRemoved(
                    final int index,
                    final int startHour,
                    final int endHour
            ) {
                mIsDirty = true;
                updateButtonsVisibility();
            }

            @Override
            public void onPointerUpdated(
                    final int index,
                    final SelectionType selectionType
            ) {
                updateButtonsVisibility();
            }

            @Override
            public void onClosedStateChanged(final boolean closed) {
                mIsDirty = true;

                mAvailabilityToggle.setOnCheckedChangeListener(null);
                mAvailabilityToggle.setChecked(!closed);
                mAvailabilityToggle.setOnCheckedChangeListener(
                        mAvailabilityToggleCheckedChangeListener);

                updateButtonsVisibility();
            }
        };
    }

    private void showDiscardChangesDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.discard_changes_confirmation_title)
                .setMessage(R.string.discard_changes_confirmation_message)
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

    @OnClick(R.id.add_time_range_button)
    public void onAddTimeRange() {
        mTimePickerViewModel.setClosed(false);
        mTimePickerViewModel.addTimeRange();
        mTimePickerViewModel.getPointer().point(
                mTimePickerViewModel.getTimeRangesCount() - 1,
                SelectionType.START_TIME
        );
    }

    @OnClick(R.id.save)
    public void onSave() {
        final Availability.Wrapper.Timelines timelinesWrapper = getTimelinesWrapperFromViewModel();
        logSubmit(timelinesWrapper);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mAvailabilityManager.saveAvailability(
                timelinesWrapper,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        logSuccess(timelinesWrapper);
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        ((BaseActivity) getActivity()).clearOnBackPressedListenerStack();
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        logError(timelinesWrapper);
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        String message = error.getMessage();
                        if (TextUtils.isEmpty(message)) {
                            message = getString(R.string.an_error_has_occurred);
                        }
                        showToast(message);
                    }
                });
    }

    private void logSubmit(final Availability.Wrapper.Timelines timelinesWrapper) {
        final Availability.Timeline timeline = timelinesWrapper.get().get(0);
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursSubmitted(mFlowContext, timeline.getDateString(),
                        getIntervalsSum(timeline.getIntervals()),
                        !timeline.hasIntervals())));
    }

    private void logSuccess(final Availability.Wrapper.Timelines timelinesWrapper) {
        final Availability.Timeline timeline = timelinesWrapper.get().get(0);
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursSuccess(mFlowContext, timeline.getDateString(),
                        getIntervalsSum(timeline.getIntervals()),
                        !timeline.hasIntervals())));
    }

    private void logError(final Availability.Wrapper.Timelines timelinesWrapper) {
        final Availability.Timeline timeline = timelinesWrapper.get().get(0);
        bus.post(new LogEvent.AddLogEvent(
                new ProAvailabilityLog.SetHoursError(mFlowContext, timeline.getDateString(),
                        getIntervalsSum(timeline.getIntervals()),
                        !timeline.hasIntervals())));
    }

    private int getIntervalsSum(final List<Availability.Interval> intervals) {
        int sum = 0;
        for (final Availability.Interval interval : intervals) {
            sum += (interval.getEndHour() - interval.getStartHour());
        }
        return sum;
    }

    private Availability.Wrapper.Timelines getTimelinesWrapperFromViewModel() {
        final Availability.Wrapper.Timelines timelinesWrapper = new Availability.Wrapper.Timelines();
        timelinesWrapper.addTimeline(mDate, getIntervalsFromViewModel());
        return timelinesWrapper;
    }

    private ArrayList<Availability.Interval> getIntervalsFromViewModel() {
        final ArrayList<Availability.Interval> intervals = new ArrayList<>();
        if (!mTimePickerViewModel.isClosed()) {
            final List<TimePickerViewModel.TimeRange> timeRanges =
                    mTimePickerViewModel.getTimeRanges();
            Collections.sort(timeRanges);
            for (final TimePickerViewModel.TimeRange timeRange : timeRanges) {
                intervals.add(new Availability.Interval(timeRange.getStartHour(),
                        timeRange.getEndHour()));
            }
        }
        return intervals;
    }

    private void updateButtonsVisibility() {
        mSaveButton.setVisibility(mTimePickerViewModel.validate() ? View.VISIBLE : View.GONE);
        mAddTimeRangeButton.setVisibility(!mTimePickerViewModel.isClosed()
                && mTimePickerViewModel.hasCompleteTimeRanges()
                && mTimePickerViewModel.getTimeRangesCount() < TIME_SLOTS_LIMIT
                && hasSelectableHours() ?
                View.VISIBLE : View.INVISIBLE);
    }

    private boolean hasSelectableHours() {
        final List<Integer> selectableHours = mTimePickerViewModel.getSelectableHours(null);
        return selectableHours != null && !selectableHours.isEmpty();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlowContext = getArguments().getString(BundleKeys.FLOW_CONTEXT);
        mDate = (Date) getArguments().getSerializable(BundleKeys.DATE);
        mOriginalTimeline = mAvailabilityManager.getTimelineForDate(mDate);
        ((BaseActivity) getActivity()).addOnBackPressedListener(mOnBackPressedListener);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_available_hours, container, false);
        ButterKnife.bind(this, view);
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String dateFormatted = DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mDate);
        setActionBar(getString(R.string.hours_for_date_formatted, dateFormatted), true);
        initTimePickerViewModel();
        initAvailabilityToggle();
        initTimePicker();
        mTimeRanges.setViewModel(mTimePickerViewModel);
        mTimePickerViewModel.addListener(mTimePickerViewModelListener);
        updateButtonsVisibility();
    }

    private void initTimePickerViewModel() {
        mTimePickerViewModel = new TimePickerViewModel();
        mTimePickerViewModel.setLimits(DEFAULT_START_HOUR, DEFAULT_END_HOUR,
                DEFAULT_TIME_RANGE_DURATION);
        if (mOriginalTimeline != null && mOriginalTimeline.hasIntervals()) {
            for (final Availability.Interval interval :
                    mOriginalTimeline.getIntervals()) {
                mTimePickerViewModel.addTimeRange(interval.getStartHour(), interval.getEndHour());
            }
            mTimePickerViewModel.getPointer().point(mTimePickerViewModel.getTimeRangesCount() - 1,
                    SelectionType.END_TIME);
        }
        else {
            mTimePickerViewModel.addTimeRange();
            mTimePickerViewModel.getPointer().point(0, SelectionType.START_TIME);
        }
        mTimePickerViewModel.setClosed(mOriginalTimeline != null
                && !mOriginalTimeline.hasIntervals());
    }

    private void initAvailabilityToggle() {
        mAvailabilityToggle.setChecked(!mTimePickerViewModel.isClosed());
        mAvailabilityToggle.setOnCheckedChangeListener(mAvailabilityToggleCheckedChangeListener);
    }

    private void initTimePicker() {
        mTimePicker.setTimeRange(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
        mTimePicker.setViewModel(mTimePickerViewModel);
    }

    @Override
    public void onDestroyView() {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
