package com.handy.portal.proavailability.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursCardView extends FrameLayout {

    private final int mWeekTitleResId;
    private final WeeklyAvailabilityTimelinesWrapper mWeeklyAvailability;
    @Nullable
    private final OnClickListener mEditClickListener;

    @BindView(R.id.date_range)
    TextView mDateRange;
    @BindView(R.id.week_title)
    TextView mWeekTitle;
    @BindView(R.id.timelines)
    ViewGroup mTimelines;
    @BindView(R.id.edit_button)
    TextView mEditButton;
    @BindDimen(R.dimen.default_padding_quarter)
    int mDefaultPaddingQuarter;
    @BindDimen(R.dimen.small_text_size)
    float mSmallTextSize;

    public WeeklyAvailableHoursCardView(
            @NonNull final Context context,
            @StringRes final int weekTitleResId,
            @NonNull final WeeklyAvailabilityTimelinesWrapper weeklyAvailability,
            @Nullable final OnClickListener editClickListener
    ) {
        super(context);
        mWeekTitleResId = weekTitleResId;
        mWeeklyAvailability = weeklyAvailability;
        mEditClickListener = editClickListener;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_available_hours_card, this);
        ButterKnife.bind(this);

        final String startDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeeklyAvailability.getStartDate());
        final String endDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeeklyAvailability.getEndDate());
        mDateRange.setText(startDateFormatted + " - " + endDateFormatted);

        mWeekTitle.setText(mWeekTitleResId);

        final Calendar calendar = Calendar.getInstance(Locale.US);
        final Date startDate = mWeeklyAvailability.getStartDate();
        final Date endDate = mWeeklyAvailability.getEndDate();
        calendar.setTime(startDate);
        while (DateTimeUtils.daysBetween(calendar.getTime(), endDate) >= 0) {
            final Date date = calendar.getTime();
            final AvailableHoursWithDateStaticView view =
                    new AvailableHoursWithDateStaticView(getContext(), date,
                            mWeeklyAvailability.getAvailabilityForDate(date));
            view.setRowPadding(0, mDefaultPaddingQuarter);
            view.setTitleSize(mSmallTextSize);
            mTimelines.addView(view);
            calendar.add(Calendar.DATE, 1);
        }

        mEditButton.setOnClickListener(mEditClickListener);
    }

    @Nullable
    public AvailableHoursWithDateStaticView getViewForDate(final Date date) {
        for (int i = 0; i < mTimelines.getChildCount(); i++) {
            final AvailableHoursWithDateStaticView view =
                    (AvailableHoursWithDateStaticView) mTimelines.getChildAt(i);
            if (view.getDate().equals(date)) {
                return view;
            }
        }
        return null;
    }
}
