package com.handy.portal.library.ui.view.timepicker;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handy.portal.proavailability.viewmodel.TimePickerViewModel;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel.SelectionType;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel.TimeRange;

import java.util.List;


public class HandyTimePicker extends LinearLayout
        implements HandyTimePickerCell.TimeClickListener, TimePickerViewModel.Listener {

    private static final int DEFAULT_CELLS_PER_ROW_COUNT = 4;
    private int mStartHour;
    private int mEndHour;
    private TimePickerViewModel mViewModel;

    public HandyTimePicker(final Context context) {
        super(context);
        init();
    }

    public HandyTimePicker(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandyTimePicker(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HandyTimePicker(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setViewModel(final TimePickerViewModel viewModel) {
        mViewModel = viewModel;
        mViewModel.addListener(this);
        clearSelection(mStartHour, mEndHour);
        for (final TimeRange timeRange : mViewModel.getTimeRanges()) {
            selectTimeRange(TimeRange.NO_HOUR, TimeRange.NO_HOUR, timeRange.getStartHour(),
                    timeRange.getEndHour());
        }
        setAlpha(mViewModel.isClosed() ? 0.3f : 1.0f);
    }

    private boolean covers(final int hour) {
        return hour >= mStartHour && hour <= mEndHour;
    }

    public void setTimeRange(final int startHour, final int endHour) {
        if (startHour < TimeRange.MINIMUM_HOUR
                || endHour > TimeRange.MAXIMUM_HOUR
                || startHour >= endHour) {
            return;
        }
        mStartHour = startHour;
        mEndHour = endHour;
        for (int rowStartHour = startHour; rowStartHour <= endHour;
             rowStartHour += DEFAULT_CELLS_PER_ROW_COUNT) {
            int rowEndHour = rowStartHour + DEFAULT_CELLS_PER_ROW_COUNT - 1;
            if (rowEndHour > endHour) {
                rowEndHour = endHour;
            }
            addView(new HandyTimePickerRow(getContext(), rowStartHour, rowEndHour, this));
        }
    }

    @Override
    public void onHourClicked(final int hour) {
        int targetHour = hour;
        final TimePickerViewModel.Pointer pointer = mViewModel.getPointer();

        // Default selection to start hour.
        if (!pointer.validate()) {
            pointer.point(0, SelectionType.START_TIME);
        }

        // Select an existing range if necessary.
        final List<TimeRange> timeRanges = mViewModel.getTimeRanges();
        for (int i = 0; i < timeRanges.size(); i++) {
            final TimeRange timeRange = timeRanges.get(i);
            if (timeRange != pointer.getTimeRange() && timeRange.covers(targetHour, true)) {
                pointer.point(i, targetHour == timeRange.getStartHour() ?
                        SelectionType.START_TIME : SelectionType.END_TIME);
                return;
            }
        }

        // Default selection to end hour if a start hour has been selected but end hour hasn't
        // been selected.
        final TimeRange pointerTimeRange = pointer.getTimeRange();
        if (pointer.getSelectionType() == SelectionType.START_TIME
                && pointerTimeRange.hasStartHour()
                && !pointerTimeRange.hasEndHour()
                && targetHour != pointerTimeRange.getStartHour()) {
            pointer.setSelectionType(SelectionType.END_TIME);
        }

        // Tapping selected times on the picker will cancel the range and leave the start hour,
        // or deselect a single selection.
        if (pointerTimeRange.getStartHour() == targetHour
                || pointerTimeRange.getEndHour() == targetHour) {
            if (pointerTimeRange.hasRange()) // a range is selected
            {
                // Reset selection then reselect the original selected start hour.
                final int selectedStartHour = pointerTimeRange.getStartHour();
                pointerTimeRange.clear();
                pointer.setSelectionType(SelectionType.START_TIME);
                targetHour = selectedStartHour;
            }
            else // a single hour is currently selected
            {
                pointerTimeRange.clear();
                return;
            }
        }

        // Tapping an earlier hour when there is already a selected start hour will force the
        // selection to start hour.
        if (pointerTimeRange.hasStartHour()
                && targetHour < pointerTimeRange.getStartHour()
                && pointerTimeRange.validateStartHour(targetHour)) {
            pointer.setSelectionType(SelectionType.START_TIME);
        }

        // Tapping a later hour when there is already a selected end hour will force the selection
        // to end hour.
        if (pointerTimeRange.hasEndHour()
                && targetHour > pointerTimeRange.getEndHour()
                && pointerTimeRange.validateEndHour(targetHour)) {
            pointer.setSelectionType(SelectionType.END_TIME);
        }

        // Select start hour if applicable.
        if (pointer.getSelectionType() == SelectionType.START_TIME) {
            pointerTimeRange.setStartHour(targetHour);
        }

        // Select end hour if applicable.
        if (pointer.getSelectionType() == SelectionType.END_TIME) {
            pointerTimeRange.setEndHour(targetHour);
        }
    }

    public void selectTimeRange(
            final int oldStartHour, final int oldEndHour,
            final int newStartHour, final int newEndHour
    ) {
        clearSelection(oldStartHour, oldEndHour);
        if (newStartHour == TimeRange.NO_HOUR || newEndHour == TimeRange.NO_HOUR) {
            selectCellForHour(newStartHour);
            selectCellForHour(newEndHour);
        }
        else {
            for (int i = 0; i < getChildCount(); i++) {
                final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
                for (int j = 0; j < timePickerRow.mTimePickerCellViews.getChildCount(); j++) {
                    final HandyTimePickerCell timePickerCell =
                            (HandyTimePickerCell) timePickerRow.mTimePickerCellViews.getChildAt(j);
                    final int hour = timePickerCell.getHour();
                    if (hour >= newStartHour && hour <= newEndHour) {
                        if (hour == newStartHour || hour == newEndHour) {
                            timePickerCell.select();
                        }
                        else {
                            timePickerCell.highlight();
                        }
                        if (hour > newStartHour && j > 0) {
                            timePickerRow.setGapVisibility(j - 1, true);
                        }
                    }
                }
            }
        }
    }

    public void clearSelection(final int startHour, final int endHour) {
        if (startHour == TimeRange.NO_HOUR || endHour == TimeRange.NO_HOUR) {
            resetCellForHour(startHour);
            resetCellForHour(endHour);
        }
        else {
            for (int i = 0; i < getChildCount(); i++) {
                final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
                for (int j = 0; j < timePickerRow.mTimePickerCellViews.getChildCount(); j++) {
                    final HandyTimePickerCell timePickerCell =
                            (HandyTimePickerCell) timePickerRow.mTimePickerCellViews.getChildAt(j);
                    final int hour = timePickerCell.getHour();
                    if (hour >= startHour && hour <= endHour) {
                        timePickerCell.reset();
                        if (j > 0) {
                            timePickerRow.setGapVisibility(j - 1, false);
                        }
                    }
                }
            }
        }
    }

    private void resetCellForHour(final int hour) {
        if (hour == TimeRange.NO_HOUR) { return; }
        final HandyTimePickerCell cell = getCellForHour(hour);
        if (cell != null) {
            cell.reset();
        }
    }

    private void selectCellForHour(final int hour) {
        if (hour == TimeRange.NO_HOUR) { return; }
        final HandyTimePickerCell cell = getCellForHour(hour);
        if (cell != null) {
            cell.select();
        }
    }

    @Nullable
    private HandyTimePickerCell getCellForHour(final int hour) {
        if (covers(hour)) {
            for (int i = 0; i < getChildCount(); i++) {
                final HandyTimePickerRow timePickerRow = (HandyTimePickerRow) getChildAt(i);
                final HandyTimePickerCell timePickerCell = timePickerRow.getCellForHour(hour);
                if (timePickerCell != null) {
                    return timePickerCell;
                }
            }
        }
        return null;
    }

    @Override
    public void onTimeRangeUpdated(
            final int index, final int oldStartHour, final int oldEndHour,
            final int newStartHour, final int newEndHour
    ) {
        selectTimeRange(oldStartHour, oldEndHour, newStartHour, newEndHour);
    }

    @Override
    public void onTimeRangeAdded(final int index, final int startHour, final int endHour) {
        selectTimeRange(TimeRange.NO_HOUR, TimeRange.NO_HOUR, startHour, endHour);
    }

    @Override
    public void onTimeRangeRemoved(final int index, final int startHour, final int endHour) {
        clearSelection(startHour, endHour);
    }

    @Override
    public void onPointerUpdated(final int index, final SelectionType selectionType) {
        // do nothing
    }

    @Override
    public void onClosedStateChanged(final boolean closed) {
        setAlpha(closed ? 0.3f : 1.0f);
    }
}
