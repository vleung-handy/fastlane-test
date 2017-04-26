package com.handy.portal.proavailability.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.activity.BaseActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.ui.view.timepicker.HandyTimePicker;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProAvailabilityLog;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.AvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.view.TimeRangeListView;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel.SelectionType;

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

    private static final int TIME_SLOTS_LIMIT = 3;
    private static final int DEFAULT_START_HOUR = 7;
    private static final int DEFAULT_END_HOUR = 23;
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
    private DailyAvailabilityTimeline mAvailabilityTimeline;
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
                mTimePickerViewModel.setClosed(!isChecked);
                updateSaveButtonVisibility();
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
                updateSaveButtonVisibility();
            }

            @Override
            public void onTimeRangeAdded(final int index, final int startHour, final int endHour) {
                mIsDirty = true;
                updateSaveButtonVisibility();
            }

            @Override
            public void onTimeRangeRemoved(
                    final int index,
                    final int startHour,
                    final int endHour
            ) {
                mIsDirty = true;
                updateSaveButtonVisibility();
            }

            @Override
            public void onPointerUpdated(
                    final int index,
                    final SelectionType selectionType
            ) {
                // do nothing
            }

            @Override
            public void onClosedStateChanged(final boolean closed) {
                mIsDirty = true;

                mAvailabilityToggle.setOnCheckedChangeListener(null);
                mAvailabilityToggle.setChecked(!closed);
                mAvailabilityToggle.setOnCheckedChangeListener(
                        mAvailabilityToggleCheckedChangeListener);

                mAddTimeRangeButton.setAlpha(closed ? 0.3f : 1.0f);

                updateSaveButtonVisibility();
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

    @OnClick(R.id.add_time_range_button)
    public void onAddTimeRange() {
        if (mTimePickerViewModel.isClosed()) {
            mTimePickerViewModel.setClosed(false);
        }
        else if (mTimePickerViewModel.validate()) {
            if (mTimePickerViewModel.getTimeRangesCount() < TIME_SLOTS_LIMIT) {
                mTimePickerViewModel.addTimeRange();
                mTimePickerViewModel.getPointer().point(
                        mTimePickerViewModel.getTimeRangesCount() - 1,
                        SelectionType.START_TIME
                );
            }
            else {
                showToast(getString(R.string.error_time_slots_limit_exceeded_formatted,
                        TIME_SLOTS_LIMIT));
            }
        }
        else {
            showToast(R.string.error_incomplete_time_ranges);
        }
    }

    @OnClick(R.id.save)
    public void onSave() {
        final AvailabilityTimelinesWrapper availabilityTimelinesWrapper =
                getAvailabilityTimelinesWrapperFromViewModel();
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
                    getDailyAvailabilityTimelineFromViewModel());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    }

    private AvailabilityTimelinesWrapper getAvailabilityTimelinesWrapperFromViewModel() {
        final AvailabilityTimelinesWrapper timelinesWrapper = new AvailabilityTimelinesWrapper();
        timelinesWrapper.addTimeline(mDate, getAvailabilityIntervalsFromViewModel());
        return timelinesWrapper;
    }

    private DailyAvailabilityTimeline getDailyAvailabilityTimelineFromViewModel() {
        return new DailyAvailabilityTimeline(mDate, getAvailabilityIntervalsFromViewModel());
    }

    private ArrayList<AvailabilityInterval> getAvailabilityIntervalsFromViewModel() {
        final ArrayList<AvailabilityInterval> intervals = new ArrayList<>();
        for (final TimePickerViewModel.TimeRange timeRange : mTimePickerViewModel.getTimeRanges()) {
            intervals.add(new AvailabilityInterval(timeRange.getStartHour(),
                    timeRange.getEndHour()));
        }
        return intervals;
    }

    private void updateSaveButtonVisibility() {
        mSaveButton.setVisibility(mTimePickerViewModel.validate() ? View.VISIBLE : View.GONE);
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
        mTimePicker.setTimeRange(DEFAULT_START_HOUR, DEFAULT_END_HOUR);
        mTimePicker.setViewModel(mTimePickerViewModel);
        mTimeRanges.setViewModel(mTimePickerViewModel);
        mTimePickerViewModel.addListener(mTimePickerViewModelListener);
    }

    private void initTimePickerViewModel() {
        mTimePickerViewModel = new TimePickerViewModel();
        mTimePickerViewModel.setClosed(mAvailabilityTimeline != null
                && !mAvailabilityTimeline.hasIntervals());
        if (mAvailabilityTimeline != null && mAvailabilityTimeline.hasIntervals()) {
            for (final AvailabilityInterval interval :
                    mAvailabilityTimeline.getAvailabilityIntervals()) {
                mTimePickerViewModel.addTimeRange(interval.getStartHour(), interval.getEndHour());
            }
            mTimePickerViewModel.getPointer().point(mTimePickerViewModel.getTimeRangesCount() - 1,
                    SelectionType.END_TIME);
        }
        else {
            mTimePickerViewModel.addTimeRange();
        }
    }

    private void initAvailabilityToggle() {
        mAvailabilityToggle.setChecked(!mTimePickerViewModel.isClosed());
        mAvailabilityToggle.setOnCheckedChangeListener(mAvailabilityToggleCheckedChangeListener);
    }

    @Override
    public void onDestroyView() {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }
}
