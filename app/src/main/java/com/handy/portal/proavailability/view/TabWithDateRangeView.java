package com.handy.portal.proavailability.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.proavailability.model.WeeklyAvailabilityTimelinesWrapper;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabWithDateRangeView extends FrameLayout {
    private final Date mStartDate;
    private final Date mEndDate;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.date_range)
    TextView mDateRange;

    private final int mTitleResId;

    public TabWithDateRangeView(final Context context,
                                @StringRes final int titleResId,
                                final WeeklyAvailabilityTimelinesWrapper weekTimelines) {
        super(context);
        mTitleResId = titleResId;
        mStartDate = weekTimelines.getStartDate();
        mEndDate = weekTimelines.getEndDate();
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tab_with_date_range, this);
        ButterKnife.bind(this);
        mTitle.setText(mTitleResId);
        final String startDateFormatted = DateTimeUtils.formatDateMonthDay(mStartDate);
        final String endDateFormatted = DateTimeUtils.formatDateMonthDay(mEndDate);
        mDateRange.setText(getContext().getString(R.string.dash_formatted,
                startDateFormatted, endDateFormatted));
    }
}
