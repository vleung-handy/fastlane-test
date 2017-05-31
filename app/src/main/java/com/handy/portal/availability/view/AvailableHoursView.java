package com.handy.portal.availability.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AvailableHoursView extends FrameLayout {
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.intervals)
    ViewGroup mIntervals;
    @BindColor(R.color.tertiary_gray)
    int mGrayColor;

    public AvailableHoursView(final Context context) {
        super(context);
        init();
    }

    public AvailableHoursView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvailableHoursView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AvailableHoursView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_available_hours, this);
        ButterKnife.bind(this);
    }

    public void setAvailableHours(@Nullable final List<Availability.Interval> intervals) {
        mIntervals.removeAllViews();
        if (intervals == null || intervals.isEmpty()) {
            mTitle.setText(R.string.no_available_hours);
            final TextView textView = new TextView(getContext());
            textView.setText(intervals == null ? R.string.set_hours : R.string.not_available);
            textView.setTextColor(mGrayColor);
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            mIntervals.addView(textView);
        }
        else {
            mTitle.setText(R.string.available_hours);
            for (Availability.Interval availability : intervals) {
                final TextView intervalTextView = new TextView(getContext());
                final String startTimeFormatted =
                        DateTimeUtils.formatDateTo12HourClock(availability.getStartTime());
                final String endTimeFormatted =
                        DateTimeUtils.formatDateTo12HourClock(availability.getEndTime());
                intervalTextView.setText(startTimeFormatted + " - " + endTimeFormatted);
                intervalTextView.setTextColor(mGrayColor);
                intervalTextView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
                intervalTextView.setGravity(Gravity.END);
                mIntervals.addView(intervalTextView);
            }
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        updateAlpha();
    }

    private void updateAlpha() {
        float alpha = isEnabled() ? 1.0f : 0.3f;
        mTitle.setAlpha(alpha);
        mIntervals.setAlpha(alpha);
    }
}
