package com.handy.portal.availability.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeeklyAvailableHoursCardView extends FrameLayout {

    private final int mWeekTitleResId;
    private final Availability.Range mWeeklyAvailability;
    @Nullable
    private final EditListener mEditListener;
    private final int mCardIndex;

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
            @NonNull final Availability.Range weeklyAvailability,
            @NonNull final EditListener editClickListener,
            final int cardIndex
    ) {
        super(context);
        mWeekTitleResId = weekTitleResId;
        mWeeklyAvailability = weeklyAvailability;
        mEditListener = editClickListener;
        mCardIndex = cardIndex;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.element_available_hours_card, this);
        ButterKnife.bind(this);

        final String startDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeeklyAvailability.getStartDate());
        final String endDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeeklyAvailability.getEndDate());
        mDateRange.setText(getContext().getString(R.string.dash_formatted,
                startDateFormatted, endDateFormatted));

        mWeekTitle.setText(mWeekTitleResId);

        final Calendar calendar = Calendar.getInstance(Locale.US);
        final Date startDate = mWeeklyAvailability.getStartDate();
        final Date endDate = mWeeklyAvailability.getEndDate();
        calendar.setTime(startDate);
        while (DateTimeUtils.daysBetween(calendar.getTime(), endDate) >= 0) {
            final Date date = calendar.getTime();
            final AvailableHoursWithDateStaticView view =
                    new AvailableHoursWithDateStaticView(getContext(), date,
                            mWeeklyAvailability.getTimelineForDate(date));
            view.setRowPadding(0, mDefaultPaddingQuarter);
            view.setTitleSize(mSmallTextSize);
            mTimelines.addView(view);
            calendar.add(Calendar.DATE, 1);
        }

        mEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mEditListener.onEdit(mCardIndex);
            }
        });
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

    public interface EditListener {
        void onEdit(int cardIndex);
    }
}
