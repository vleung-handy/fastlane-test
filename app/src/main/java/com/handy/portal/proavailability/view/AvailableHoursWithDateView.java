package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.view.AvailableTimeSlotView.RemoveTimeSlotListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableHoursWithDateView extends FrameLayout {
    @BindView(R.id.row)
    View mRow;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.timelines)
    ViewGroup mTimelines;

    private final Date mDate;
    private DailyAvailabilityTimeline mAvailability;
    private RemoveTimeSlotListener mRemoveTimeSlotListener;
    protected final boolean mEnabled;

    public AvailableHoursWithDateView(final Context context,
                                      final Date date,
                                      @Nullable final DailyAvailabilityTimeline availability,
                                      @Nullable final RemoveTimeSlotListener removeTimeSlotListener,
                                      final boolean enabled) {
        super(context);
        mDate = date;
        mAvailability = availability;
        mRemoveTimeSlotListener = removeTimeSlotListener;
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
        updateTimelines(mAvailability);
        setEnabled(mEnabled);
        if (!mEnabled) {
            mTitle.setAlpha(0.3f);
            mTimelines.setAlpha(0.3f);
        }
    }

    public void updateTimelines(final DailyAvailabilityTimeline availability) {
        mAvailability = availability;
        mTimelines.removeAllViews();
        if (mAvailability != null && mAvailability.hasIntervals()) {
            for (AvailabilityInterval interval : mAvailability.getAvailabilityIntervals()) {
                mTimelines.addView(new AvailableTimeSlotView(getContext(),
                        mAvailability.getDate(), interval, mRemoveTimeSlotListener));
            }
        }
    }
}
