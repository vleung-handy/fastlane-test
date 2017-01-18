package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabWithDateRangeView extends FrameLayout
{
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.date_range)
    TextView mDateRange;

    private final int mTitleResId;
    private final WeeklyAvailabilityTimelinesWrapper mWeekTimelines;

    public TabWithDateRangeView(final Context context,
                                @StringRes final int titleResId,
                                final WeeklyAvailabilityTimelinesWrapper weekTimelines)
    {
        super(context);
        mTitleResId = titleResId;
        mWeekTimelines = weekTimelines;
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_tab_with_date_range, this);
        ButterKnife.bind(this);
        mTitle.setText(mTitleResId);
        final String startDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeekTimelines.getStartDate());
        final String endDateFormatted =
                DateTimeUtils.formatDateMonthDay(mWeekTimelines.getEndDate());
        mDateRange.setText(startDateFormatted + " - " + endDateFormatted);
    }
}
