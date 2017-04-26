package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.handy.portal.proavailability.viewmodel.TimePickerViewModel;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel.SelectionType;

import java.util.ArrayList;
import java.util.List;


public class TimeRangeListView extends LinearLayout implements TimePickerViewModel.Listener {
    private List<TimeRangeView> mTimeRangeViews;
    private TimePickerViewModel mViewModel;

    public TimeRangeListView(final Context context) {
        super(context);
        init();
    }

    public TimeRangeListView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeRangeListView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mTimeRangeViews = new ArrayList<>();
    }

    public void setViewModel(final TimePickerViewModel viewModel) {
        mViewModel = viewModel;
        mViewModel.addListener(this);
        removeAllViews();
        mTimeRangeViews.clear();
        for (final TimePickerViewModel.TimeRange timeRange : mViewModel.getTimeRanges()) {
            createNewTimeRange(timeRange.getStartHour(), timeRange.getEndHour());
        }
        setAlpha(mViewModel.isClosed() ? 0.3f : 1.0f);
        if (mViewModel.getPointer().validate()) {
            final TimeRangeView selectedTimeRangeView =
                    mTimeRangeViews.get(mViewModel.getPointer().getIndex());
            final SelectionType selectionType = mViewModel.getPointer().getSelectionType();
            if (selectionType == SelectionType.START_TIME) {
                selectedTimeRangeView.editStartTime();
            }
            else if (selectionType == SelectionType.END_TIME) {
                selectedTimeRangeView.editEndTime();
            }
        }
    }

    private void createNewTimeRange(final int startHour, final int endHour) {
        final TimeRangeView timeRangeView = new TimeRangeView(getContext());
        timeRangeView.setStartTimeClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int index = mTimeRangeViews.indexOf(timeRangeView);
                mViewModel.getPointer().point(index, SelectionType.START_TIME);
            }
        });
        timeRangeView.setEndTimeClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int index = mTimeRangeViews.indexOf(timeRangeView);
                mViewModel.getPointer().point(index, SelectionType.END_TIME);
            }
        });
        timeRangeView.setRemoveListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int index = mTimeRangeViews.indexOf(timeRangeView);
                mViewModel.clearTimeRange(index);
                mViewModel.getPointer().setSelectionType(SelectionType.START_TIME);
                if (mTimeRangeViews.size() > 1) {
                    mViewModel.removeTimeRange(index);
                }
            }
        });
        timeRangeView.updateStartTime(startHour);
        timeRangeView.updateEndTime(endHour);
        mTimeRangeViews.add(timeRangeView);
        addView(timeRangeView);
    }

    @Override
    public void onTimeRangeUpdated(
            final int index,
            final int oldStartHour,
            final int oldEndHour,
            final int newStartHour,
            final int newEndHour
    ) {
        final TimeRangeView timeRangeView = mTimeRangeViews.get(index);
        timeRangeView.updateStartTime(newStartHour);
        timeRangeView.updateEndTime(newEndHour);
    }

    @Override
    public void onTimeRangeAdded(final int index, final int startHour, final int endHour) {
        createNewTimeRange(startHour, endHour);
    }

    @Override
    public void onTimeRangeRemoved(final int index, final int startHour, final int endHour) {
        removeViewAt(index);
        mTimeRangeViews.remove(index);
    }

    @Override
    public void onPointerUpdated(final int index, final SelectionType selectionType) {
        for (final TimeRangeView timeRangeView : mTimeRangeViews) {
            timeRangeView.unedit();
        }
        if (index != TimePickerViewModel.Pointer.NO_INDEX) {
            final TimeRangeView selectedTimeRangeView = mTimeRangeViews.get(index);
            if (selectionType == SelectionType.START_TIME) {
                selectedTimeRangeView.editStartTime();
            }
            else if (selectionType == SelectionType.END_TIME) {
                selectedTimeRangeView.editEndTime();
            }
        }
    }

    @Override
    public void onClosedStateChanged(final boolean closed) {
        setAlpha(closed ? 0.3f : 1.0f);
    }
}
