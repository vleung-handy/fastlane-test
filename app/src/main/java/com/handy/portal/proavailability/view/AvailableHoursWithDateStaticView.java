package com.handy.portal.proavailability.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.proavailability.model.DailyAvailabilityTimeline;

import java.util.Date;

import butterknife.BindColor;

public class AvailableHoursWithDateStaticView extends AvailableHoursWithDateView {

    @BindColor(R.color.tertiary_gray)
    int mGrayColor;
    @BindColor(R.color.error_red)
    int mRedColor;

    public AvailableHoursWithDateStaticView(
            final Context context,
            final Date date,
            @Nullable final DailyAvailabilityTimeline availability) {
        super(context, date, availability, null, !DateTimeUtils.isDaysPast(date));
    }

    @Override
    public void updateTimelines(final DailyAvailabilityTimeline availability) {
        super.updateTimelines(availability);
        if (mEnabled && (availability == null || !availability.hasIntervals())) {
            mTimelines.removeAllViews();
            final TextView textView = new TextView(getContext());
            textView.setText(R.string.no_hours_set);
            textView.setTextColor(mRedColor);
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            mTimelines.addView(textView);
        }
        else if (!mEnabled) {
            mTimelines.removeAllViews();
            final TextView textView = new TextView(getContext());
            textView.setText(R.string.no_data);
            textView.setTypeface(FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK));
            mTimelines.addView(textView);
        }
    }
}
