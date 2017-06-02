package com.handy.portal.availability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;

import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableHoursWithDateView extends FrameLayout {
    @BindView(R.id.row)
    View mRow;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.intervals)
    ViewGroup mIntervals;
    @BindColor(R.color.tertiary_gray)
    int mGrayColor;
    @BindColor(R.color.handy_blue)
    int mBlueColor;

    private final Date mDate;
    private Availability.Timeline mTimeline;
    protected final boolean mEnabled;

    public AvailableHoursWithDateView(
            final Context context,
            final Date date,
            final boolean enabled
    ) {
        super(context);
        mDate = date;
        mEnabled = enabled;
        init();
    }

    public Date getDate() {
        return mDate;
    }

    public void setRowPadding(final int xPadding, final int yPadding) {
        mRow.setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    public void setTitleSize(final float size) {
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    private void init() {
        inflate(getContext(), R.layout.element_available_hours_with_date, this);
        ButterKnife.bind(this);
        mTitle.setText(DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(mDate));
        updateIntervals(mTimeline);
        setEnabled(mEnabled);
        if (!mEnabled) {
            mTitle.setAlpha(0.3f);
            mIntervals.setAlpha(0.3f);
        }
    }

    public void updateIntervals(@Nullable final Availability.Timeline timeline) {
        mTimeline = timeline;
        mIntervals.removeAllViews();
        if (mTimeline != null) {
            if (mTimeline.hasIntervals()) {
                for (final Availability.Interval interval :
                        mTimeline.getIntervals()) {
                    final TextView textView = createTextView();
                    final String startTimeFormatted =
                            DateTimeUtils.formatDateTo12HourClock(interval.getStartTime());
                    final String endTimeFormatted =
                            DateTimeUtils.formatDateTo12HourClock(interval.getEndTime());
                    textView.setText(startTimeFormatted + " - " + endTimeFormatted);
                    mIntervals.addView(textView);
                }
            }
            else {
                final TextView textView = createTextView();
                textView.setText(R.string.not_available);
                mIntervals.addView(textView);
            }
        }
        else if (mEnabled) {
            final TextView textView = createTextView();
            textView.setText(R.string.set_hours);
            textView.setTextColor(mBlueColor);
            mIntervals.addView(textView);
        }
    }

    protected TextView createTextView() {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(mGrayColor);
        textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
        textView.setGravity(Gravity.END);
        return textView;
    }
}
