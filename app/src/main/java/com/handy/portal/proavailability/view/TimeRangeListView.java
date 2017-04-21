package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.handy.portal.library.ui.view.timepicker.HandyTimePicker;

import java.util.ArrayList;
import java.util.List;


public class TimeRangeListView extends LinearLayout {
    private List<TimeRangeView> mTimeRangeViews;
    private Callbacks mCallbacks;
    private TimeRangeView mCurrentTimeRangeView;

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

    public void setCallbacks(final Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void selectLastTimeRange() {
        mCurrentTimeRangeView = mTimeRangeViews.get(mTimeRangeViews.size() - 1);
    }

    public void createNewTimeRange() {
        final TimeRangeView timeRangeView = new TimeRangeView(getContext());
        timeRangeView.setStartTimeClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                timeRangeView.editStartTime();
                mCurrentTimeRangeView = timeRangeView;
                if (mCallbacks != null) {
                    mCallbacks.onStartTimeClicked(mTimeRangeViews.indexOf(timeRangeView));
                }
            }
        });
        timeRangeView.setEndTimeClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                timeRangeView.editEndTime();
                mCurrentTimeRangeView = timeRangeView;
                if (mCallbacks != null) {
                    mCallbacks.onEndTimeClicked(mTimeRangeViews.indexOf(timeRangeView));
                }
            }
        });
        timeRangeView.setRemoveListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int index = mTimeRangeViews.indexOf(timeRangeView);

                timeRangeView.updateStartTime(HandyTimePicker.NO_HOUR_SELECTED);
                timeRangeView.updateEndTime(HandyTimePicker.NO_HOUR_SELECTED);
                timeRangeView.editStartTime();
                if (mCallbacks != null) {
                    mCallbacks.onClear(index);
                }

                if (mTimeRangeViews.size() > 1) {
                    mTimeRangeViews.remove(timeRangeView);
                    removeView(timeRangeView);
                    if (mCallbacks != null) {
                        mCallbacks.onRemove(index);
                    }
                }
            }
        });
        mTimeRangeViews.add(timeRangeView);
        addView(timeRangeView);
    }

    public void clearCurrentTimeRange() {
        mCurrentTimeRangeView.uneditStartTime();
        mCurrentTimeRangeView.uneditEndTime();
    }

    public void updateCurrentTimeRange(final int startHour, final int endHour) {
        mCurrentTimeRangeView.updateStartTime(startHour);
        mCurrentTimeRangeView.updateEndTime(endHour);
    }

    public void editCurrentStartTime() {
        mCurrentTimeRangeView.editStartTime();
    }

    public void editCurrentEndTime() {
        mCurrentTimeRangeView.editEndTime();
    }

    public interface Callbacks {
        void onStartTimeClicked(int index);

        void onEndTimeClicked(int index);

        void onClear(int index);

        void onRemove(int index);
    }
}
