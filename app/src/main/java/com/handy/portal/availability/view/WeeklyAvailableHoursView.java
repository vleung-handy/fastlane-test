package com.handy.portal.availability.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Date;

import butterknife.BindDimen;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursView extends LinearLayout {
    private final Availability.Range mWeekRange;
    private final DateClickListener mDateClickListener;

    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;

    public WeeklyAvailableHoursView(
            final Context context,
            final Availability.Range weekRange,
            final DateClickListener dateClickListener) {
        super(context);
        mWeekRange = weekRange;
        mDateClickListener = dateClickListener;
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        for (final Date date : mWeekRange.dates()) {
            boolean enabled = !DateTimeUtils.isDaysPast(date);
            final AvailableHoursWithDateView view = new AvailableHoursWithDateView(
                    getContext(), date, enabled
            );
            view.setRowPadding(mDefaultPadding, mDefaultPadding);
            view.setBackgroundResource(R.drawable.border_gray_bottom);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mDateClickListener.onDateClicked(date);
                }
            });
            addView(view);
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
