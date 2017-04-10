package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;
import com.handy.portal.proavailability.view.AvailableTimeSlotView.RemoveTimeSlotListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursView extends LinearLayout {
    private final WeeklyAvailabilityTimelinesWrapper mWeeklyAvailability;
    private final DateClickListener mDateClickListener;
    private RemoveTimeSlotListener mRemoveTimeSlotListener;

    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;

    public WeeklyAvailableHoursView(
            final Context context,
            final WeeklyAvailabilityTimelinesWrapper weeklyAvailability,
            final DateClickListener dateClickListener,
            final RemoveTimeSlotListener removeTimeSlotListener) {
        super(context);
        mWeeklyAvailability = weeklyAvailability;
        mDateClickListener = dateClickListener;
        mRemoveTimeSlotListener = removeTimeSlotListener;
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        final Calendar calendar = Calendar.getInstance(Locale.US);
        final Date startDate = mWeeklyAvailability.getStartDate();
        final Date endDate = mWeeklyAvailability.getEndDate();
        calendar.setTime(startDate);
        while (DateTimeUtils.daysBetween(calendar.getTime(), endDate) >= 0) {
            final Date date = calendar.getTime();
            final DailyAvailabilityTimeline availability =
                    mWeeklyAvailability.getAvailabilityForDate(date);
            boolean enabled = !DateTimeUtils.isDaysPast(date);
            final AvailableHoursWithDateView view = new AvailableHoursWithDateView(getContext(),
                    date, availability, mRemoveTimeSlotListener, enabled);
            view.setRowPadding(mDefaultPadding, mDefaultPadding);
            view.setBackgroundResource(R.drawable.border_gray_bottom);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mDateClickListener.onDateClicked(date);
                }
            });
            addView(view);
            calendar.add(Calendar.DATE, 1);
        }
    }

    @Nullable
    public AvailableHoursWithDateView getViewForDate(final Date date) {
        for (int i = 0; i < getChildCount(); i++) {
            final AvailableHoursWithDateView view = (AvailableHoursWithDateView) getChildAt(i);
            if (view.getDate().equals(date)) {
                return view;
            }
        }
        return null;
    }

    public interface DateClickListener {
        void onDateClicked(final Date date);
    }
}
