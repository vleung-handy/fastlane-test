package com.handy.portal.availability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursView extends LinearLayout {
    private final Availability.Range mWeeklyAvailability;
    private final DateClickListener mDateClickListener;

    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;

    public WeeklyAvailableHoursView(
            final Context context,
            final Availability.Range weeklyAvailability,
            final DateClickListener dateClickListener) {
        super(context);
        mWeeklyAvailability = weeklyAvailability;
        mDateClickListener = dateClickListener;
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
            final Availability.Timeline timeline = mWeeklyAvailability.getTimelineForDate(date);
            boolean enabled = !DateTimeUtils.isDaysPast(date);
            final AvailableHoursWithDateView view = new AvailableHoursWithDateView(getContext(),
                    date, timeline, enabled);
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
