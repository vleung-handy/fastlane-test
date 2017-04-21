package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.viewmodel.TimePickerViewModel;

import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeRangeView extends FrameLayout {

    @BindView(R.id.start_time)
    TextView mStartTime;
    @BindView(R.id.end_time)
    TextView mEndTime;
    @BindView(R.id.end_time_holder)
    ViewGroup mEndTimeHolder;
    @BindView(R.id.reset_time_range)
    View mResetTimeRangeButton;
    @BindColor(R.color.black)
    int mBlackColorValue;
    @BindColor(R.color.white)
    int mWhiteColorValue;

    public TimeRangeView(@NonNull final Context context) {
        super(context);
        init();
    }

    public TimeRangeView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeRangeView(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_time_range, this);
        ButterKnife.bind(this);
    }

    public void setStartTimeClickListener(final View.OnClickListener listener) {
        mStartTime.setOnClickListener(listener);
    }

    public void setEndTimeClickListener(final View.OnClickListener listener) {
        mEndTimeHolder.setOnClickListener(listener);
    }

    public void setRemoveListener(final OnClickListener removeListener) {
        mResetTimeRangeButton.setOnClickListener(removeListener);
    }

    public void editStartTime() {
        mStartTime.setBackgroundResource(R.color.tertiary_gray);
        mStartTime.setTextColor(mWhiteColorValue);
        uneditEndTime();
    }

    public void editEndTime() {
        mEndTimeHolder.setBackgroundResource(R.color.tertiary_gray);
        mEndTime.setTextColor(mWhiteColorValue);
        uneditStartTime();
    }

    public void unedit() {
        uneditStartTime();
        uneditEndTime();
    }

    public void uneditStartTime() {
        mStartTime.setBackgroundResource(R.color.handy_bg);
        mStartTime.setTextColor(mBlackColorValue);
    }

    public void uneditEndTime() {
        mEndTimeHolder.setBackgroundResource(R.color.handy_bg);
        mEndTime.setTextColor(mBlackColorValue);
    }

    public void updateStartTime(final int hour) {
        updateTime(mStartTime, hour, R.string.start_time);
    }

    public void updateEndTime(final int hour) {
        updateTime(mEndTime, hour, R.string.end_time);
    }

    private void updateTime(final TextView timeView, final int hour,
                            @StringRes final int emptyStringResId) {
        if (hour == TimePickerViewModel.TimeRange.NO_HOUR) {
            timeView.setText(emptyStringResId);
        }
        else {
            final Date date = DateTimeUtils.parseDateString(
                    String.valueOf(hour), DateTimeUtils.HOUR_INT_FORMATTER);
            timeView.setText(DateTimeUtils.formatDateTo12HourClock(date));
        }
    }
}
