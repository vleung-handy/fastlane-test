package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.AvailabilityInterval;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.view.AvailableTimeSlotView.RemoveTimeSlotListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableHoursWithDateView extends FrameLayout
{
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.timelines)
    ViewGroup mTimelines;

    private final Date mDate;
    private final DailyAvailabilityTimeline mAvailability;
    private RemoveTimeSlotListener mRemoveTimeSlotListener;

    public AvailableHoursWithDateView(final Context context,
                                      final Date date,
                                      @Nullable final DailyAvailabilityTimeline availability,
                                      final RemoveTimeSlotListener removeTimeSlotListener)
    {
        super(context);
        mDate = date;
        mAvailability = availability;
        mRemoveTimeSlotListener = removeTimeSlotListener;
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_available_hours_with_date, this);
        ButterKnife.bind(this);

        setBackgroundResource(R.drawable.border_gray_bottom);

        mTitle.setText(DateTimeUtils.formatDayOfWeekMonthDate(mDate));

        if (mAvailability != null)
        {
            final ArrayList<AvailabilityInterval> intervals =
                    mAvailability.getAvailabilityIntervals();
            if (intervals != null && !intervals.isEmpty())
            {
                for (AvailabilityInterval interval : intervals)
                {
                    mTimelines.addView(new AvailableTimeSlotView(getContext(),
                            mAvailability.getDate(), interval, mRemoveTimeSlotListener));
                }
            }
        }
    }
}
